package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.LineString
import remocra.couverturehydraulique.graphe.Voie
import remocra.db.AbstractRepository
import remocra.db.fetchInto
import remocra.db.fetchOneInto
import remocra.db.jooq.couverturehydraulique.tables.pojos.Reseau
import remocra.db.jooq.couverturehydraulique.tables.references.RESEAU
import java.util.UUID

/**
 * Repository pour les données de réseau
 */
class ReseauRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    /**
     * Obtient un tronçon par ID
     */
    fun getById(id: UUID, useReseauImporteWithCourant: Boolean, idReseau: UUID?): Reseau? {
        return dsl.selectFrom(RESEAU)
            .where(RESEAU.ID.eq(id))
            .and(
                useReseauImporteWithCourant.let {
                    if (it) {
                        RESEAU.ETUDE_ID.eq(idReseau).or(RESEAU.ETUDE_ID.isNull)
                    } else {
                        idReseau?.let { RESEAU.ETUDE_ID.eq(it) } ?: RESEAU.ETUDE_ID.isNull
                    }
                },
            )
            .fetchOneInto()
    }

    data class ReseauTopologie(
        val reseauId: UUID,
        val reseauGeometrie: LineString,
        val reseauEtude: UUID?,
    )

    fun getReseauEtudeTopologie(etudeId: UUID): List<ReseauTopologie> {
        return dsl.select(
            RESEAU.ID,
            RESEAU.GEOMETRIE,
            RESEAU.ETUDE_ID.`as`("reseauEtude"),
        )
            .from(RESEAU)
            .where(RESEAU.ETUDE_ID.eq(etudeId)).or(RESEAU.ETUDE_ID.isNull)
            .fetchInto()
    }

    /**
     * Récupère le réseau de l'étude
     */
    fun getReseauEtude(etudeId: UUID?, useReseauImporteWithCourant: Boolean): List<Voie> {
        val condition = if (useReseauImporteWithCourant) {
            RESEAU.ETUDE_ID.eq(etudeId).or(RESEAU.ETUDE_ID.isNull)
        } else {
            etudeId?.let { RESEAU.ETUDE_ID.eq(it) } ?: RESEAU.ETUDE_ID.isNull
        }
        val result = dsl.select(
            RESEAU.ID.`as`("id"),
            RESEAU.GEOMETRIE.`as`("geometrie"),
            RESEAU.SOMMET_SOURCE.`as`("source"),
            RESEAU.SOMMET_DESTINATION.`as`("destination"),
            RESEAU.TRAVERSABLE.`as`("traversable"),
            RESEAU.SENS_UNIQUE.`as`("sensUnique"),
            RESEAU.NIVEAU.`as`("niveau"),
        ).from(RESEAU)
            .where(condition)
            .fetchInto(Voie::class.java)
        return result
    }

    /**
     * Insère un nouveau tronçon
     */
    fun insert(
        geometrie: LineString,
        idEtude: UUID?,
        traversable: Boolean? = null,
        sensUnique: Boolean? = null,
        niveau: Int? = null,
        peiTroncon: UUID? = null,
        sommetSource: UUID? = null,
        sommetDestination: UUID? = null,
    ): UUID {
        val id = UUID.randomUUID()

        dsl.insertInto(RESEAU)
            .set(RESEAU.ID, id)
            .set(RESEAU.GEOMETRIE, geometrie)
            .set(RESEAU.ETUDE_ID, idEtude)
            .set(RESEAU.TRAVERSABLE, traversable)
            .set(RESEAU.SENS_UNIQUE, sensUnique)
            .set(RESEAU.NIVEAU, niveau)
            .set(RESEAU.PEI_TRONCON, peiTroncon)
            .set(RESEAU.SOMMET_SOURCE, sommetSource)
            .set(RESEAU.SOMMET_DESTINATION, sommetDestination)
            .execute()
        return id
    }

    /**
     * Supprime un tronçon
     */
    fun delete(id: UUID) {
        dsl.deleteFrom(RESEAU)
            .where(RESEAU.ID.eq(id))
            .execute()
    }

    /**
     * Batch update des sommets source
     */
    fun batchUpdateSource(updates: List<Pair<UUID, UUID>>) {
        if (updates.isEmpty()) return
        dsl.batch(
            updates.map { (reseauId, sommetId) ->
                dsl.update(RESEAU)
                    .set(RESEAU.SOMMET_SOURCE, sommetId)
                    .where(RESEAU.ID.eq(reseauId))
            },
        ).execute()
    }

    /**
     * Batch update des sommets destination
     */
    fun batchUpdateDestination(updates: List<Pair<UUID, UUID>>) {
        if (updates.isEmpty()) return
        dsl.batch(
            updates.map { (reseauId, sommetId) ->
                dsl.update(RESEAU)
                    .set(RESEAU.SOMMET_DESTINATION, sommetId)
                    .where(RESEAU.ID.eq(reseauId))
            },
        ).execute()
    }
}
