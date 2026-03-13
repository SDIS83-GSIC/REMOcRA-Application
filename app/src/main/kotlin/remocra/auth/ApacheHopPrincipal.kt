package remocra.auth

import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse
import net.ltgt.oauth.common.SimpleTokenPrincipal

class ApacheHopPrincipal(
    tokenInfo: TokenIntrospectionSuccessResponse,
) : SimpleTokenPrincipal(tokenInfo)
