package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.THEMATIQUE

class ThematiqueRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            THEMATIQUE.ID.`as`("id"),
            THEMATIQUE.CODE.`as`("code"),
            THEMATIQUE.LIBELLE.`as`("libelle"),
        )
            .from(THEMATIQUE)
            .orderBy(THEMATIQUE.LIBELLE)
            .fetchInto()
}
