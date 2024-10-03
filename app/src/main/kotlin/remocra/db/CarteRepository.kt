package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.locationtech.jts.geom.Point
import remocra.data.enums.TypePointCarte
import remocra.db.jooq.couverturehydraulique.tables.references.PEI_PROJET
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_Within
import java.util.UUID

class CarteRepository @Inject constructor(private val dsl: DSLContext) {
    /**
     * Récupère les PEI dans une BBOX selon la zone de compétence
     */
    fun getPeiWithinZoneAndBbox(zoneId: UUID, bbox: Field<org.locationtech.jts.geom.Geometry?>): Collection<PeiCarte> {
        return dsl.select(PEI.GEOMETRIE.`as`("pointGeometrie"), PEI.ID.`as`("pointId"))
            .from(PEI)
            .innerJoin(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .innerJoin(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .innerJoin(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .join(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE)
                    .and(ST_Within(PEI.GEOMETRIE, bbox)),
            )
            .fetchInto()
    }

    /**
     * Récupère les PEI selon la zone de compétence
     */
    fun getPeiWithinZone(zoneId: UUID): Collection<PeiCarte> {
        return dsl.select(PEI.GEOMETRIE.`as`("pointGeometrie"), PEI.ID.`as`("pointId"))
            .from(PEI)
            .innerJoin(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .innerJoin(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .innerJoin(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .join(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE),
            )
            .fetchInto()
    }

    /**
     * Récupère les PEI en projet dans une BBOX selon l'étude
     */
    fun getPeiProjetWithinEtudeAndBbox(etudeId: UUID, bbox: Field<org.locationtech.jts.geom.Geometry?>): Collection<PeiProjetCarte> {
        return dsl.select(PEI_PROJET.GEOMETRIE.`as`("pointGeometrie"), PEI_PROJET.ID.`as`("pointId"))
            .from(PEI_PROJET)
            .where(
                PEI_PROJET.ETUDE_ID.eq(etudeId),
            ).and(
                ST_Within(PEI_PROJET.GEOMETRIE, bbox),
            )
            .fetchInto()
    }

    /**
     * Récupère les PEI en projet selon l'étude
     */
    fun getPeiProjetWithinEtude(etudeId: UUID): Collection<PeiProjetCarte> {
        return dsl.select(PEI_PROJET.GEOMETRIE.`as`("pointGeometrie"), PEI_PROJET.ID.`as`("pointId"))
            .from(PEI_PROJET)
            .where(
                PEI_PROJET.ETUDE_ID.eq(etudeId),
            )
            .fetchInto()
    }

    abstract class PointCarte {
        abstract val pointGeometrie: Point
        abstract val pointId: UUID
        abstract val typePointCarte: TypePointCarte
    }

    data class PeiCarte(
        override val pointGeometrie: Point,
        override val pointId: UUID,

        // TODO à compléter au besoin

    ) : PointCarte() {
        override val typePointCarte: TypePointCarte
            get() = TypePointCarte.PEI
    }

    data class PeiProjetCarte(
        override val pointGeometrie: Point,
        override val pointId: UUID,

        // TODO à compléter au besoin

    ) : PointCarte() {
        override val typePointCarte: TypePointCarte
            get() = TypePointCarte.PEI_PROJET
    }
}
