package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.Point
import remocra.app.AppSettings
import remocra.couverturehydraulique.db.ReseauRepository
import remocra.couverturehydraulique.db.SommetRepository
import remocra.db.jooq.couverturehydraulique.tables.pojos.Sommet
import remocra.usecase.AbstractUseCase
import java.math.RoundingMode
import java.util.UUID

class CreateTopologieUseCase @Inject constructor(
    private val reseauRepository: ReseauRepository,
    private val sommetRepository: SommetRepository,
    private val appSettings: AppSettings,
) : AbstractUseCase() {

    fun createTopologie(idEtude: UUID) {
        val voies = reseauRepository.getReseauEtude(idEtude)
        // Pré-chargement des sommets existants (clé = coord arrondie + étude)
        val sommetsExistants = sommetRepository.getAllForEtudeOrNull(idEtude)
        val sommetKey: (Point) -> String = { pt ->
            val c = pt.coordinate
            val x = c.x.toBigDecimal().setScale(5, RoundingMode.HALF_UP)
            val y = c.y.toBigDecimal().setScale(5, RoundingMode.HALF_UP)
            x.toPlainString() + "_" + y.toPlainString()
        }
        val mapSommets = sommetsExistants.associateBy {
            val pt = it.sommetGeometrie as Point
            sommetKey(pt)
        }.toMutableMap()
        val sommetsToInsert = mutableListOf<Triple<UUID, Point, UUID?>>()
        val updatesSource = mutableListOf<Pair<UUID, UUID>>()
        val updatesDest = mutableListOf<Pair<UUID, UUID>>()
        // Calcul des sommets à créer et des mises à jour à faire
        voies.forEachIndexed { _, voie ->
            val start = voie.reseauGeometrie.startPoint.also { it.srid = appSettings.srid }
            val end = voie.reseauGeometrie.endPoint.also { it.srid = appSettings.srid }
            val keySource = sommetKey(start)
            val keyDest = sommetKey(end)
            val sommetSourceId = mapSommets[keySource]?.sommetId ?: run {
                val newId = UUID.randomUUID()
                sommetsToInsert.add(Triple(newId, start, voie.reseauEtude))
                mapSommets[keySource] = Sommet(newId, start, voie.reseauEtude)
                newId
            }
            val sommetDestId = mapSommets[keyDest]?.sommetId ?: run {
                val newId = UUID.randomUUID()
                sommetsToInsert.add(Triple(newId, end, voie.reseauEtude))
                mapSommets[keyDest] = Sommet(newId, end, voie.reseauEtude)
                newId
            }
            updatesSource.add(voie.reseauId to sommetSourceId)
            updatesDest.add(voie.reseauId to sommetDestId)
        }
        // Update une fois tous les sommets créés
        sommetRepository.batchInsert(sommetsToInsert)
        reseauRepository.batchUpdateSource(updatesSource)
        reseauRepository.batchUpdateDestination(updatesDest)
    }
}
