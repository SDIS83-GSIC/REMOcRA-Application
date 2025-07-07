package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import remocra.db.AbstractRepository
import remocra.db.fetchInto
import remocra.db.fetchOneInto
import remocra.db.jooq.couverturehydraulique.tables.pojos.Reseau
import remocra.db.jooq.couverturehydraulique.tables.references.RESEAU
import remocra.utils.ST_DWithin
import remocra.utils.ST_Distance
import remocra.utils.ST_Intersects
import remocra.utils.toGeomFromText
import java.util.UUID

/**
 * Repository pour les données de réseau
 */
class ReseauRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {
    /**
     * Trouve le tronçon le plus proche d'un point
     */
    fun findTronconPlusProche(
        point: Point,
        distanceMax: Int,
        idEtude: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): Reseau? {
        val condition = if (useReseauImporteWithCourant) {
            RESEAU.ETUDE_ID.eq(idEtude).or(RESEAU.ETUDE_ID.isNull)
        } else {
            idEtude?.let { RESEAU.ETUDE_ID.eq(it) } ?: RESEAU.ETUDE_ID.isNull
        }

        return dsl.selectFrom(RESEAU)
            .where(ST_DWithin(RESEAU.GEOMETRIE, point.toGeomFromText(), distanceMax.toDouble()))
            .and(condition)
            .and(RESEAU.PEI_TRONCON.isNull)
            .orderBy(ST_Distance(RESEAU.GEOMETRIE, point.toGeomFromText()))
            .limit(1)
            .fetchOneInto()
    }

    /**
     * Obtient un tronçon par ID
     */
    fun getById(id: UUID, useReseauImporteWithCourant: Boolean, idReseau: UUID?): Reseau? {
        return dsl.selectFrom(RESEAU)
            .where(RESEAU.ID.eq(id))
            .and(
                useReseauImporteWithCourant.let { it ->
                    if (it) {
                        RESEAU.ETUDE_ID.eq(idReseau).or(RESEAU.ETUDE_ID.isNull)
                    } else {
                        idReseau?.let { RESEAU.ETUDE_ID.eq(it) } ?: RESEAU.ETUDE_ID.isNull
                    }
                },
            )
            .fetchOneInto()
    }

    /**
     * Obtient un tronçon par PEI
     */
    fun getByPei(peiId: UUID): Reseau? {
        return dsl.selectFrom(RESEAU)
            .where(RESEAU.PEI_TRONCON.eq(peiId))
            .limit(1)
            .fetchOneInto()
    }

    data class ReseauTopologie(
        val reseauId: UUID,
        val reseauGeometrie: LineString,
    )

    /**
     * Récupère le réseau de l'étude
     */
    fun getReseauEtude(etudeId: UUID): List<ReseauTopologie> {
        return dsl.select(
            RESEAU.ID,
            RESEAU.GEOMETRIE,
        )
            .from(RESEAU)
            .where(RESEAU.ETUDE_ID.eq(etudeId))
            .fetchInto()
    }

    /**
     * Met à jour la géométrie d'un tronçon
     */
    fun updateGeometrie(id: UUID, geometrie: LineString) {
        dsl.update(RESEAU)
            .set(RESEAU.GEOMETRIE, geometrie)
            .where(RESEAU.ID.eq(id))
            .execute()
    }

    /**
     * Met à jour le sommet de destination
     */
    fun updateSommetDestination(id: UUID, sommetDestination: UUID) {
        dsl.update(RESEAU)
            .set(RESEAU.SOMMET_DESTINATION, sommetDestination)
            .where(RESEAU.ID.eq(id))
            .execute()
    }

