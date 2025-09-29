package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.JSONB
import remocra.db.jooq.remocra.tables.pojos.RisqueExpress
import remocra.db.jooq.remocra.tables.references.RISQUE_EXPRESS
import java.util.UUID

class RisqueExpressRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun insert(id: UUID, libelle: String, geometries: String) {
        dsl.insertInto(RISQUE_EXPRESS)
            .set(RISQUE_EXPRESS.ID, id)
            .set(RISQUE_EXPRESS.LIBELLE, libelle)
            .set(RISQUE_EXPRESS.GEOMETRIES, JSONB.valueOf(geometries))
            .execute()
    }

    fun getById(id: UUID): RisqueExpress {
        return dsl.selectFrom(RISQUE_EXPRESS)
            .where(RISQUE_EXPRESS.ID.eq(id))
            .fetchSingleInto()
    }
}
