package remocra.usecase.risque

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.data.risque.ImportRisqueExpressData
import remocra.db.RisqueExpressRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.RisqueExpress
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteRisquesExpressUseCase() : AbstractCUDUseCase<Collection<RisqueExpress>>(TypeOperation.DELETE) {

    @Inject
    lateinit var risqueExpressRepository: RisqueExpressRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.RISQUE_EXPRESS_A)) {
            throw RemocraResponseException(ErrorType.RISQUE_FORBIDDEN_A)
        }
    }

    override fun postEvent(element: Collection<RisqueExpress>, userInfo: WrappedUserInfo) {
        element.forEach {
            val risque = ImportRisqueExpressData(
                risqueId = it.risqueExpressId,
                risqueLibelle = it.risqueExpressLibelle,
                fileRisqueExpress = null,
            )
            if (risque.risqueId != null) {
                eventBus.post(
                    TracabiliteEvent(
                        pojo = risque,
                        pojoId = risque.risqueId,
                        typeOperation = typeOperation,
                        typeObjet = TypeObjet.RISQUE_EXPRESS,
                        auteurTracabilite = AuteurTracabiliteData(
                            idAuteur = userInfo.utilisateurId!!,
                            nom = userInfo.nom!!,
                            prenom = userInfo.prenom,
                            email = userInfo.userInfo!!.email,
                            typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                        ),
                        date = dateUtils.now(),
                    ),
                )
            }
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: Collection<RisqueExpress>): Collection<RisqueExpress> {
        risqueExpressRepository.deleteRisquesExpress()
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Collection<RisqueExpress>) {
        // no-op
    }
}
