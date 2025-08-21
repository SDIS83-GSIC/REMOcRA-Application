package remocra.usecase.pei

import jakarta.inject.Inject
import remocra.data.PeiData
import remocra.data.PeiForCalculDispoData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecase.AbstractUseCase

class GetDisponibilitePeiUseCase @Inject constructor(
    private val visiteRepository: remocra.db.VisiteRepository,
    private val calculDispoUseCase: CalculDispoUseCase,
) : AbstractUseCase() {

    fun execute(element: PeiData): Disponibilite {
        // à partir de là, on a besoin de travailler sur le type concret
        if (TypePei.PIBI == element.peiTypePei) {
            val elementConcret = element as PibiData

            val lastVisite = visiteRepository.getLastVisiteDebitPression(element.peiId)

            // Calcul de la dispo du PEI
            val peiForCalculDispoData = PeiForCalculDispoData(
                peiId = elementConcret.peiId,
                peiNatureId = elementConcret.peiNatureId,
                diametreId = elementConcret.pibiDiametreId,
                reservoirId = elementConcret.pibiReservoirId,
                debit = lastVisite?.visiteCtrlDebitPressionDebit,
                pression = lastVisite?.visiteCtrlDebitPressionPression?.toDouble(),
                pressionDynamique = lastVisite?.visiteCtrlDebitPressionPressionDyn?.toDouble(),
                penaCapacite = null,
                penaCapaciteIllimitee = null,
                penaCapaciteIncertaine = null,
            )

            return calculDispoUseCase.execute(peiForCalculDispoData)
        } else {
            val elementConcret = element as PenaData

            // Calcul de la dispo du PEI
            val peiForCalculDispoData = PeiForCalculDispoData(
                peiId = elementConcret.peiId,
                peiNatureId = elementConcret.peiNatureId,
                diametreId = null,
                reservoirId = null,
                debit = null,
                pression = null,
                pressionDynamique = null,
                penaCapacite = elementConcret.penaCapacite,
                penaCapaciteIllimitee = elementConcret.penaCapaciteIllimitee,
                penaCapaciteIncertaine = elementConcret.penaCapaciteIncertaine,
            )

            return calculDispoUseCase.execute(peiForCalculDispoData)
        }
    }
}
