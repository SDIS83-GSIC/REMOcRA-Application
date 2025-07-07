package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.Point
import remocra.db.AbstractRepository
import remocra.db.fetchOneInto
import remocra.db.jooq.couverturehydraulique.tables.pojos.Sommet
import remocra.db.jooq.couverturehydraulique.tables.references.RESEAU
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

    fun ensureSommetTopologie(geometrie: Point, idEtude: UUID, reseauId: UUID, source: Boolean): UUID {
        val sommetExistant = getByGeometrieTopologie(geometrie, idEtude)

        val sommetId = if (sommetExistant != null) {
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
        if (source) {
            dsl.update(RESEAU)
                .set(RESEAU.SOMMET_SOURCE, sommetId).where(RESEAU.ID.eq(reseauId)).execute()
        } else {
            dsl.update(RESEAU)
                .set(RESEAU.SOMMET_DESTINATION, sommetId).where(RESEAU.ID.eq(reseauId)).execute()
        }
        return sommetId
    }

    /**
     * Supprime un sommet
     */
    fun delete(id: UUID) {
        dsl.deleteFrom(SOMMET)
            .where(SOMMET.ID.eq(id))
            .execute()
    }
}
