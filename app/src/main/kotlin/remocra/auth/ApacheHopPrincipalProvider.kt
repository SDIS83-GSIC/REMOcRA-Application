import com.github.benmanes.caffeine.cache.Caffeine
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.ltgt.oauth.common.CachedTokenPrincipalProvider
import net.ltgt.oauth.common.TokenPrincipal
import remocra.auth.ApacheHopPrincipal
import remocra.auth.AuthModule

@Singleton
class ApacheHopPrincipalProvider @Inject constructor(
    authnSettings: AuthModule.AuthnSettings,
) : CachedTokenPrincipalProvider(Caffeine.from(authnSettings.tokenIntrospectionCacheSpec)) {
    override fun load(introspectionResponse: TokenIntrospectionSuccessResponse): TokenPrincipal {
        return ApacheHopPrincipal(introspectionResponse)
    }
}
