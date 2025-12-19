package remocra.usecase.admin.couches.groupecouche

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.GroupeCoucheRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.GroupeCouche
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteGroupeCoucheUseCase @Inject constructor(
    private val groupeCoucheRepository: GroupeCoucheRepository,
) :
    AbstractCUDUseCase<GroupeCouche>(TypeOperation.DELETE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_COUCHE_CARTOGRAPHIQUE)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES)
        }
    }

    override fun postEvent(element: GroupeCouche, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.groupeCoucheId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.GROUPE_COUCHE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: GroupeCouche): GroupeCouche {
        groupeCoucheRepository.delete(element.groupeCoucheId)

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: GroupeCouche) {
        // Pour supprimer un groupe couche, il ne doit y avoir aucune couche associée
        val couchesAssociees = groupeCoucheRepository.getCouchesByGroupeCoucheId(element.groupeCoucheId)
        if (couchesAssociees.isNotEmpty()) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHE_GROUPE_COUCHE_DELETE_CONTRAINTE)
        }
    }
}
