package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.Point
import remocra.app.AppSettings
import remocra.db.AbstractRepository
import remocra.db.fetchInto
import remocra.db.jooq.couverturehydraulique.tables.pojos.Sommet
import remocra.db.jooq.couverturehydraulique.tables.references.SOMMET
import java.util.UUID

/**
 * Repository pour les données de somme
 */
class SommetRepository @Inject constructor(
    private val dsl: DSLContext,
    private val appSettings: AppSettings,
) : AbstractRepository() {

    fun getSommetsEtude(etudeId: UUID?, useReseauImporteWithCourant: Boolean): List<Sommet> {
        val condition = if (useReseauImporteWithCourant) {
            SOMMET.ETUDE_ID.eq(etudeId).or(SOMMET.ETUDE_ID.isNull)
        } else {
            etudeId?.let { SOMMET.ETUDE_ID.eq(it) } ?: SOMMET.ETUDE_ID.isNull
        }

        return dsl.selectFrom(SOMMET)
            .where(condition)
            .fetchInto()
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
        dsl.batch(
            sommets.map { (id, geom, etudeId) ->
                val record = dsl.newRecord(SOMMET)
                record.set(SOMMET.ID, id)
                record.set(SOMMET.GEOMETRIE, geom)
                record.set(SOMMET.ETUDE_ID, etudeId)
                dsl.insertInto(SOMMET).set(record)
            },
        ).execute()
    }
}
