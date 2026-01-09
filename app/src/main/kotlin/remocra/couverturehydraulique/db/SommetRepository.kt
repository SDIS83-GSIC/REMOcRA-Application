package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.TableRecord
import org.locationtech.jts.geom.Point
import remocra.db.AbstractRepository
import remocra.db.fetchOneInto
import remocra.db.jooq.couverturehydraulique.tables.pojos.Sommet
import remocra.db.jooq.couverturehydraulique.tables.references.SOMMET
import remocra.utils.distanceBetween
import remocra.utils.normalizePoint
import java.util.UUID

/**
 * Repository pour les données de sommet
 */
class SommetRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        const val TOLERANCE_TOPOLOGIE = 0.2
    }

    /**
     * Trouve un sommet par géométrie exacte
     */
    fun getByGeometrie(geometrie: Point): Sommet? {
        return dsl.selectFrom(SOMMET)
            .where(SOMMET.GEOMETRIE.eq(geometrie))
            .fetchOneInto()
    }

    fun getByGeometrieTopologie(geometrie: Point, idEtude: UUID?): Sommet? {
        val referencePoint = normalizePoint(geometrie, appSettings.srid)
        val condition = idEtude?.let { SOMMET.ETUDE_ID.eq(it).or(SOMMET.ETUDE_ID.isNull) } ?: SOMMET.ETUDE_ID.isNull
        val candidats = dsl.selectFrom(SOMMET)
            .where(condition)
            .fetchInto(Sommet::class.java)

        return candidats
            .asSequence()
            .mapNotNull { sommet ->
                val geometry = sommet.sommetGeometrie as? Point ?: return@mapNotNull null
                val normalized = normalizePoint(geometry, appSettings.srid)
                val distance = distanceBetween(referencePoint, normalized)
                if (distance <= TOLERANCE_TOPOLOGIE) sommet to distance else null
            }
            .minByOrNull { it.second }
            ?.first
    }

    fun getPoint(id: UUID): Point? {
        return dsl.select(SOMMET.GEOMETRIE)
            .from(SOMMET)
            .where(SOMMET.ID.eq(id))
            .fetchOneInto(Point::class.java)
    }

    /**
     * Crée un nouveau sommet ou retourne l'existant
     */
    fun ensureSommet(geometrie: Point, idEtude: UUID? = null): UUID {
        val sommetExistant = getByGeometrie(geometrie)

        return if (sommetExistant != null) {
            sommetExistant.sommetId
        } else {
            val nouveauId = UUID.randomUUID()
            dsl.insertInto(SOMMET)
                .set(SOMMET.ID, nouveauId)
                .set(SOMMET.GEOMETRIE, geometrie)
                .set(SOMMET.ETUDE_ID, idEtude)
                .execute()
            nouveauId
        }
    }

    /**
     * Supprime un sommet
     */
    fun delete(id: UUID) {
        dsl.deleteFrom(SOMMET)
            .where(SOMMET.ID.eq(id))
            .execute()
    }

    /**
     * Charge tous les sommets pour une étude donnée ou null
     */
    fun getAllForEtudeOrNull(idEtude: UUID): List<Sommet> {
        return dsl.selectFrom(SOMMET)
            .where(SOMMET.ETUDE_ID.eq(idEtude).or(SOMMET.ETUDE_ID.isNull))
            .fetchInto(Sommet::class.java)
    }

    /**
     * Insertion batch de nouveaux sommets
     */
    fun batchInsert(sommets: List<Triple<UUID, Point, UUID?>>) {
        if (sommets.isEmpty()) return
        val records: List<TableRecord<*>> = sommets.map { (id, geom, etudeId) ->
            (
                dsl.newRecord(SOMMET).apply {
                    set(SOMMET.ID, id)
                    set(SOMMET.GEOMETRIE, geom)
                    set(SOMMET.ETUDE_ID, etudeId)
                } as TableRecord<*>
                )
        }
        dsl.batchInsert(records).execute()
    }
}
