package remocra.authn

import com.google.inject.Inject
import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.context.CallContext
import org.pac4j.core.profile.UserProfile
import remocra.db.DroitsRepository
import remocra.db.UtilisateurRepository
import java.util.Optional
import java.util.UUID

class SyncProfileAuthorizationGenerator : AuthorizationGenerator {

    @Inject
    lateinit var utilisateurRepository: UtilisateurRepository

    @Inject
    lateinit var droitsRepository: DroitsRepository

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
            userProfile.droits = droitsRepository.getDroitsFromUser(userProfile.idUtilisateur)
        }

        return Optional.of(profile)
    }
}
