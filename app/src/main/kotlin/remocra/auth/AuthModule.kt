package remocra.auth

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.inject.BindingAnnotation
import com.google.inject.Provides
import com.nimbusds.oauth2.sdk.GeneralException
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic
import com.nimbusds.oauth2.sdk.auth.Secret
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.Issuer
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata
import com.nimbusds.openid.connect.sdk.op.ReadOnlyOIDCProviderMetadata
import com.typesafe.config.Config
import jakarta.inject.Singleton
import net.ltgt.oauth.common.TokenIntrospector
import net.ltgt.oidc.servlet.Configuration
import okhttp3.HttpUrl
import remocra.RemocraModule
import remocra.web.registerResources
import java.io.IOException
import kotlin.jvm.Throws

class AuthModule(
    private val settings: AuthnSettings,
    private val oidcProviderMetadata: ReadOnlyOIDCProviderMetadata,
) : RemocraModule() {
    override fun configure() {
        binder().registerResources(AuthenticationFeature::class, AuthorizationFeature::class)
        bind(AuthnSettings::class.java).toInstance(settings)
    }

    companion object {
        @Throws(GeneralException::class, IOException::class)
        fun loadOidcProviderMetadata(config: Config): OIDCProviderMetadata =
            OIDCProviderMetadata.resolve(
                Issuer(
                    HttpUrl.get(config.getString("base-uri"))
                        .newBuilder()
                        .addPathSegment("realms")
                        .addPathSegment(config.getString("realm"))
                        .build()
                        .uri(),
                ),
            )

        fun create(config: Config, oidcProviderMetadata: ReadOnlyOIDCProviderMetadata) =
            AuthModule(
                AuthnSettings(
                    config.getString("client-id"),
                    config.getString("client-secret"),
                    config.getString("base-uri"),
                    config.getString("realm"),
                    config.getString("token-introspection-cache-spec"),
                    config.getString("client-id-apache-hop"),
                    config.getString("client-secret-apache-hop"),
                ),
                oidcProviderMetadata,
            )
    }

    @Provides
    @Singleton
    fun provideOidcConfiguration(): Configuration {
        return Configuration(
            oidcProviderMetadata,
            ClientSecretBasic(
                ClientID(settings.clientId),
                Secret(settings.clientSecret),
            ),
        )
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
            oidcProviderMetadata,
            ClientSecretBasic(
                ClientID(settings.clientId),
                Secret(settings.clientSecret),
            ),
            Caffeine.from(settings.tokenIntrospectionCacheSpec),
        )
    }

    @Provides
    @Singleton // contient un cache mémoire
    @ApacheHopToken
    fun provideApacheHopTokenIntrospector(): TokenIntrospector {
        return TokenIntrospector(
            oidcProviderMetadata,
            ClientSecretBasic(
                ClientID(settings.clientIdApacheHop),
                Secret(settings.clientSecreteApacheHop),
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
        val clientIdApacheHop: String,
        val clientSecreteApacheHop: String,
    )

    /**
     * @param clientId
     * @param clientSecret
     */
    class KeycloakClient(val clientId: String, val clientSecret: String)
}

/**
 * Annotation permettant au moteur d'injection de distinguer le client connecté
 */
@BindingAnnotation
annotation class ApacheHopToken
