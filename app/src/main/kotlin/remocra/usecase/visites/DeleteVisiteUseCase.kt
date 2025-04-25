package remocra.usecase.visites

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.VisiteData
import remocra.data.enums.ErrorType
import remocra.db.AnomalieRepository
import remocra.db.PeiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.LPeiAnomalie
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.pei.UpdatePeiUseCase

class DeleteVisiteUseCase @Inject constructor(
    private val visiteRepository: VisiteRepository,
) : AbstractCUDUseCase<VisiteData>(TypeOperation.DELETE) {

    @Inject
    lateinit var updatePeiUseCase: UpdatePeiUseCase

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var anomalieRepository: AnomalieRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
    }

    override fun postEvent(element: VisiteData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.visitePeiId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.VISITE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: VisiteData) {
        // Vérification des droits de création
        when (element.visiteTypeVisite) {
            TypeVisite.CTP -> if (!userInfo.hasDroit(droitWeb = Droit.VISITE_CTP_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_CTP_FORBIDDEN)
            }
            TypeVisite.RECEPTION -> if (!userInfo.hasDroit(droitWeb = Droit.VISITE_RECEP_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_RECEPTION_FORBIDDEN)
            }
            TypeVisite.RECO_INIT -> if (!userInfo.hasDroit(droitWeb = Droit.VISITE_RECO_INIT_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_RECO_INIT_FORBIDDEN)
            }
            TypeVisite.ROP -> if (!userInfo.hasDroit(droitWeb = Droit.VISITE_RECO_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_ROP_FORBIDDEN)
            }
            TypeVisite.NP -> if (!userInfo.hasDroit(droitWeb = Droit.VISITE_NP_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_NP_FORBIDDEN)
            }
        }
        val lastPeiVisiteId = visiteRepository.getLastPeiVisiteId(peiId = element.visitePeiId)
        if (lastPeiVisiteId != element.visiteId) {
            throw RemocraResponseException(ErrorType.VISITE_DELETE_NOT_LAST)
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: VisiteData): VisiteData {
        // On supprime les anomalies du PEI (de la dernière visite)
        peiRepository.deleteAnomaliePei(element.visitePeiId, element.listeAnomalie)

        // On va ensuite chercher les anomalies de la nouvelle dernière visite
        val avantDernierVisiteId = visiteRepository.getAvantDerniereVisite(element.visitePeiId, element.visiteId)

        val listeAnomalie = visiteRepository.getLVisiteAnomalie(avantDernierVisiteId)

        anomalieRepository.batchInsertLPeiAnomalie(
            listeAnomalie.takeIf { it.isNotEmpty() }?.map {
                LPeiAnomalie(
                    peiId = element.visitePeiId,
                    anomalieId = it,
                )
            } ?: listOf(),
        )

        // On supprime les autres données liées à la visite
        visiteRepository.deleteAllVisiteAnomalies(element.visiteId)
        visiteRepository.deleteVisiteCtrl(element.visiteId)
        visiteRepository.deleteVisite(element.visiteId)

        // On met à jour le PEI pour le calcul débit pression  / volume et le calcul d'insdispo
        updatePeiUseCase.updatePeiWithId(
            peiId = element.visitePeiId,
            userInfo = userInfo,
        )
        return element
    }
}
