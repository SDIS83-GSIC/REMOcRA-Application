package remocra.usecase.admin.groupefonctionnalites

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.GroupeFonctionnalitesData
import remocra.data.enums.ErrorType
import remocra.db.GroupeFonctionnalitesRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.GroupeFonctionnalites
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateGroupeFonctionnalitesUseCase @Inject constructor(private val groupeFonctionnalitesRepository: GroupeFonctionnalitesRepository) :
    AbstractCUDUseCase<GroupeFonctionnalitesData>(
        TypeOperation.UPDATE,
    ) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A)) {
            throw RemocraResponseException(ErrorType.GROUPE_FONCTIONNALITES_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: GroupeFonctionnalitesData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.groupeFonctionnalitesId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.GROUPE_FONCTIONNALITES,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: GroupeFonctionnalitesData): GroupeFonctionnalitesData {
        groupeFonctionnalitesRepository.update(
            GroupeFonctionnalites(
                groupeFonctionnalitesId = element.groupeFonctionnalitesId,
                groupeFonctionnalitesLibelle = element.groupeFonctionnalitesLibelle,
                groupeFonctionnalitesCode = element.groupeFonctionnalitesCode,
                groupeFonctionnalitesActif = element.groupeFonctionnalitesActif,
                groupeFonctionnalitesDroits = element.groupeFonctionnalitesDroits,
            ),
        )
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: GroupeFonctionnalitesData) { }
}
