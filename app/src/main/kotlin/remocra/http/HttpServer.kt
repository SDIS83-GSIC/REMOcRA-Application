package remocra.http

import jakarta.inject.Inject
import jakarta.servlet.DispatcherType.ASYNC
import jakarta.servlet.DispatcherType.FORWARD
import jakarta.servlet.DispatcherType.REQUEST
import jakarta.servlet.MultipartConfigElement
import jakarta.servlet.ServletContext
import jakarta.servlet.SessionTrackingMode
import org.eclipse.jetty.ee10.servlet.DefaultServlet
import org.eclipse.jetty.ee10.servlet.ErrorPageErrorHandler
import org.eclipse.jetty.ee10.servlet.FilterHolder
import org.eclipse.jetty.ee10.servlet.ResourceServlet
import org.eclipse.jetty.ee10.servlet.ServletContextHandler
import org.eclipse.jetty.ee10.servlet.ServletContextHandler.SESSIONS
import org.eclipse.jetty.ee10.servlet.ServletHolder
import org.eclipse.jetty.server.ForwardedRequestCustomizer
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.QoSHandler
import org.eclipse.jetty.session.DefaultSessionCache
import org.eclipse.jetty.session.FileSessionDataStore
import org.jboss.resteasy.core.ResteasyDeploymentImpl
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters
import org.jboss.resteasy.spi.ResteasyDeployment
import org.jboss.resteasy.spi.ResteasyProviderFactory
import org.pac4j.jee.filter.CallbackFilter
import org.pac4j.jee.filter.LogoutFilter
import org.pac4j.jee.filter.SecurityFilter
import remocra.GlobalConstants
import remocra.auth.AuthnConstants
import remocra.auth.UserInfoFilter
import remocra.healthcheck.HealthServlet
import remocra.resteasy.GuiceInjectorFactory
import remocra.security.CsrfServletFilter
import remocra.security.SecurityHeadersFilter
import remocra.web.JaxrsApplication
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

        // QoS
        val qosHandler = QoSHandler()
        qosHandler.maxRequestCount = settings.qosMaxRequests
        qosHandler.maxSuspendedRequestCount = settings.qosMaxSuspendedRequests
        qosHandler.maxSuspend = settings.qosMaxSuspend
        server.handler = qosHandler

        // Servlets
        val context = ServletContextHandler(SESSIONS)

        val tmpdir = Files.createTempDirectory(settings.tempDirPrefix).toFile()
        tmpdir.deleteOnExit()
        context.setAttribute(ServletContext.TEMPDIR, tmpdir)
        context.setBaseResourceAsPath(settings.staticDir)
        context.errorHandler = ErrorPageErrorHandler()
        qosHandler.handler = context

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
        sessionHandler.isHttpOnly = true

        context.addFilter(SecurityHeadersFilter::class.java, "/*", null)

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
        resteasy.registration.setMultipartConfig(
            MultipartConfigElement(
                settings.multipartTempDir,
                settings.multipartMaxFileSize,
                settings.multipartMaxRequestSize,
                settings.multipartFileSizeThreshold,
            ),
        )
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

        // Servlets par défaut pour les ressources statiques
        context.addServlet(ResourceServlet::class.java, "/favicon.ico")
        context.addServlet(
            ServletHolder(AuthnConstants.IMAGES_SERVLET_NAME, ResourceServlet::class.java).apply {
                setInitParameter("dirAllowed", "false")
                setInitParameter("baseResource", GlobalConstants.DOSSIER_IMAGES_RESSOURCES)
                setInitParameter("pathInfoOnly", "true")
            },
            "/images/*",
        )
        context.addServlet(
            ServletHolder(
                AuthnConstants.DEFAULT_SERVLET_NAME,
                DefaultServlet::class.java,
            ).apply {
                setInitParameter("dirAllowed", "false")
            },
            "/",
        )

        // On veut attacher le SpaFilter uniquement à la AuthnConstants.DEFAULT_SERVLET_NAME
        FilterHolder(SpaFilter::class.java).apply {
            context.servletHandler.addFilter(this)
            registration.addMappingForServletNames(null, false, AuthnConstants.DEFAULT_SERVLET_NAME)
        }

        server.start()
    }
    fun waitTillInterrupt() = server.join()
}
