package remocra.usecase.visites

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.VisiteData
import remocra.data.VisiteTourneeInput
import remocra.db.MaterializedViewRepository
import remocra.db.TransactionManager
import remocra.db.VisiteRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class CreateMultipleVisiteUseCase
@Inject
constructor(
    private val createVisiteUseCase: CreateVisiteUseCase,
    private val visiteRepository: VisiteRepository,
    private val transactionManager: TransactionManager,
    private val materializedViewRepository: MaterializedViewRepository,
) :
    AbstractUseCase() {

    /** Le useCase reçoit un objet visiteTourneeInput pour boucler et obtenir X objets VisiteData pour pouvoir les insérer
     * Pour chaque insert, on regarde le AbstractEndpoint.Result :
     * - S'il est de type Created, ça a fonctionné, pas besoin de remonter le resultat
     * - S'il est d'un type différent, ça n'a pas fonctionné, on remonte le peiId face à la RemocraResponseException remontée par le useCase
     * @param userInfo
     * @param visiteTourneeInput
     */
    fun createMultipleVisite(userInfo: WrappedUserInfo, visiteTourneeInput: VisiteTourneeInput): Map<UUID, AbstractUseCase.Result> = transactionManager.transactionResult {
        try {
            // on désactive les triggers pour éviter le lancer les événements pour chaque insert,
            // et notament de la vue v_pei_visite_date
            // on les lancera à la fin
            visiteRepository.disabledAllTriggerOnVisite()

            val listResult: MutableMap<UUID, AbstractUseCase.Result> = mutableMapOf()
            visiteTourneeInput.listeSimplifiedVisite?.forEach { visite ->
                val generatedVisiteId = UUID.randomUUID()
                val result =
                    createVisiteUseCase.execute(
                        userInfo = userInfo,
                        mainTransactionManager = transactionManager,
                        element = VisiteData(
                            visiteId = generatedVisiteId,
                            visitePeiId = visite.visitePeiId,
                            visiteDate = visiteTourneeInput.visiteDate,
                            visiteTypeVisite = visiteTourneeInput.visiteTypeVisite,
                            visiteAgent1 = visiteTourneeInput.visiteAgent1,
                            visiteAgent2 = visiteTourneeInput.visiteAgent2,
                            visiteObservation = visite.visiteObservation,
                            listeAnomalie = visite.listeAnomalie,
                            isCtrlDebitPression = visiteTourneeInput.isCtrlDebitPression,
                            ctrlDebitPression = visite.ctrlDebitPression,
                        ),
                    )
                if (result !is AbstractUseCase.Result.Created) {
                    listResult[visite.visitePeiId] = result
                }
            }
            return@transactionResult listResult
        } finally {
            visiteRepository.enableAllTriggerOnVisite()
            materializedViewRepository.refreshViewVisites()
        }
    }
}
