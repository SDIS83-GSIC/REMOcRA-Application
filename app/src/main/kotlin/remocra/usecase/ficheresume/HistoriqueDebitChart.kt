package remocra.usecase.ficheresume

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import remocra.app.ParametresProvider
import remocra.data.enums.ParametreEnum
import remocra.db.PibiRepository
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.usecase.AbstractUseCase
import java.util.UUID

class HistoriqueDebitChart : AbstractUseCase() {

    @Inject
    lateinit var pibiRepository: PibiRepository

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Inject
    lateinit var objectMapper: ObjectMapper

    fun execute(pibiId: UUID): DebitPressionChartWithMoyenne? {
        val nombreHistorique = parametresProvider.getParametreInt(ParametreEnum.PEI_NOMBRE_HISTORIQUE.name)
        val typeVisiteCdpString = parametresProvider.getParametreString(ParametreEnum.TYPE_VISITE_CDP.name)
        val typeVisiteWithCdp: List<TypeVisite> = if (typeVisiteCdpString != null) {
            objectMapper.readValue<List<TypeVisite>>(typeVisiteCdpString)
        } else {
            listOf(TypeVisite.CTP, TypeVisite.RECEPTION)
        }

        if (nombreHistorique == null || nombreHistorique == 0) {
            return null
        }

        val historique = pibiRepository.getHistorique(pibiId, nombreHistorique, typeVisiteWithCdp)

        // Si on a aucune mesure, pas la peine d'afficher le tableau
        if (historique.all { it.debit == null && it.pression == null && it.pressionDyn == null }) {
            return null
        }

        return DebitPressionChartWithMoyenne(
            data = historique,
            moyenneDebit = historique.map { it.debit }.filterNotNull().average(),
            moyennePression = historique.map { it.pression }.filterNotNull().average(),
            moyennePressionDyn = historique.map { it.pressionDyn }.filterNotNull().average(),
        )
    }

    data class DebitPressionChartWithMoyenne(
        val data: List<PibiRepository.DebitPressionChart>?,
        val moyenneDebit: Double,
        val moyennePression: Double,
        val moyennePressionDyn: Double,
    )
}
