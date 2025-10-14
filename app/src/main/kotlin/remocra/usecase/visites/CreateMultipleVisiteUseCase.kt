package remocra.usecase.visites

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.VisiteData
import remocra.data.VisiteTourneeInput
import remocra.usecase.AbstractUseCase
import java.util.UUID

class CreateMultipleVisiteUseCase : AbstractUseCase() {

    @Inject lateinit var createVisiteUseCase: CreateVisiteUseCase

    /** Le useCase reçoit un objet visiteTourneeInput pour boucler et obtenir X objets VisiteData pour pouvoir les insérer
     * Pour chaque insert, on regarde le AbstractEndpoint.Result :
     * - S'il est de type Created, ça a fonctionné, pas besoin de remonter le resultat
     * - S'il est d'un type différent, ça n'a pas fonctionné, on remonte le peiId face à la RemocraResponseException remontée par le useCase
     * @param userInfo
     * @param visiteTourneeInput
     */
    fun createMultipleVisite(userInfo: WrappedUserInfo, visiteTourneeInput: VisiteTourneeInput): Map<UUID, AbstractUseCase.Result> {
        val listResult: MutableMap<UUID, AbstractUseCase.Result> = mutableMapOf()
        visiteTourneeInput.listeSimplifiedVisite?.forEach { visite ->
            val generatedVisiteId = UUID.randomUUID()
            val result =
                createVisiteUseCase.execute(
                    userInfo = userInfo,
                    mainTransactionManager = null,
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
        return listResult
    }
}
