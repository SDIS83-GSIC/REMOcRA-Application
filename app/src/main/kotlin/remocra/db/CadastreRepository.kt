package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.CadastreParcelle
import remocra.db.jooq.remocra.tables.pojos.CadastreSection
import remocra.db.jooq.remocra.tables.references.CADASTRE_PARCELLE
import remocra.db.jooq.remocra.tables.references.CADASTRE_SECTION
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_Within
import java.util.UUID

class CadastreRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getSectionByCommuneId(communeId: UUID, zoneIntegrationId: UUID): List<CadastreSection> =
        dsl.select(*CADASTRE_SECTION.fields())
            .from(CADASTRE_SECTION)
            .join(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(zoneIntegrationId))
            .join(COMMUNE).on(COMMUNE.ID.eq(CADASTRE_SECTION.COMMUNE_ID).and(COMMUNE.ID.eq(communeId)).and(ST_Within(COMMUNE.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE)))
            .fetchInto()

    fun getParcelleBySectionId(sectionId: UUID): List<CadastreParcelle> =
        dsl.selectFrom(CADASTRE_PARCELLE)
            .where(CADASTRE_PARCELLE.CADASTRE_SECTION_ID.eq(sectionId))
            .fetchInto()
}
