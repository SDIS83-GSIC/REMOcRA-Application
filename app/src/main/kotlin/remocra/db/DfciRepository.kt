package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.tables.pojos.CarroyageDfci
import remocra.db.jooq.remocra.tables.references.CARROYAGE_DFCI
import remocra.utils.ST_Transform
import remocra.utils.ST_Within

class DfciRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getCarroyage(x: Double, y: Double, srid: Int): CarroyageDfci? =
        dsl.selectFrom(CARROYAGE_DFCI)
            .where(ST_Within(DSL.field("'SRID=$srid;POINT($x $y)'", Geometry::class.java), ST_Transform(CARROYAGE_DFCI.GEOMETRIE, srid)))
            .orderBy(DSL.length(CARROYAGE_DFCI.COORDONNEEE).desc())
            .limit(1)
            .fetchAnyInto()
}
