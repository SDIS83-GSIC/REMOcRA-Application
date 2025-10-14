package remocra.usecase.visites

import jakarta.inject.Inject
import remocra.db.VisiteRepository
import java.util.UUID

class GetVisiteWithAnomalies {

    @Inject lateinit var visiteRepository: VisiteRepository

    fun getVisiteWithAnomalies(peiUUID: UUID): List<VisiteRepository.VisiteComplete> {
        val listVisite = visiteRepository.getAllVisiteByPeiId(peiUUID)
        val listCtrl = visiteRepository.getAllCtrlByListVisiteId(listVisite.map { it.visiteId })
        val listAnomalieOrderedByVisite = visiteRepository.getCompletedAnomalieByPeiId(peiUUID)

        listVisite.forEach { visite ->
            visite.ctrlDebitPression = listCtrl.firstOrNull { it.visiteCtrlDebitPressionVisiteId == visite.visiteId }
            visite.listeAnomalie = listAnomalieOrderedByVisite[visite.visiteId]
        }
        return listVisite
    }
}
