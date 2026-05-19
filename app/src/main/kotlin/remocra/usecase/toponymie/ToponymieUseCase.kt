package remocra.usecase.toponymie

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.app.ParametresProvider
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.data.enums.ParametreEnum
import remocra.db.CommuneRepository
import remocra.db.CriseRepository.ToponymieResult
import remocra.db.ToponymieRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.getParametre
import remocra.utils.toGeomFromText
import java.util.UUID

class ToponymieUseCase @Inject constructor(
    private var toponymieRepository: ToponymieRepository,
    private var communeRepository: CommuneRepository,
    private var parametresProvider: ParametresProvider,
) : AbstractUseCase() {
    fun getToponymieForSelect(): Collection<IdCodeLibelleData> = toponymieRepository.getToponymieForSelect()

    fun getParametreToponymies(libelle: String, dependenceObj: UUID?): List<ToponymieResult> {
        // Récupérer les ids des toponymies depuis les paramètres
        val toponymieCodes = parametresProvider.get().mapParametres.getParametre(ParametreEnum.LISTE_TOPONYMIE_CODE.name).parametreValeur
        val toponymiesList = extractStringList(toponymieCodes ?: "").mapNotNull { toponymieRepository.getByCode(it) }

        // Si une commune est spécifiée, récupérer la géométrie de la commune
        var comuneGeometrie: Geometry? = null
        if (dependenceObj != null) {
            comuneGeometrie = communeRepository.getById(dependenceObj).communeGeometrie
        }

        return toponymieRepository.getToponymiesProtegesQuery(
            toponymiesList.filter { it.typeToponymieProtected == true && it.typeToponymieActif },
            comuneGeometrie?.toGeomFromText(),
            libelle,
        ).toList() + toponymieRepository.getOtherToponymiesQuery(
            comuneGeometrie?.toGeomFromText(),
            libelle,
            toponymiesList.filter { it.typeToponymieProtected == false && it.typeToponymieActif }.map { it.typeToponymieId },

        ).toList()
    }

    private fun extractStringList(input: String): List<String> {
        return input.trim('[', ']').split(",")
            .map {
                it.trim().removeSurrounding("\"")
            }
    }
}
