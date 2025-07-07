package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import remocra.app.AppSettings
import remocra.couverturehydraulique.db.ReseauRepository
import remocra.couverturehydraulique.db.SommetRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class CreateTopologieUseCase @Inject constructor(
    private val reseauRepository: ReseauRepository,
    private val sommetRepository: SommetRepository,
    private val appSettings: AppSettings,
) : AbstractUseCase() {

    fun createTopologie(idEtude: UUID) {
        val voies = reseauRepository.getReseauEtude(idEtude)
        voies.forEach { voie ->
            val start = voie.reseauGeometrie.startPoint.also { it.srid = appSettings.srid }
            val end = voie.reseauGeometrie.endPoint.also { it.srid = appSettings.srid }
            // Recherche/création pour le début (source)
            sommetRepository.ensureSommetTopologie(start, idEtude, voie.reseauId, true)
            // Recherche/création pour la fin (destination)
            sommetRepository.ensureSommetTopologie(end, idEtude, voie.reseauId, false)
        }
    }
}
