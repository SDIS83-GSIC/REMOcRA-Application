package remocra.usecase.utilisateur

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.db.OrganismeRepository

/**
 * Usecase permettant de calculer les organismes que l'utilisateur peut voir car enfants de l'organisme de rattachement de l'utilisateur
 */
class UtilisateurOrganismesUseCase {
    @Inject
    lateinit var organismeRepository: OrganismeRepository

    fun execute(userInfo: UserInfo) {
        if (userInfo.organismeId != null) {
            userInfo.affiliatedOrganismeIds = organismeRepository.getOrganismeAndChildren(userInfo.organismeId!!).toSet()
        }
    }
}
