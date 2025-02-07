package remocra.auth

import com.github.benmanes.caffeine.cache.Caffeine
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse
import jakarta.inject.Inject
import jakarta.inject.Provider
import net.ltgt.oauth.common.CachedTokenPrincipalProvider
import net.ltgt.oauth.common.KeycloakTokenPrincipal
import net.ltgt.oauth.common.TokenPrincipal
import remocra.db.DroitsRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.utilisateur.UtilisateurOrganismesUseCase
import java.util.UUID

class MobileUserPrincipalProvider @Inject constructor(
    private val utilisateurRepository: Provider<UtilisateurRepository>,
    private val droitsRepository: Provider<DroitsRepository>,
    private val utilisateurOrganismesUseCase: Provider<UtilisateurOrganismesUseCase>,
    authnSettings: AuthModule.AuthnSettings,
) : CachedTokenPrincipalProvider(Caffeine.from(authnSettings.tokenIntrospectionCacheSpec)) {
    override fun load(introspectionResponse: TokenIntrospectionSuccessResponse): TokenPrincipal? {
        // XXX: utiliser syncUtilisateur ?
        val utilisateur =
            utilisateurRepository.get().getUtilisateurById(UUID.fromString(introspectionResponse.subject!!.value))
                ?: return null

        val userInfo = UserInfo()
        userInfo.id = introspectionResponse.subject!!.value
        userInfo.addAttribute("preferred_username", utilisateur.utilisateurUsername)
        userInfo.addAttribute("given_name", utilisateur.utilisateurPrenom)
        userInfo.addAttribute("family_name", utilisateur.utilisateurNom)
        userInfo.addAttribute("email", utilisateur.utilisateurEmail)
        userInfo.utilisateur = utilisateur

        // XXX: factoriser avec SyncProfileAuthorizationGenerator

        // On remplit ses droits
        userInfo.droits = if (utilisateur.utilisateurIsSuperAdmin == true) {
            Droit.entries.toSet()
        } else {
            droitsRepository.get().getDroitsFromUser(userInfo.utilisateurId)
        }

        if (userInfo.utilisateur.utilisateurOrganismeId != null) {
            // zone de compétence
            userInfo.zoneCompetence =
                utilisateurRepository.get().getZoneByOrganismeId(utilisateur.utilisateurOrganismeId!!)
        }

        // On remplit ses organismes affiliés
        utilisateurOrganismesUseCase.get().execute(userInfo)

        return object : KeycloakTokenPrincipal(introspectionResponse), RemocraUserPrincipal {
            override fun getName() = super<RemocraUserPrincipal>.getName()
            override val userInfo = userInfo
        }
    }
}
