package remocra.utils

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.app.ParametresProvider
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.CommuneRepository
import remocra.db.VoieRepository
import remocra.usecase.AbstractUseCase

class GetCommuneVoieUseCase @Inject constructor(
    private val parametresProvider: ParametresProvider,
    private val communeRepository: CommuneRepository,
    private val voieRepository: VoieRepository,
) : AbstractUseCase() {

    /**
     * Récupère les communes et les voies associées à une géométrie donnée en fonction des paramètres de tolérance.
     */
    fun execute(geometrie: Geometry?): CommuneVoieResult {
        val toleranceCommune = parametresProvider.getParametreInt(GlobalConstants.PEI_TOLERANCE_COMMUNE_METRES)
            ?: throw IllegalArgumentException("Le paramètre PEI_TOLERANCE_COMMUNE_METRES est nul, veuillez renseigner une valeur")
        val toleranceVoie = parametresProvider.getParametreInt(GlobalConstants.TOLERANCE_VOIES_METRES)
            ?: throw IllegalArgumentException("Le paramètre TOLERANCE_VOIES_METRES est nul, veuillez renseigner une valeur")

        var listCommune: Collection<IdCodeLibelleData> = listOf()
        var listVoie: Collection<VoieRepository.VoieWithCommune> = listOf()

        if (geometrie != null) {
            listCommune = communeRepository.getCommunesByPoint(geometrie.toGeomFromText(), toleranceCommune)
            val listIdCommune = listCommune.map { it.id }
            listVoie = voieRepository.getVoies(geometrie.toGeomFromText(), toleranceVoie, listIdCommune)
        }

        return CommuneVoieResult(
            listCommunes = listCommune,
            listVoies = listVoie,
        )
    }

    data class CommuneVoieResult(
        val listCommunes: Collection<IdCodeLibelleData>,
        val listVoies: Collection<VoieRepository.VoieWithCommune>,
    )
}
