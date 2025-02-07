package remocra.auth

import com.google.inject.Inject
import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.context.CallContext
import org.pac4j.core.profile.UserProfile
import remocra.db.DroitsRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.utilisateur.UtilisateurOrganismesUseCase
import java.util.Optional
import java.util.UUID

class SyncProfileAuthorizationGenerator : AuthorizationGenerator {

    @Inject
    lateinit var utilisateurRepository: UtilisateurRepository

    @Inject
    lateinit var droitsRepository: DroitsRepository

    @Inject
    lateinit var utilisateurOrganismesUseCase: UtilisateurOrganismesUseCase

    override fun generate(p0: CallContext?, profile: UserProfile): Optional<UserProfile> {
        if (profile is UserInfo) {
            val userProfile: UserInfo = profile
            val utilisateur = utilisateurRepository.syncUtilisateur(
                UUID.fromString(userProfile.id),
                userProfile.familyName,
                userProfile.firstName,
                userProfile.email,
                userProfile.username,
                !profile.roles.contains("inactif"),
            )
            userProfile.utilisateur = utilisateur

            // XXX: factoriser avec MobileUserInfoProvider

            // On remplit ses droits
            userProfile.droits = if (utilisateur.utilisateurIsSuperAdmin == true) {
                Droit.entries.toSet()
            } else {
                try {
                    droitsRepository.getDroitsFromUser(userProfile.utilisateurId)
                } catch (nsee: NoSuchElementException) {
                    // L'utilisateur n'a aucun droit associé (typiquement pas de profil droit), il ne faut pas qu'il puisse se connecter ou que ça plante
                    emptySet()
                }
            }

            userProfile.profilDroits = droitsRepository.getProfilDroitFromUser(utilisateur.utilisateurId)

            if (userProfile.utilisateur.utilisateurOrganismeId != null) {
                // zone de compétence
                userProfile.zoneCompetence =
                    utilisateurRepository.getZoneByOrganismeId(utilisateur.utilisateurOrganismeId!!)
            }

            // On remplit ses organismes affiliés
            utilisateurOrganismesUseCase.execute(userProfile)
        }

        return Optional.of(profile)
    }
}
