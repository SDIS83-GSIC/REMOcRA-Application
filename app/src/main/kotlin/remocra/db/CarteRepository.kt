package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import remocra.data.enums.TypePointCarte
import remocra.db.jooq.couverturehydraulique.tables.references.PEI_PROJET
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.DEBIT_SIMULTANE
import remocra.db.jooq.remocra.tables.references.DEBIT_SIMULTANE_MESURE
import remocra.db.jooq.remocra.tables.references.L_DEBIT_SIMULTANE_MESURE_PEI
import remocra.db.jooq.remocra.tables.references.L_INDISPONIBILITE_TEMPORAIRE_PEI
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.PEI_PRESCRIT
import remocra.db.jooq.remocra.tables.references.PIBI
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
        val hasDebitSimultane = DSL.exists(
            DSL.select(L_DEBIT_SIMULTANE_MESURE_PEI.DEBIT_SIMULTANE_MESURE_ID).from(L_DEBIT_SIMULTANE_MESURE_PEI)
                .where(L_DEBIT_SIMULTANE_MESURE_PEI.PEI_ID.eq(PEI.ID)),
        ).`as`("hasDebitSimultane")
    }

    /**
     * Récupère les PEI dans une BBOX selon la zone de compétence
     */
    fun getPeiWithinZoneAndBbox(zoneId: UUID?, bbox: Field<Geometry?>, srid: Int, isSuperAdmin: Boolean): Collection<PeiCarte> {
        return dsl.select(
            ST_Transform(PEI.GEOMETRIE, srid).`as`("pointGeometrie"),
            PEI.ID.`as`("pointId"),
            hasIndispoTemp,
            hasTournee,
            hasDebitSimultane,
            NATURE_DECI.CODE,
            PIBI.TYPE_RESEAU_ID,
            PEI.NUMERO_COMPLET,
        )
            .from(PEI)
            .innerJoin(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .innerJoin(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .innerJoin(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .leftJoin(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .leftJoin(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE)
                        .and(ST_Within(ST_Transform(PEI.GEOMETRIE, srid), bbox)),
                    isSuperAdmin,
                ),
            )
            .fetchInto()
    }

    /**
     * Récupère les PEI selon la zone de compétence
     */
    fun getPeiWithinZone(zoneId: UUID?, srid: Int, isSuperAdmin: Boolean): Collection<PeiCarte> {
        return dsl.select(
            ST_Transform(PEI.GEOMETRIE, srid).`as`("pointGeometrie"),
            PEI.ID.`as`("pointId"),
            hasIndispoTemp,
            hasTournee,
            hasDebitSimultane,
            NATURE_DECI.CODE,
            PIBI.TYPE_RESEAU_ID,
            PEI.NUMERO_COMPLET,
        )
            .from(PEI)
            .innerJoin(COMMUNE).on(PEI.COMMUNE_ID.eq(COMMUNE.ID))
            .innerJoin(NATURE_DECI).on(PEI.NATURE_DECI_ID.eq(NATURE_DECI.ID))
            .innerJoin(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
            .leftJoin(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .leftJoin(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE), isSuperAdmin),
            )
            .fetchInto()
    }

    /**
     * Récupère les PEI en projet dans une BBOX selon l'étude
     */
    fun getPeiProjetWithinEtudeAndBbox(etudeId: UUID, bbox: Field<Geometry?>, srid: Int): Collection<PeiProjetCarte> {
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

    fun getPeiPrescritWithinZoneAndBbox(zoneId: UUID?, bbox: Field<Geometry?>?, srid: Int, isSuperAdmin: Boolean): Collection<PeiPrescritsCarte> {
        return dsl.select(
            ST_Transform(PEI_PRESCRIT.GEOMETRIE, srid).`as`("pointGeometrie"),
            PEI_PRESCRIT.ID.`as`("pointId"),
        ).from(PEI_PRESCRIT)
            .leftJoin(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(PEI_PRESCRIT.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE)
                        .and(bbox?.let { ST_Within(ST_Transform(PEI_PRESCRIT.GEOMETRIE, srid), bbox) }),
                    isSuperAdmin,
                ),
            )
            .fetchInto()
    }

    fun getDebitSimultaneWithinZoneAndBbox(zoneId: UUID?, bbox: Field<Geometry?>?, srid: Int, isSuperAdmin: Boolean): Collection<DebitSimultaneCarte> {
        return dsl.select(
            ST_Transform(DEBIT_SIMULTANE.GEOMETRIE, srid).`as`("pointGeometrie"),
            DEBIT_SIMULTANE.ID.`as`("pointId"),
            DEBIT_SIMULTANE.NUMERO_DOSSIER,
            PIBI.TYPE_RESEAU_ID.`as`("typeReseauId"),
            multiset(
                selectDistinct(PEI.NUMERO_COMPLET)
                    .from(PEI)
                    .join(L_DEBIT_SIMULTANE_MESURE_PEI)
                    .on(L_DEBIT_SIMULTANE_MESURE_PEI.PEI_ID.eq(PEI.ID))
                    .join(DEBIT_SIMULTANE_MESURE)
                    .on(L_DEBIT_SIMULTANE_MESURE_PEI.DEBIT_SIMULTANE_MESURE_ID.eq(DEBIT_SIMULTANE_MESURE.ID))
                    .where(DEBIT_SIMULTANE_MESURE.DEBIT_SIMULTANE_ID.eq(DEBIT_SIMULTANE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1()
                }?.joinToString()
            }.`as`("listeNumeroPei"),
        )
            .from(DEBIT_SIMULTANE)
            .join(DEBIT_SIMULTANE_MESURE)
            .on(DEBIT_SIMULTANE_MESURE.DEBIT_SIMULTANE_ID.eq(DEBIT_SIMULTANE.ID))
            .join(L_DEBIT_SIMULTANE_MESURE_PEI)
            .on(L_DEBIT_SIMULTANE_MESURE_PEI.DEBIT_SIMULTANE_MESURE_ID.eq(DEBIT_SIMULTANE_MESURE.ID))
            .join(PIBI)
            .on(PIBI.ID.eq(L_DEBIT_SIMULTANE_MESURE_PEI.PEI_ID))
            .leftJoin(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    ST_Within(DEBIT_SIMULTANE.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE)
                        .and(bbox?.let { ST_Within(ST_Transform(DEBIT_SIMULTANE.GEOMETRIE, srid), bbox) }),
                    isSuperAdmin,
                ),
            )
            .fetchInto()
    }

    abstract class PointCarte {
        abstract val pointGeometrie: Point
        abstract val pointId: UUID
        abstract val typePointCarte: TypePointCarte

        // Propriétés à afficher dans la tooltip
        abstract val propertiesToDisplay: String?
    }

    data class PeiCarte(
        override val pointGeometrie: Point,
        override val pointId: UUID,
        override val propertiesToDisplay: String? = null,
        val hasIndispoTemp: Boolean = false,
        val hasTournee: Boolean = false,
        val hasDebitSimultane: Boolean = false,
        val natureDeciCode: String,
        val pibiTypeReseauId: UUID?,
        val peiNumeroComplet: String,

        // TODO à compléter au besoin

    ) : PointCarte() {
        override val typePointCarte: TypePointCarte
            get() = TypePointCarte.PEI
    }

    data class PeiProjetCarte(
        override val pointGeometrie: Point,
        override val pointId: UUID,
        override val propertiesToDisplay: String? = null,

    ) : PointCarte() {
        override val typePointCarte: TypePointCarte
            get() = TypePointCarte.PEI_PROJET
    }

    data class PeiPrescritsCarte(
        override val pointGeometrie: Point,
        override val pointId: UUID,
        override val propertiesToDisplay: String? = null,

    ) : PointCarte() {
        override val typePointCarte: TypePointCarte
            get() = TypePointCarte.PEI_PRESCRIT
    }

    data class DebitSimultaneCarte(
        override val pointGeometrie: Point,
        override val pointId: UUID,
        val listeNumeroPei: String?,
        val debitSimultaneNumeroDossier: String,
        val typeReseauId: UUID,

        // TODO à compléter au besoin

    ) : PointCarte() {
        override val typePointCarte: TypePointCarte
            get() = TypePointCarte.DEBIT_SIMULTANE

        override val propertiesToDisplay: String =
            "Numéro du dossier : $debitSimultaneNumeroDossier \n Liste des PEI concernés : $listeNumeroPei"
    }
}
