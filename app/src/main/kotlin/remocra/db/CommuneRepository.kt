package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.db.jooq.tables.pojos.Commune
import remocra.db.jooq.tables.references.COMMUNE
import java.util.UUID

class CommuneRepository @Inject constructor(private val dsl: DSLContext) {
    fun getMapById(): Map<UUID, Commune> = dsl.selectFrom(COMMUNE).fetchInto<Commune>().associateBy { it.communeId }

    fun getAll(codeInsee: String?, libelle: String?, limit: Int?, offset: Int?): Collection<Commune> =
        dsl.selectFrom(COMMUNE)
            .where(getConditions(codeInsee, libelle))
            .orderBy(COMMUNE.INSEE)
            .limit(limit)
            .offset(offset)
            .fetchInto()

    private fun getConditions(codeInsee: String?, libelle: String?): Condition {
        var condition: Condition = DSL.trueCondition()
        if (codeInsee != null) {
            condition = condition.and(COMMUNE.INSEE.eq(codeInsee))
        }
        if (libelle != null) {
            condition = condition.and(COMMUNE.LIBELLE.likeIgnoreCase("%$libelle%"))
        }
        return condition
    }
}
