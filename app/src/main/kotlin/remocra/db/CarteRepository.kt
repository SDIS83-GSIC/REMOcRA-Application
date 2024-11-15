package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import remocra.data.enums.TypePointCarte
import remocra.db.jooq.couverturehydraulique.tables.references.PEI_PROJET
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.L_INDISPONIBILITE_TEMPORAIRE_PEI
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_Transform
import remocra.utils.ST_Within
import java.util.UUID

class CarteRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        val hasIndispoTemp = DSL.exists(
            DSL.select(L_INDISPONIBILITE_TEMPORAIRE_PEI.INDISPONIBILITE_TEMPORAIRE_ID).from(L_INDISPONIBILITE_TEMPORAIRE_PEI)
                .where(L_INDISPONIBILITE_TEMPORAIRE_PEI.PEI_ID.eq(PEI.ID)),
        ).`as`("hasIndispoTemp")
        val hasTournee = DSL.exists(
            DSL.select(L_TOURNEE_PEI.TOURNEE_ID).from(L_TOURNEE_PEI)
                .where(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID)),
        ).`as`("hasTournee")
    }

    /**
     * Récupère les PEI dans une BBOX selon la zone de compétence
     */
    fun getPeiWithinZoneAndBbox(zoneId: UUID, bbox: Field<Geometry?>, srid: Int): Collection<PeiCarte> {
        return dsl.select(
            ST_Transform(PEI.GEOMETRIE, srid).`as`("pointGeometrie"),
            PEI.ID.`as`("pointId"),
            hasIndispoTemp,
            hasTournee,
            NATURE_DECI.CODE,
        )
            .from(PEI)
            .innerJoin(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .innerJoin(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .innerJoin(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .join(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE)
                    .and(ST_Within(ST_Transform(PEI.GEOMETRIE, srid), bbox)),
            )
            .fetchInto()
    }

    /**
     * Récupère les PEI selon la zone de compétence
     */
    fun getPeiWithinZone(zoneId: UUID, srid: Int): Collection<PeiCarte> {
        return dsl.select(
            ST_Transform(PEI.GEOMETRIE, srid).`as`("pointGeometrie"),
            PEI.ID.`as`("pointId"),
            hasIndispoTemp,
            hasTournee,
            NATURE_DECI.CODE,
        )
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
    fun getPeiProjetWithinEtudeAndBbox(etudeId: UUID, bbox: Field<org.locationtech.jts.geom.Geometry?>, srid: Int): Collection<PeiProjetCarte> {
        return dsl.select(ST_Transform(PEI_PROJET.GEOMETRIE, srid).`as`("pointGeometrie"), PEI_PROJET.ID.`as`("pointId"))
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
    fun getPeiProjetWithinEtude(etudeId: UUID, srid: Int): Collection<PeiProjetCarte> {
        return dsl.select(ST_Transform(PEI.GEOMETRIE, srid).`as`("pointGeometrie"), PEI_PROJET.ID.`as`("pointId"))
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
        val hasIndispoTemp: Boolean = false,
        val hasTournee: Boolean = false,
        val natureDeciCode: String,

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
