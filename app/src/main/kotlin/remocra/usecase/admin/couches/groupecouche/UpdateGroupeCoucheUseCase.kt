package remocra.usecase.admin.couches.groupecouche

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.couche.GroupeCoucheData
import remocra.data.enums.ErrorType
import remocra.db.GroupeCoucheRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.GroupeCouche
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateGroupeCoucheUseCase @Inject constructor(
    private val groupeCoucheRepository: GroupeCoucheRepository,
) :
    AbstractCUDUseCase<GroupeCoucheData>(TypeOperation.UPDATE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_COUCHE_CARTOGRAPHIQUE)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES)
        }
    }

    override fun postEvent(element: GroupeCoucheData, userInfo: WrappedUserInfo) {
        val groupeCouche = groupeCoucheRepository.getById(element.groupeCoucheId)
        eventBus.post(
            TracabiliteEvent(
                pojo = groupeCouche,
                pojoId = element.groupeCoucheId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.GROUPE_COUCHE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: GroupeCoucheData): GroupeCoucheData {
        groupeCoucheRepository.update(
            GroupeCouche(
                groupeCoucheId = element.groupeCoucheId,
                groupeCoucheCode = element.groupeCoucheCode,
                groupeCoucheOrdre = groupeCoucheRepository.getLastOrdre()?.plus(1) ?: 1,
                groupeCoucheLibelle = element.groupeCoucheLibelle,
                groupeCoucheProtected = false,
            ),
        )

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: GroupeCoucheData) {
        if (element.groupeCoucheProtected && groupeCoucheRepository.getById(element.groupeCoucheId).groupeCoucheCode != element.groupeCoucheCode) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHE_IS_PROTECTED)
        }

        if (groupeCoucheRepository.existsByCode(element.groupeCoucheCode, element.groupeCoucheId)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES_CODE_UNIQUE)
        }
    }
}
