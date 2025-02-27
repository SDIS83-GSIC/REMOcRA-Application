package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.db.jooq.remocra.tables.pojos.CarroyageDfci
import remocra.db.jooq.remocra.tables.references.CARROYAGE_DFCI
import remocra.utils.ST_MakePoint
import remocra.utils.ST_SetSrid
import remocra.utils.ST_Transform
import remocra.utils.ST_Within

class DfciRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getCarroyage(x: Double, y: Double, sridCoords: Int, sridSdis: Int): CarroyageDfci? =
        dsl.selectFrom(CARROYAGE_DFCI)
            .where(
                ST_Within(
                    ST_Transform(ST_SetSrid(ST_MakePoint(x.toFloat(), y.toFloat()), sridCoords), sridSdis),
                    CARROYAGE_DFCI.GEOMETRIE,
                ),
            )
            .orderBy(DSL.length(CARROYAGE_DFCI.COORDONNEEE).desc())
            .limit(1)
            .fetchAnyInto()
}
