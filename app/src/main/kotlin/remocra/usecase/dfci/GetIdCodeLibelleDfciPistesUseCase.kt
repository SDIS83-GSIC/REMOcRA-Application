package remocra.usecase.dfci

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.app.ParametresProvider
import remocra.data.GlobalData
import remocra.db.DfciPistesRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.toGeomFromText

/**
 * Use case permettant de récupérer l'id, le code, et le libelle des pistes
 */
class GetIdCodeLibelleDfciPistesUseCase @Inject constructor(
    private val dfciPistesRepository: DfciPistesRepository,
    private val parametresProvider: ParametresProvider,
) : AbstractUseCase() {

    fun execute(geometrie: Geometry): Collection<GlobalData.IdCodeLibelleData> {
        val tolerance = parametresProvider.getParametreInt(GlobalConstants.DFCI_TOLERANCE_DFCI_PISTE_METRES)
            ?: throw IllegalArgumentException("Le paramètre TOLERANCE_DFCI_PISTE_METRES est nul, veuillez renseigner une valeur")
        return dfciPistesRepository.getIdCodeLibelleDfciPiste(geometrie.toGeomFromText(), tolerance)
    }
}
