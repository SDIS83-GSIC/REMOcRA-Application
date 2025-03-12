package remocra.usecase.crise

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.db.CommuneRepository
import remocra.db.CriseRepository
import remocra.db.CriseRepository.ToponymieResult
import remocra.db.CriseRepository.TypeCriseComplete
import remocra.usecase.AbstractUseCase
import remocra.utils.toGeomFromText
import java.util.UUID

class CriseUseCase : AbstractUseCase() {
    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var communeRepository: CommuneRepository

    @Inject lateinit var appSettings: AppSettings

    fun getTypeCriseForSelect(): Collection<TypeCriseComplete> = criseRepository.getCriseForSelect()

    /**
     * Récupère toutes les géométries des communes associées à la crise spécifiée par son identifiant.
     *
     * @param criseId L'identifiant de la crise pour laquelle les géométries des communes doivent être récupérées.
     * @return La liste des géométries des communes associées à la crise, ou une liste vide si aucune géométrie n'est trouvée.
     */
    fun getCommuneGeometriesByCrise(criseId: UUID): List<Geometry> {
        val communeIds = criseRepository.getCrise(criseId).listeCommune ?: return emptyList()
        return communeIds.map { communeRepository.getById(it).communeGeometrie }
    }

    fun getCriseForMerge(): Collection<CriseRepository.CriseMerge> {
        return criseRepository.getCriseForMerge()
    }

    /**
     * Exécute la requête pour récupérer les toponymies en fonction des types sélectionnés
     */
    fun getToponymies(criseId: UUID, libelle: String): List<ToponymieResult> {
        // récupérer les informations de la crise pour filtrer les évènements
        val crise = criseRepository.getCrise(criseId)

        // récupère les types sélectionnés par l'utilisateur (protégés / non protégés)
        val nonProteges = criseRepository.getSelectedTypes(crise.listeToponymie, false)
        val proteges = criseRepository.getSelectedTypes(crise.listeToponymie, true)
        val results = mutableListOf<ToponymieResult>()
        val globalGeometry = criseRepository.getCriseGeometryUnion(criseId)

        // Requête pour les toponymies non protégées
        if (globalGeometry != null && nonProteges.isNotEmpty()) {
            results.addAll(
                criseRepository.getToponymiesNonProtegesQuery(nonProteges, globalGeometry.toGeomFromText(appSettings.epsg.name), libelle),
            )
        }

        // Requête pour les toponymies protégées
        if (globalGeometry != null && proteges.isNotEmpty()) {
            results.addAll(
                criseRepository.getToponymiesProtegesQuery(proteges, globalGeometry.toGeomFromText(appSettings.epsg.name), libelle),
            )
        }

        return results
    }
}
