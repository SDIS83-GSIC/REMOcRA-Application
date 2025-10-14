package remocra.usecase.couverturehydraulique

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.PeiProjetData
import remocra.data.enums.ErrorType
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeletePeiProjetUseCase : AbstractCUDUseCase<PeiProjetData>(TypeOperation.DELETE) {
    @Inject
    lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: PeiProjetData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiProjetId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI_PROJET,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: PeiProjetData): PeiProjetData {
        couvertureHydrauliqueRepository.deletePeiProjet(element.peiProjetId)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: PeiProjetData) {
        // Aucune contrainte
    }
}
