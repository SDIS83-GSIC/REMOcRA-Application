package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.Geometry
import remocra.db.AbstractRepository
import remocra.db.fetchInto
import remocra.db.fetchOneInto
import remocra.db.jooq.couverturehydraulique.enums.TypeSide
import remocra.db.jooq.couverturehydraulique.tables.pojos.TempDistance
import remocra.db.jooq.couverturehydraulique.tables.references.TEMP_DISTANCE
import java.util.UUID

/**
 * Repository pour les données temporaires de distance
 */
class TempDistanceRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Vide la table temporaire
     */
    fun emptyTable() {
        dsl.truncate(TEMP_DISTANCE).execute()
    }

    /**
     * Supprime les distances pour un PEI de départ
     */
    fun deleteByPeiDepart(peiDepart: UUID) {
        dsl.deleteFrom(TEMP_DISTANCE)
            .where(TEMP_DISTANCE.PEI_START.eq(peiDepart))
            .execute()
    }

    /**
     * Supprime les distances pour un PEI et une distance donnée
     */
    fun deleteByPeiAndDistance(peiDepart: UUID, distance: Int) {
        dsl.deleteFrom(TEMP_DISTANCE)
            .where(TEMP_DISTANCE.PEI_START.eq(peiDepart))
            .and(TEMP_DISTANCE.DISTANCE.eq(distance.toDouble()))
            .execute()
    }

    /**
     * Obtient un record temporaire par PEI et sommet
     */
    fun getByPeiAndSommet(peiDepart: UUID, sommet: UUID): TempDistance? {
        return dsl.selectFrom(TEMP_DISTANCE)
            .where(TEMP_DISTANCE.PEI_START.eq(peiDepart))
            .and(TEMP_DISTANCE.SOMMET.eq(sommet))
            .orderBy(TEMP_DISTANCE.DISTANCE)
            .limit(1)
            .fetchOneInto()
    }

    /**
     * Vérifie l'existence d'un trajet
     */
    fun checkIfExistTrajet(peiDepart: UUID, destination: UUID?, voieId: UUID): Boolean {
        return dsl.fetchExists(
            dsl.selectFrom(TEMP_DISTANCE)
                .where(TEMP_DISTANCE.PEI_START.eq(peiDepart))
                .and(TEMP_DISTANCE.SOMMET.eq(destination))
                .and(TEMP_DISTANCE.VOIE_COURANTE.eq(voieId)),
        )
    }

    /**
     * Insère un nouveau record temporaire
     */
    fun insert(
        peiDepart: UUID,
        sommet: UUID,
        voieCourante: UUID,
        voiePrecedente: UUID? = null,
        distance: Double,
        geometrie: Geometry,
        traversable: Boolean? = false,
        side: TypeSide,
    ) {
        dsl.insertInto(TEMP_DISTANCE)
            .set(TEMP_DISTANCE.ID, UUID.randomUUID())
            .set(TEMP_DISTANCE.PEI_START, peiDepart)
            .set(TEMP_DISTANCE.SOMMET, sommet)
            .set(TEMP_DISTANCE.VOIE_COURANTE, voieCourante)
            .set(TEMP_DISTANCE.DISTANCE, distance)
            .set(TEMP_DISTANCE.GEOMETRIE, geometrie)
            .set(TEMP_DISTANCE.VOIE_PRECEDENTE, voiePrecedente)
            .set(TEMP_DISTANCE.TRAVERSABLE, traversable)
            .set(TEMP_DISTANCE.SIDE, side)
            .execute()
    }

    /**
     * Obtient les nœuds depuis un parcours précédent
     * Logique SQL : récupère les sommets des tronçons dont la voie courante
     * figure comme voie précédente dans le parcours précédent
     */
    fun getNoeudsFromPreviousParcours(distancePrecedente: Int): List<UUID> {
        return dsl.select(TEMP_DISTANCE.SOMMET)
            .from(TEMP_DISTANCE)
            .where(
                TEMP_DISTANCE.VOIE_COURANTE.`in`(
                    dsl.selectDistinct(TEMP_DISTANCE.VOIE_PRECEDENTE)
                        .from(TEMP_DISTANCE)
                        .where(TEMP_DISTANCE.DISTANCE.eq(distancePrecedente.toDouble())),
                ),
            )
            .fetchInto()
    }

    /**
     * Récupère toutes les géométries pour un PEI de départ (pour union incrémentale)
     */
    fun getGeometries(peiDepart: UUID): List<Geometry> {
        return dsl.select(TEMP_DISTANCE.GEOMETRIE)
            .from(TEMP_DISTANCE)
            .where(TEMP_DISTANCE.PEI_START.eq(peiDepart))
            .and(TEMP_DISTANCE.GEOMETRIE.isNotNull)
            .fetch { record ->
                record.component1()
            }
    }
}