    fun updateSommetSource(id: UUID, sommetSource: UUID) {
        dsl.update(RESEAU)
            .set(RESEAU.SOMMET_SOURCE, sommetSource)
            .where(RESEAU.ID.eq(id)).execute()
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
     * Supprime un tronçon par PEI
     */
    fun deleteByPei(peiId: UUID) {
        dsl.deleteFrom(RESEAU)
            .where(RESEAU.PEI_TRONCON.eq(peiId))
            .execute()
    }

    /**
     * Obtient les tronçons connectés à un sommet (sortants)
     */
    fun getTronconsSortants(
        sommetId: UUID,
        idReseauImporte: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): List<Reseau> {
        val condition = if (useReseauImporteWithCourant) {
            RESEAU.ETUDE_ID.eq(idReseauImporte).or(RESEAU.ETUDE_ID.isNull)
        } else {
            idReseauImporte?.let { RESEAU.ETUDE_ID.eq(it) } ?: RESEAU.ETUDE_ID.isNull
        }

        return dsl.selectFrom(RESEAU)
            .where(RESEAU.SOMMET_SOURCE.eq(sommetId))
            .and(condition)
            .fetchInto()
    }

    /**
     * Obtient les tronçons connectés à un sommet (entrants)
     */
    fun getTronconsEntrants(
        sommetId: UUID,
        idReseauImporte: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): List<Reseau> {
        val condition = if (useReseauImporteWithCourant) {
            RESEAU.ETUDE_ID.eq(idReseauImporte).or(RESEAU.ETUDE_ID.isNull)
        } else {
            idReseauImporte?.let { RESEAU.ETUDE_ID.eq(it) } ?: RESEAU.ETUDE_ID.isNull
        }

        return dsl.selectFrom(RESEAU)
            .where(RESEAU.SOMMET_DESTINATION.eq(sommetId))
            .and(condition)
            .fetchInto()
    }

    /**
     * Obtient les tronçons non traversables qui intersectent une géométrie
     */
    fun getTronconsNonTraversablesIntersectant(
        geometrie: Geometry,
        tronconExclu: UUID,
        idReseauImporte: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): List<Reseau> {
        val condition = if (useReseauImporteWithCourant) {
            RESEAU.ETUDE_ID.eq(idReseauImporte).or(RESEAU.ETUDE_ID.isNull)
        } else {
            idReseauImporte?.let { RESEAU.ETUDE_ID.eq(it) } ?: RESEAU.ETUDE_ID.isNull
        }

        return dsl.selectFrom(RESEAU)
            .where(RESEAU.PEI_TRONCON.isNull)
            .and(RESEAU.TRAVERSABLE.eq(false))
            .and(RESEAU.NIVEAU.eq(0))
            .and(RESEAU.ID.ne(tronconExclu))
            .and(
                ST_Intersects(
                    geometrie.toGeomFromText(),
                    RESEAU.GEOMETRIE,
                ),
            )
            .and(condition)
            .fetchInto()
    }

    /**
     * Obtient le sommet source d'un tronçon PEI
     */
    fun getSommetSourcePei(peiId: UUID): UUID? {
        return dsl.select(RESEAU.SOMMET_SOURCE)
            .from(RESEAU)
            .where(RESEAU.PEI_TRONCON.eq(peiId))
            .limit(1)
            .fetchOne()
            ?.get(RESEAU.SOMMET_SOURCE)
    }

    /**
     * Obtient l'ID d'un tronçon PEI
     */
    fun getIdTronconPei(peiId: UUID): UUID? {
        return dsl.select(RESEAU.ID)
            .from(RESEAU)
            .where(RESEAU.PEI_TRONCON.eq(peiId))
            .limit(1)
            .fetchOne()
            ?.get(RESEAU.ID)
    }

    /**
     * Trouve les tronçons connectés à un sommet par destination
     */
    fun getTronconByDestination(sommetId: UUID): Reseau? {
        return dsl.selectFrom(RESEAU)
            .where(RESEAU.SOMMET_DESTINATION.eq(sommetId))
            .and(RESEAU.PEI_TRONCON.isNull)
            .fetchOneInto()
    }

    /**
     * Trouve les tronçons connectés à un sommet par source
     */
    fun getTronconBySource(sommetId: UUID): Reseau? {
        return dsl.selectFrom(RESEAU)
            .where(RESEAU.SOMMET_SOURCE.eq(sommetId))
            .and(RESEAU.PEI_TRONCON.isNull)
            .fetchOneInto()
    }
}
