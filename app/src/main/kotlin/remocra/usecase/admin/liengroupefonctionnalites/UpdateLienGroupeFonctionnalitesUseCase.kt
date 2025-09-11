package remocra.usecase.admin.liengroupefonctionnalites

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.GroupeFonctionnalitesData
import remocra.data.enums.ErrorType
import remocra.db.GroupeFonctionnalitesRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.GroupeFonctionnalites
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateLienGroupeFonctionnalitesUseCase @Inject constructor(private val groupeFonctionnalitesRepository: GroupeFonctionnalitesRepository) :
    AbstractCUDUseCase<Collection<GroupeFonctionnalitesData>>(
        TypeOperation.UPDATE,
    ) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_GROUPE_UTILISATEUR)) {
            throw RemocraResponseException(ErrorType.GROUPE_FONCTIONNALITES_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: Collection<GroupeFonctionnalitesData>, userInfo: WrappedUserInfo) { }

    override fun execute(userInfo: WrappedUserInfo, element: Collection<GroupeFonctionnalitesData>): Collection<GroupeFonctionnalitesData> {
        element.forEach { groupeFonctionnalites ->
            groupeFonctionnalitesRepository.updateDroits(
                GroupeFonctionnalites(
                    groupeFonctionnalitesId = groupeFonctionnalites.groupeFonctionnalitesId,
                    groupeFonctionnalitesLibelle = groupeFonctionnalites.groupeFonctionnalitesLibelle,
                    groupeFonctionnalitesCode = groupeFonctionnalites.groupeFonctionnalitesCode,
                    groupeFonctionnalitesActif = groupeFonctionnalites.groupeFonctionnalitesActif,
                    groupeFonctionnalitesDroits = groupeFonctionnalites.groupeFonctionnalitesDroits,
                ),
            )
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Collection<GroupeFonctionnalitesData>) { }
}
