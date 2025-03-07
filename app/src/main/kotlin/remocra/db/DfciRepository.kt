package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.tables.pojos.CarroyageDfci
import remocra.db.jooq.remocra.tables.references.CARROYAGE_DFCI
import remocra.utils.ST_Transform
import remocra.utils.ST_Within

class DfciRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getCarroyage(geometry: Field<Geometry?>): CarroyageDfci? =
        dsl.selectFrom(CARROYAGE_DFCI)
            .where(
                ST_Within(
                    ST_Transform(geometry, SRID),
                    CARROYAGE_DFCI.GEOMETRIE,
                ),
            )
            .orderBy(DSL.length(CARROYAGE_DFCI.COORDONNEEE).desc())
            .limit(1)
            .fetchAnyInto()
}
