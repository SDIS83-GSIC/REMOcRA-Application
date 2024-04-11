package remocra.authn

import com.google.inject.Inject
import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.context.CallContext
import org.pac4j.core.profile.UserProfile
import remocra.db.UtilisateurRepository
import java.util.Optional
import java.util.UUID

class SyncProfileAuthorizationGenerator : AuthorizationGenerator {

    @Inject
    lateinit var utilisateurRepository: UtilisateurRepository

    override fun generate(p0: CallContext?, profile: UserProfile): Optional<UserProfile> {
        if (profile is UserInfo) {
            // TODO prendre en compte les groups et les rôles
            val userProfile: UserInfo = profile
            val utilisateur = utilisateurRepository.syncUtilisateur(
                UUID.fromString(userProfile.getId()),
                userProfile.familyName,
                userProfile.firstName,
                userProfile.email,
                userProfile.username,
            )
            userProfile.utilisateur = utilisateur
        }
        return Optional.of(profile)
    }
}
