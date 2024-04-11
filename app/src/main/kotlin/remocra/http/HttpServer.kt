package remocra.http

import com.google.common.net.HttpHeaders
import jakarta.inject.Inject
import jakarta.servlet.DispatcherType.ASYNC
import jakarta.servlet.DispatcherType.FORWARD
import jakarta.servlet.DispatcherType.REQUEST
import jakarta.servlet.MultipartConfigElement
import jakarta.servlet.ServletContext
import jakarta.servlet.SessionTrackingMode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.ForwardedRequestCustomizer
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.ResourceService
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.session.DefaultSessionCache
import org.eclipse.jetty.server.session.FileSessionDataStore
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ErrorPageErrorHandler
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlets.QoSFilter
import org.eclipse.jetty.util.resource.PathResource
import org.jboss.resteasy.core.ResteasyDeploymentImpl
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters
import org.jboss.resteasy.spi.ResteasyDeployment
import org.jboss.resteasy.spi.ResteasyProviderFactory
import org.pac4j.jee.filter.CallbackFilter
import org.pac4j.jee.filter.LogoutFilter
import org.pac4j.jee.filter.SecurityFilter
import remocra.authn.AuthnConstants
import remocra.authn.UserInfoFilter
import remocra.healthcheck.HealthServlet
import remocra.resteasy.GuiceInjectorFactory
import remocra.security.CsrfServletFilter
import remocra.web.JaxrsApplication
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.EnumSet

class HttpServer
@Inject
constructor(
    private val settings: HttpSettings,
    private val guiceInjectorFactory: GuiceInjectorFactory,
    private val application: JaxrsApplication,
    private val healthServlet: HealthServlet,
    private val userInfoFilter: UserInfoFilter,
    private val callbackFilter: CallbackFilter,
    private val logoutFilter: LogoutFilter,
    private val securityFilter: SecurityFilter,
) {
    private lateinit var server: Server

    fun start() {
        server = Server()
        server.stopAtShutdown = true
        server.stopTimeout = settings.gracefulStopTime.toLong()

        // Connector, avec support pour X-Forwarded-Proto et al.
        val httpConnector = ServerConnector(server)
        server.addConnector(httpConnector)
        httpConnector.idleTimeout = settings.idleTimeout.toLong()
        httpConnector.port = settings.port
        val httpConfiguration = HttpConfiguration()
        httpConfiguration.addCustomizer(ForwardedRequestCustomizer())
        httpConfiguration.sendServerVersion = false
        httpConfiguration.sendXPoweredBy = false
        httpConnector.addConnectionFactory(HttpConnectionFactory(httpConfiguration))

        // Servlets
        val context = ServletContextHandler(SESSIONS)

        val tmpdir = Files.createTempDirectory(settings.tempDirPrefix).toFile()
        tmpdir.deleteOnExit()
        context.setAttribute(ServletContext.TEMPDIR, tmpdir)
        context.baseResource = PathResource(settings.staticDir)
        context.errorHandler = ErrorPageErrorHandler()
        server.handler = context

        val sessionHandler = context.sessionHandler
        if (settings.sessionStoreDir != null) {
            sessionHandler.sessionCache =
                DefaultSessionCache(sessionHandler).apply {
                    sessionDataStore =
                        FileSessionDataStore().apply {
                            this.storeDir = settings.sessionStoreDir.apply {
                                mkdir()
                            }
                        }
                }
        }
        sessionHandler.setSessionTrackingModes(setOf(SessionTrackingMode.COOKIE))
        sessionHandler.sessionCookie = settings.sessionCookieName
        sessionHandler.maxInactiveInterval = settings.sessionMaxIdleTime.seconds.toInt()
        sessionHandler.httpOnly = true

        // QoS
        val qosFilterHolder = FilterHolder(QoSFilter::class.java)
        qosFilterHolder.setInitParameter("maxRequests", settings.qosMaxRequests)
        qosFilterHolder.setInitParameter("waitMS", settings.qosWaitMS)
        qosFilterHolder.setInitParameter("suspendMS", settings.qosSuspendMS)
        context.addFilter(qosFilterHolder, "/*", null)

        // CSRF
        // DispatcherType.FORWARD permet de matcher l'index.html pour une requête sur "/"
        context.addFilter(
            CsrfServletFilter::class.java,
            "*.html",
            EnumSet.of(REQUEST, ASYNC, FORWARD),
        )

        context.addFilter(
            FilterHolder(userInfoFilter),
            "/index.html",
            EnumSet.of(REQUEST, ASYNC, FORWARD),
        )

        // Securité
        context.addFilter(FilterHolder(callbackFilter), AuthnConstants.CALLBACK_PATH, null)
        context.addFilter(FilterHolder(logoutFilter), AuthnConstants.LOGOUT_PATH, null)
        context.addFilter(FilterHolder(securityFilter), "/*", null)

        // Resteasy
        val resteasy = ServletHolder(AuthnConstants.API_SERVLET_NAME, HttpServlet30Dispatcher::class.java)
        resteasy.setInitParameter(
            ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX,
            AuthnConstants.API_PATH,
        )
        // Pour avoir useGlobal = false dans ServletContainerDispatcher.init
        resteasy.setInitParameter("resteasy.servlet.context.deployment", "false")
        resteasy.registration.setMultipartConfig(MultipartConfigElement("./tmp"))
        context.addServlet(resteasy, AuthnConstants.API_PATH + "*")

        // Resteasy + Guice
        val providerFactory = ResteasyProviderFactory.getInstance()
        providerFactory.injectorFactory = guiceInjectorFactory
        val deployment = ResteasyDeploymentImpl()
        deployment.providerFactory = providerFactory
        deployment.application = application
        context.setAttribute(ResteasyDeployment::class.java.name, deployment)

        context.addServlet(
            ServletHolder(AuthnConstants.HEALTH_SERVLET_NAME, healthServlet),
            HealthServlet.PATH,
        )

        // Servlet par défaut pour les ressources statiques
        context.addServlet(DefaultServlet::class.java, "/favicon.ico")

        val resource =
            ServletHolder(
                AuthnConstants.DEFAULT_SERVLET_NAME,
                DefaultServlet(
                    object : ResourceService() {
                        override fun doGet(
                            request: HttpServletRequest?,
                            response: HttpServletResponse?,
                        ): Boolean {
                            response?.characterEncoding = StandardCharsets.UTF_8.name()
                            return super.doGet(request, response)
                        }

                        override fun notFound(
                            request: HttpServletRequest,
                            response: HttpServletResponse,
                        ) {
                            // Pour utilisation avec history.pushState: renvoie l'index pour tout ce
                            // qui n'est pas reconnu.
                            // XXX: filtrer ce qui n'a pas d'extension (et/ou .html) uniquement ?
                            response.setHeader(HttpHeaders.CONTENT_LOCATION, "/")
                            request.getRequestDispatcher("/").forward(request, response)
                        }
                    },
                ),
            ).apply {
                setInitParameter("dirAllowed", "false")
            }

        context.addServlet(resource, "/*")

        server.start()
    }
    fun waitTillInterrupt() = server.join()
}
