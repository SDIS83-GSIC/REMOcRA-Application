package remocra.usecases.visites

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecases.AbstractCUDUseCase
import java.time.ZonedDateTime
import java.util.UUID

class DeleteVisiteUseCase @Inject constructor(
    private val visiteRepository: VisiteRepository,
) : AbstractCUDUseCase<UUID>(TypeOperation.DELETE) {

    override fun checkDroits(userInfo: UserInfo) {
    }

    override fun postEvent(element: UUID, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.VISITE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.idUtilisateur, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(clock),
            ),
        )
        // TODO : Gestion du calcul debit/pression
        // TODO : Gestion du calcul indispo
        // TODO : Gestion "notification changement état" et autres jobs
    }

    override fun checkContraintes(element: UUID) {
        val peiId = visiteRepository.getPeiIdByVisiteId(visiteId = element)
        val lastPeiVisiteId = visiteRepository.getLastPeiVisiteId(peiId = peiId!!)
        if (lastPeiVisiteId != element) {
            throw RemocraResponseException(ErrorType.VISITE_DELETE_NOT_LAST)
        }
    }

    override fun execute(userInfo: UserInfo?, element: UUID): UUID {
        visiteRepository.deleteAllVisiteAnomalies(element)
        visiteRepository.deleteVisiteCtrl(element)
        visiteRepository.deleteVisite(element)
        return element
    }
}
