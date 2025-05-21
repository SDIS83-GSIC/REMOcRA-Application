package remocra.auth

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.inject.Inject
import com.google.inject.Provides
import com.google.inject.Singleton
import com.nimbusds.oauth2.sdk.`as`.AuthorizationServerMetadata
import com.nimbusds.oauth2.sdk.`as`.ReadOnlyAuthorizationServerMetadata
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic
import com.nimbusds.oauth2.sdk.auth.Secret
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.Issuer
import com.typesafe.config.Config
import net.ltgt.oauth.common.TokenIntrospector
import okhttp3.HttpUrl
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers
import org.pac4j.core.context.CallContext
import org.pac4j.core.exception.http.HttpAction
import org.pac4j.core.http.ajax.AjaxRequestResolver
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver
import org.pac4j.core.http.url.DefaultUrlResolver
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
import remocra.RemocraModule
import remocra.web.registerResources
import java.net.MalformedURLException

class AuthModule(
    private val settings: AuthnSettings,
    private val authorizationServerMetadata: ReadOnlyAuthorizationServerMetadata,
) : RemocraModule() {
    override fun configure() {
        binder().registerResources(AuthenticationFeature::class, AuthorizationFeature::class)
        bind(AuthnSettings::class.java).toInstance(settings)
    }

    companion object {
        fun create(config: Config): AuthModule {
            val uriBuilder = HttpUrl.get(config.getString("base-uri"))
                .newBuilder()
                .addPathSegment("realms")
                .addPathSegment(config.getString("realm"))
            return AuthModule(
                AuthnSettings(
                    config.getString("client-id"),
                    config.getString("client-secret"),
                    config.getString("base-uri"),
                    config.getString("realm"),
                    config.getString("token-introspection-cache-spec"),
                ),
                AuthorizationServerMetadata(Issuer(uriBuilder.build().uri())).apply {
                    introspectionEndpointURI =
                        uriBuilder
                            .addPathSegment("protocol")
                            .addPathSegment("openid-connect")
                            .addPathSegment("token")
                            .addPathSegment("introspect")
                            .build()
                            .uri()
                },
            )
        }

        fun excludeStaticResources(
            path: String,
            servletName: String,
            getResourcePath: (path: String) -> String?,
        ): Boolean {
            if (path == "/favicon.ico") {
                return false
            }
            // Les API point d'eau et API mobile gèrent leur propre authentification
            if (path.startsWith(AuthnConstants.API_DECI_PATH) || path.startsWith(AuthnConstants.API_REFERENTIEL_PATH) || path.startsWith(AuthnConstants.API_MOBILE_PATH)) {
                return false
            }

            if (path == AuthnConstants.OPENAPI_PATH || path.startsWith(AuthnConstants.OPENAPI_PATH + "/")) {
                return false
            }

            if (AuthnConstants.HEALTH_SERVLET_NAME == servletName) {
                return false
            }
            if (AuthnConstants.IMAGES_SERVLET_NAME == servletName) {
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
            "excludeStaticResources",
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

    @Provides
    fun providerKeycloakClient(): KeycloakClient {
        return KeycloakClient(
            settings.clientId,
            settings.clientSecret,
        )
    }

    @Provides
    @Singleton // contient un cache mémoire
    fun provideTokenIntrospector(): TokenIntrospector {
        return TokenIntrospector(
            authorizationServerMetadata,
            ClientSecretBasic(
                ClientID(settings.clientId),
                Secret(settings.clientSecret),
            ),
            Caffeine.from(settings.tokenIntrospectionCacheSpec),
        )
    }

    data class AuthnSettings(
        val clientId: String,
        val clientSecret: String,
        val baseUri: String,
        val realm: String,
        val tokenIntrospectionCacheSpec: String,
    )

    /**
     * @param clientId
     * @param clientSecret
     */
    class KeycloakClient(val clientId: String, val clientSecret: String)
}
