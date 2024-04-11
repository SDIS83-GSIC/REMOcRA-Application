package remocra.authn

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Provides
import com.google.inject.Singleton
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
import com.typesafe.config.Config
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers
import org.pac4j.core.context.CallContext
import org.pac4j.core.exception.http.HttpAction
import org.pac4j.core.http.ajax.AjaxRequestResolver
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver
import org.pac4j.core.http.url.DefaultUrlResolver
import org.pac4j.core.matching.matcher.DefaultMatchers
import org.pac4j.core.redirect.RedirectionActionBuilder
import org.pac4j.core.util.HttpActionHelper
import org.pac4j.core.util.Pac4jConstants
import org.pac4j.jee.context.JEEContext
import org.pac4j.jee.filter.CallbackFilter
import org.pac4j.jee.filter.LogoutFilter
import org.pac4j.jee.filter.SecurityFilter
import org.pac4j.oidc.client.KeycloakOidcClient
import org.pac4j.oidc.config.KeycloakOidcConfiguration
import org.pac4j.oidc.profile.OidcProfileDefinition
import org.pac4j.oidc.profile.creator.OidcProfileCreator
import remocra.web.registerResources
import java.net.MalformedURLException

class AuthnModule(private val settings: AuthnSettings) : AbstractModule() {
    override fun configure() {
        binder().registerResources()
        bind(AuthnSettings::class.java).toInstance(settings)
    }

    companion object {
        fun create(config: Config) =
            AuthnModule(
                AuthnSettings(
                    config.getString("client-id"),
                    config.getString("client-secret"),
                    config.getString("base-uri"),
                    config.getString("realm"),
                ),
            )

        fun excludeStaticResources(
            path: String,
            servletName: String,
            getResourcePath: (path: String) -> String?,
        ): Boolean {
            if (path == "/favicon.ico") {
                return false
            }

            if (AuthnConstants.HEALTH_SERVLET_NAME == servletName) {
                return false
            }
            // Si la requête ne matche pas la DefaultServlet, elle doit être authentifiée
            if (AuthnConstants.DEFAULT_SERVLET_NAME != servletName) {
                return true
            }

            val resourcePath: String? = try {
                getResourcePath(path)
            } catch (e: MalformedURLException) {
                return true // dans le doute, on requiert l'authentification
            }

            return resourcePath == null ||
                resourcePath.endsWith("/") ||
                resourcePath.endsWith("/index.html")
        }
    }

    @Provides
    @Singleton
    @Inject
    fun providePac4jConfig(
        syncProfileAuthorizationGenerator: SyncProfileAuthorizationGenerator,
        settings: AuthnSettings,
    ): org.pac4j.core.config.Config {
        val keycloakConfig = KeycloakOidcConfiguration()
        keycloakConfig.clientId = settings.clientId
        keycloakConfig.secret = settings.clientSecret
        keycloakConfig.baseUri = settings.baseUri
        keycloakConfig.realm = settings.realm
        val client = KeycloakOidcClient(keycloakConfig)
        client.urlResolver = DefaultUrlResolver(true)
        client.callbackUrl = AuthnConstants.CALLBACK_PATH
        client.callbackUrlResolver = NoParameterCallbackUrlResolver()
        val profileCreator = OidcProfileCreator(client.configuration, client)
        profileCreator.profileDefinition = OidcProfileDefinition { UserInfo() }
        keycloakConfig.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        client.profileCreator = profileCreator
        client.addAuthorizationGenerator(syncProfileAuthorizationGenerator)
        object : AjaxRequestResolver {

            override fun buildAjaxResponse(
                context: CallContext?,
                redirectionActionBuilder: RedirectionActionBuilder?,
            ): HttpAction? = HttpActionHelper.buildUnauthenticatedAction(context?.webContext)

            override fun isAjax(context: CallContext?): Boolean {
                return context?.webContext?.path?.startsWith(AuthnConstants.API_PATH) ?: false
            }
        }.also { client.ajaxRequestResolver = it }

        val config = org.pac4j.core.config.Config(client)

        config.addMatcher("excludeStaticResources") { context ->

            val request = (context.webContext as JEEContext).nativeRequest
            val servletName = request.httpServletMapping.servletName

            return@addMatcher excludeStaticResources(context.webContext.path, servletName) { path ->
                request.servletContext.getResource(path)?.path
            }
        }
        return config
    }

    @Provides
    fun provideSecurityFilter(config: org.pac4j.core.config.Config): SecurityFilter {
        // Par rapport à la configuration par défaut, désactive la protection CSRF (+ cf. matchers)
        return SecurityFilter(
            config,
            null,
            DefaultAuthorizers.IS_AUTHENTICATED,
            java.lang.String.join(
                ",", // Correspond à la configuration par SECURITY_HEADERS à l'exception de HSTS
                DefaultMatchers.NOCACHE,
                DefaultMatchers.NOSNIFF,
                DefaultMatchers.NOFRAME,
                DefaultMatchers.XSSPROTECTION,
                "excludeStaticResources",
            ),
        )
    }

    @Provides
    fun provideCallbackFilter(config: org.pac4j.core.config.Config?): CallbackFilter {
        return CallbackFilter(config)
    }

    @Provides
    fun provideLogoutFilter(config: org.pac4j.core.config.Config?): LogoutFilter {
        val logoutFilter = LogoutFilter(config, Pac4jConstants.DEFAULT_URL_VALUE)
        logoutFilter.destroy()
        logoutFilter.apply {
            destroySession = true
            centralLogout = true
        }

        return logoutFilter
    }

    data class AuthnSettings(
        val clientId: String,
        val clientSecret: String,
        val baseUri: String,
        val realm: String,
    )
}
