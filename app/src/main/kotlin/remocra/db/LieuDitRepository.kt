package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.references.LIEU_DIT
import java.util.UUID

class LieuDitRepository @Inject constructor(private val dsl: DSLContext) {

    fun getLieuDitWithCommunePei(
        listIdCommune: List<UUID>,
    ): Collection<LieuDitWithCommune> =
        dsl.select(LIEU_DIT.ID.`as`("id"), LIEU_DIT.LIBELLE.`as`("libelle"), LIEU_DIT.COMMUNE_ID.`as`("communeId"))
            .from(LIEU_DIT)
            .where(LIEU_DIT.COMMUNE_ID.`in`(listIdCommune))
            .orderBy(LIEU_DIT.LIBELLE)
            .fetchInto()

    data class LieuDitWithCommune(
        val id: UUID,
        val libelle: String,
        val communeId: UUID,
    )
}
