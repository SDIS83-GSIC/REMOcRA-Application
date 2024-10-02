package remocra.auth

import com.google.inject.Inject
import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.context.CallContext
import org.pac4j.core.profile.UserProfile
import remocra.db.DroitsRepository
import remocra.db.UtilisateurRepository
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
            userProfile.utilisateur = utilisateurRepository.syncUtilisateur(
                UUID.fromString(userProfile.id),
                userProfile.familyName,
                userProfile.firstName,
                userProfile.email,
                userProfile.username,
            )

            // On remplit ses droits
            userProfile.droits = droitsRepository.getDroitsFromUser(userProfile.utilisateurId)

            // On remplit ses organismes affiliés
            utilisateurOrganismesUseCase.execute(userProfile)
        }

        return Optional.of(profile)
    }
}
