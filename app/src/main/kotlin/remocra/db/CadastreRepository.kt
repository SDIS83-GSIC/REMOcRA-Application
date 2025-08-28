package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.locationtech.jts.geom.Geometry
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.pojos.CadastreParcelle
import remocra.db.jooq.remocra.tables.pojos.CadastreSection
import remocra.db.jooq.remocra.tables.references.CADASTRE_PARCELLE
import remocra.db.jooq.remocra.tables.references.CADASTRE_SECTION
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.OLDEB
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_Distance
import remocra.utils.ST_Transform
import remocra.utils.ST_Within
import java.util.UUID

class CadastreRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getSectionByCommuneId(communeId: UUID, zoneIntegrationId: UUID?, isSuperAdmin: Boolean): List<CadastreSection> =
        dsl.select(*CADASTRE_SECTION.fields())
            .from(CADASTRE_SECTION)
            .let {
                if (zoneIntegrationId != null) {
                    it.join(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneIntegrationId))
                } else {
                    it
                }
            }
            .join(COMMUNE).on(
                COMMUNE.ID.eq(CADASTRE_SECTION.COMMUNE_ID)
                    .and(COMMUNE.ID.eq(communeId)).and(
                        repositoryUtils.checkIsSuperAdminOrCondition(
                            ST_Within(COMMUNE.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE).isTrue,
                            isSuperAdmin,
                        ),
                    ),
            )
            .fetchInto()

    fun getParcelleBySectionId(sectionId: UUID): List<CadastreParcelle> =
        dsl.selectFrom(CADASTRE_PARCELLE)
            .where(CADASTRE_PARCELLE.CADASTRE_SECTION_ID.eq(sectionId))
            .fetchInto()

    fun getParcelleWithOldBySectionId(sectionId: UUID): List<CadastreParcelle> =
        dsl.selectFrom(CADASTRE_PARCELLE)
            .where(CADASTRE_PARCELLE.CADASTRE_SECTION_ID.eq(sectionId))
            .and(CADASTRE_PARCELLE.ID.`in`(dsl.select(OLDEB.CADASTRE_PARCELLE_ID).from(OLDEB)))
            .fetchInto()

    /**
     * Récupère les parcelles disponibles à la sélection.
     * On inclut aussi `oldebId` pour récupérer sa parcelle lors de la modification.
     */
    fun getAvailableParcelleBySectionId(sectionId: UUID, oldebId: UUID?): List<CadastreParcelle> {
        return dsl.selectFrom(CADASTRE_PARCELLE)
            .where(
                CADASTRE_PARCELLE.CADASTRE_SECTION_ID.eq(sectionId).and(
                    CADASTRE_PARCELLE.ID.notIn(
                        dsl.select(OLDEB.CADASTRE_PARCELLE_ID)
                            .from(OLDEB),
                    ).or(
                        CADASTRE_PARCELLE.ID.eq(
                            dsl.select(OLDEB.CADASTRE_PARCELLE_ID)
                                .from(OLDEB).where(OLDEB.ID.eq(oldebId)),
                        ),
                    ),
                ),
            ).fetchInto()
    }

    fun getParcelleFromCoordsForCombo(
        geometry: Field<Geometry?>,
        limit: Int? = null,
    ): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(
            CADASTRE_PARCELLE.ID.`as`("id"),
            CADASTRE_SECTION.NUMERO.`as`("code"),
            CADASTRE_PARCELLE.NUMERO.`as`("libelle"),
        )
            .from(CADASTRE_PARCELLE)
            .join(CADASTRE_SECTION).on(CADASTRE_PARCELLE.CADASTRE_SECTION_ID.eq(CADASTRE_SECTION.ID))
            .orderBy(
                ST_Distance(
                    CADASTRE_PARCELLE.GEOMETRIE,
                    ST_Transform(geometry, SRID),
                ),
            )
            .limit(limit)
            .fetchInto()
}
