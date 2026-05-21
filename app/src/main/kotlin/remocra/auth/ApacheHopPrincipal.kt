package remocra.auth

import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse
import net.ltgt.oauth.common.SimpleTokenPrincipal

class ApacheHopPrincipal(
    tokenInfo: TokenIntrospectionSuccessResponse,
    val apacheHopInfo: ApacheHopInfo,
) : SimpleTokenPrincipal(tokenInfo) {
    override fun getName(): String = "Apache HOP"
}
