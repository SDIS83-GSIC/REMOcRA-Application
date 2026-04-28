package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.VPeiLastMesures.Companion.V_PEI_LAST_MESURES
import remocra.db.jooq.remocra.tables.VPeiVisiteDate.Companion.V_PEI_VISITE_DATE

class MaterializedViewRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun refreshViewVisites() {
        dsl.execute("REFRESH MATERIALIZED VIEW ${V_PEI_VISITE_DATE.qualifiedName}")
    }
    fun refreshViewMesures() {
        dsl.execute("REFRESH MATERIALIZED VIEW ${V_PEI_LAST_MESURES.qualifiedName}")
    }
}
