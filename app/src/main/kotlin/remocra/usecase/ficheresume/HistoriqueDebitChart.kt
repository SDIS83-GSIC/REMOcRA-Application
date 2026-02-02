package remocra.usecase.ficheresume

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import remocra.app.ParametresProvider
import remocra.data.enums.ParametreEnum
import remocra.db.PibiRepository
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.usecase.AbstractUseCase
import java.math.BigDecimal
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

        val historique = pibiRepository.getHistorique(pibiId, nombreHistorique, typeVisiteWithCdp).sortedBy { it.visiteDate }

        // Si on a aucune mesure, pas la peine d'afficher le tableau
        if (historique.all { it.debit == null && it.pression == null && it.pressionDyn == null }) {
            return null
        }

        return DebitPressionChartWithMoyenne(
            data = historique,
            moyenneDebit = historique.mapNotNull { it.debit }.average().round(),
            moyennePression = historique.mapNotNull { it.pression }.average().round(),
            moyennePressionDyn = historique.mapNotNull { it.pressionDyn }.average().round(),
        )
    }

    data class DebitPressionChartWithMoyenne(
        val data: List<PibiRepository.DebitPressionChart>?,
        val moyenneDebit: BigDecimal?,
        val moyennePression: BigDecimal?,
        val moyennePressionDyn: BigDecimal?,
    )

    private fun Double?.round(): BigDecimal? = this
        ?.takeIf { !it.isNaN() }
        ?.toBigDecimal()
        ?.setScale(2, java.math.RoundingMode.HALF_UP)
}
