package remocra.usecase.crise

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.db.CommuneRepository
import remocra.db.CriseRepository
import remocra.db.CriseRepository.TypeCriseComplete
import remocra.usecase.AbstractUseCase
import java.util.UUID

class CriseUseCase : AbstractUseCase() {
    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var communeRepository: CommuneRepository

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
}
