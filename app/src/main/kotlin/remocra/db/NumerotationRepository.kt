package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.PEI
import java.util.UUID

class NumerotationRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getNextPeiNumeroInterneMethodeA(peiCommuneId: UUID): Int = dsl
        .select(DSL.coalesce(DSL.max(PEI.NUMERO_INTERNE), 0))
        .from(PEI)
        .where(PEI.COMMUNE_ID.eq(peiCommuneId))
        .fetchSingleInto()

    fun getListPeiNumeroInterneMethodeC(peiCommuneId: UUID, peiNatureId: UUID): Collection<Int> = dsl
        .select(PEI.NUMERO_INTERNE)
        .from(PEI)
        .where(PEI.COMMUNE_ID.eq(peiCommuneId))
        .and(PEI.NATURE_ID.eq(peiNatureId))
        .fetchInto()

    /**
     * Retourne les numéros internes des PEI dont les critères passés sont non nuls
     */
    fun getListPeiNumeroInterne(typePei: TypePei?, peiNatureId: UUID?, peiCommuneId: UUID?, peiZoneSpecialeId: UUID?, peiNatureDeciId: UUID?): Collection<Int> = dsl
        .select(PEI.NUMERO_INTERNE)
        .from(PEI)
        .leftJoin(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
        .where(
            DSL.and(
                listOfNotNull(
                    typePei?.let { NATURE.TYPE_PEI.eq(typePei) },
                    peiNatureId?.let { PEI.NATURE_ID.eq(peiNatureId) },
                    peiCommuneId?.let { PEI.COMMUNE_ID.eq(peiCommuneId) },
                    peiNatureDeciId?.let { PEI.NATURE_DECI_ID.eq(peiNatureDeciId) },
                    peiZoneSpecialeId?.let { PEI.ZONE_SPECIALE_ID.eq(peiZoneSpecialeId) },
                ),
            ),
        )
        .orderBy(PEI.NUMERO_INTERNE)
        .fetchInto()

    fun getMaxPeiNumeroInterne(): Int = dsl.select(DSL.max(PEI.NUMERO_INTERNE)).from(PEI).fetchSingleInto()
}
