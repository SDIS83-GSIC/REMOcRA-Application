package remocra.auth

import com.github.benmanes.caffeine.cache.Caffeine
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse
import jakarta.inject.Inject
import jakarta.inject.Provider
import net.ltgt.oauth.common.CachedTokenPrincipalProvider
import net.ltgt.oauth.common.KeycloakTokenPrincipal
import net.ltgt.oauth.common.TokenPrincipal
import remocra.data.enums.TypeSourceModification
import remocra.db.DroitsRepository
import remocra.db.OrganismeRepository
import remocra.db.ProfilDroitRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit

class MobileUserPrincipalProvider @Inject constructor(
    private val utilisateurRepository: Provider<UtilisateurRepository>,
    private val droitsRepository: Provider<DroitsRepository>,
    private val organismeRepository: Provider<OrganismeRepository>,
    private val profilDroitRepository: ProfilDroitRepository,
    authnSettings: AuthModule.AuthnSettings,
) : CachedTokenPrincipalProvider(Caffeine.from(authnSettings.tokenIntrospectionCacheSpec)) {
    override fun load(introspectionResponse: TokenIntrospectionSuccessResponse): TokenPrincipal? {
        // XXX: utiliser syncUtilisateur ?
        val utilisateur = utilisateurRepository.get().getUtilisateurByUsername(introspectionResponse.username)
            ?: return null

        // XXX: factoriser avec RemocraUserPrincipalFactory

        // On remplit ses droits
        val droits = if (utilisateur.utilisateurIsSuperAdmin == true) {
            Droit.entries.toSet()
        } else {
            droitsRepository.get().getDroitsFromUser(utilisateur.utilisateurId)
        }

        // zone de compétence
        val zoneCompetence = utilisateur.utilisateurOrganismeId?.let {
            utilisateurRepository.get().getZoneByOrganismeId(it)
        }

        // On remplit ses organismes affiliés
        val affiliatedOrganismeIds = utilisateur.utilisateurOrganismeId?.let {
            organismeRepository.get().getOrganismeAndChildren(it).toSet()
        } ?: organismeRepository.get().getAll().map { it.id }.toSet()

        val profilDroit = profilDroitRepository.getProfilDroitByUtilisateurId(utilisateur.utilisateurId)

        return object : KeycloakTokenPrincipal(introspectionResponse), RemocraUserPrincipal {
            override fun getName() = super<RemocraUserPrincipal>.getName()
            override val userInfo = UserInfo(utilisateur, droits, zoneCompetence, affiliatedOrganismeIds, profilDroit, TypeSourceModification.MOBILE)
        }
    }
}
