package remocra.auth

import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse
import net.ltgt.oauth.common.SimpleTokenPrincipal

class OrganismePrincipal(
    tokenInfo: TokenIntrospectionSuccessResponse,
    val organismeInfo: OrganismeInfo,
) : SimpleTokenPrincipal(tokenInfo) {
    override fun getName(): String {
        return organismeInfo.libelle
    }
}
