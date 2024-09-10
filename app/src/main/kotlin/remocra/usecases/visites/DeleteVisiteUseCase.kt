package remocra.usecases.visites

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.VisiteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecases.AbstractCUDUseCase
import java.time.ZonedDateTime

class DeleteVisiteUseCase @Inject constructor(
    private val visiteRepository: VisiteRepository,
) : AbstractCUDUseCase<VisiteData>(TypeOperation.DELETE) {

    override fun checkDroits(userInfo: UserInfo) {
    }

    override fun postEvent(element: VisiteData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.visitePeiId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.VISITE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(clock),
            ),
        )
        // TODO : Gestion du calcul debit/pression
        // TODO : Gestion du calcul indispo
        // TODO : Gestion "notification changement état" et autres jobs
    }

    override fun checkContraintes(userInfo: UserInfo?, element: VisiteData) {
        if (userInfo == null) {
            throw RemocraResponseException(errorType = ErrorType.VISITE_D_FORBIDDEN)
        }

        // Vérification des droits de création
        when (element.visiteTypeVisite) {
            TypeVisite.CTP -> if (!userInfo.droits.contains(Droit.VISITE_CTP_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_CTP_FORBIDDEN)
            }
            TypeVisite.RECEPTION -> if (!userInfo.droits.contains(Droit.VISITE_RECEP_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_RECEPTION_FORBIDDEN)
            }
            TypeVisite.RECO_INIT -> if (!userInfo.droits.contains(Droit.VISITE_RECO_INIT_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_RECO_INIT_FORBIDDEN)
            }
            TypeVisite.RECOP -> if (!userInfo.droits.contains(Droit.VISITE_RECO_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_RECOP_FORBIDDEN)
            }
            TypeVisite.NP -> if (!userInfo.droits.contains(Droit.VISITE_NP_D)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_NP_FORBIDDEN)
            }
        }
        val lastPeiVisiteId = visiteRepository.getLastPeiVisiteId(peiId = element.visitePeiId)
        if (lastPeiVisiteId != element.visiteId) {
            throw RemocraResponseException(ErrorType.VISITE_DELETE_NOT_LAST)
        }
    }

    override fun execute(userInfo: UserInfo?, element: VisiteData): VisiteData {
        visiteRepository.deleteAllVisiteAnomalies(element.visitePeiId)
        visiteRepository.deleteVisiteCtrl(element.visitePeiId)
        visiteRepository.deleteVisite(element.visitePeiId)
        return element
    }
}
