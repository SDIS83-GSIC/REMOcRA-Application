package remocra.auth

import com.github.benmanes.caffeine.cache.Caffeine
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse
import jakarta.inject.Inject
import jakarta.inject.Provider
import jakarta.inject.Singleton
import net.ltgt.oauth.common.CachedTokenPrincipalProvider
import net.ltgt.oauth.common.TokenPrincipal
import remocra.db.OrganismeRepository
import remocra.db.TypeOrganismeRepository

@Singleton
class OrganismePrincipalProvider @Inject constructor(
    private val organismeRepository: Provider<OrganismeRepository>,
    private val typeOrganismeRepository: Provider<TypeOrganismeRepository>,
    authnSettings: AuthModule.AuthnSettings,
) : CachedTokenPrincipalProvider(Caffeine.from(authnSettings.tokenIntrospectionCacheSpec)) {
    override fun load(introspectionResponse: TokenIntrospectionSuccessResponse): TokenPrincipal? {
        // L'id de client doit correspondre à l'adresse mail, attention elle doit être unique !
        return organismeRepository.get().getByEmail(introspectionResponse.clientID!!.value)
            ?.let { organisme ->
                val droits = organismeRepository.get().getDroitApi(typeOrganismeId = organisme.organismeTypeOrganismeId)
                return@let OrganismePrincipal(
                    introspectionResponse,
                    OrganismeInfo(
                        organisme.organismeId,
                        organisme.organismeLibelle,
                        droits,
                        typeOrganismeRepository.get().getByOrganismeId(organisme.organismeId),
                    ),
                )
            }
    }
}
