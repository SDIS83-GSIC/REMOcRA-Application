package remocra.auth

import jakarta.inject.Inject
import jakarta.servlet.http.HttpSession
import net.ltgt.oidc.servlet.KeycloakUserPrincipal
import net.ltgt.oidc.servlet.SessionInfo
import net.ltgt.oidc.servlet.UserPrincipal
import net.ltgt.oidc.servlet.UserPrincipalFactory
import remocra.data.enums.TypeSourceModification
import remocra.db.DroitsRepository
import remocra.db.OrganismeRepository
import remocra.db.ProfilDroitRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import java.util.UUID

class RemocraUserPrincipalFactory @Inject constructor(
    private val utilisateurRepository: UtilisateurRepository,
    private val droitsRepository: DroitsRepository,
    private val organismeRepository: OrganismeRepository,
    private val profilDroitRepository: ProfilDroitRepository,
) : UserPrincipalFactory {

    companion object {
        private val USER_INFO_SESSION_ATTRIBUTE_NAME = UserInfo::class.qualifiedName
    }

    override fun createUserPrincipal(sessionInfo: SessionInfo, session: HttpSession): UserPrincipal {
        return object : KeycloakUserPrincipal(sessionInfo), RemocraUserPrincipal {
            override fun getName() = super<RemocraUserPrincipal>.getName()
            override val userInfo = session.getAttribute(USER_INFO_SESSION_ATTRIBUTE_NAME) as UserInfo
        }
    }

    override fun userAuthenticated(sessionInfo: SessionInfo, session: HttpSession) {
        val utilisateur = utilisateurRepository.syncUtilisateur(
            id = UUID.fromString(sessionInfo.userInfo.subject.value),
            nom = sessionInfo.userInfo.familyName,
            prenom = sessionInfo.userInfo.givenName,
            email = sessionInfo.userInfo.emailAddress,
            username = sessionInfo.userInfo.preferredUsername,
            // utilise KeycloakUserPrincipal pour simplifier le calcul des rôles
            actif = !KeycloakUserPrincipal(sessionInfo).hasRole("inactif"),
        )

        // XXX: factoriser avec MobileUserPrincipalProvider

        // On remplit ses droits
        val droits = if (utilisateur.utilisateurIsSuperAdmin == true) {
            Droit.entries.toSet()
        } else {
            droitsRepository.getDroitsFromUser(utilisateur.utilisateurId)
        }

        // zone de compétence
        val zoneCompetence = utilisateur.utilisateurOrganismeId?.let {
            utilisateurRepository.getZoneByOrganismeId(it)
        }

        // On remplit ses organismes affiliés
        val affiliatedOrganismeIds = utilisateur.utilisateurOrganismeId?.let {
            organismeRepository.getOrganismeAndChildren(it).toSet()
        } ?: organismeRepository.getAll().map { it.id }.toSet()

        val profilDroit = profilDroitRepository.getProfilDroitByUtilisateurId(utilisateur.utilisateurId)

        session.setAttribute(
            USER_INFO_SESSION_ATTRIBUTE_NAME,
            UserInfo(utilisateur, droits, zoneCompetence, affiliatedOrganismeIds, profilDroit, TypeSourceModification.REMOCRA_WEB),
        )
    }
}
