package remocra.usecase.pei

import jakarta.inject.Inject
import jakarta.inject.Provider
import remocra.app.DataCacheProvider
import remocra.app.ParametresProvider
import remocra.data.PeiData
import remocra.data.PeiForNumerotationData
import remocra.usecase.AbstractUseCase

class GetNumerotationPeiUseCase @Inject constructor(
    private val dataCacheProvider: DataCacheProvider,
    private val numerotationUseCase: NumerotationUseCase,
    private val parametresProvider: Provider<ParametresProvider>,
) : AbstractUseCase() {

    fun execute(element: PeiData): Pair<String, Int> {
        // Création de l'objet data pour le calcul
        val peiForNumerotationData = PeiForNumerotationData(
            peiNumeroInterne = element.peiNumeroInterne,
            peiId = element.peiId,
            peiCommuneId = element.peiCommuneId,
            peiZoneSpecialeId = element.peiZoneSpecialeId,
            gestionnaireId = element.peiGestionnaireId,
            domaine = dataCacheProvider.getDomaines().values.firstOrNull { it.domaineId == element.peiDomaineId },
            nature = dataCacheProvider.getNatures().values.firstOrNull { it.natureId == element.peiNatureId },
            natureDeci = dataCacheProvider.getNaturesDeci().values.firstOrNull { it.natureDeciId == element.peiNatureDeciId },
        )

        // Calcul du numéro *interne*
        val numeroInterne = numerotationUseCase.computeNumeroInterne(peiForNumerotationData)
        peiForNumerotationData.peiNumeroInterne = numeroInterne

        // Calcul du numéro *complet*, avec un numéro interne mis à jour
        val numeroComplet = numerotationUseCase.computeNumero(peiForNumerotationData)

        return Pair(numeroComplet, numeroInterne)
    }
}
