package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import java.util.UUID

class NomenclatureAnomalieCategorieRepository
@Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {
    fun getListeOrdonnee(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(ANOMALIE_CATEGORIE.ID.`as`("id"), ANOMALIE_CATEGORIE.CODE.`as`("code"), ANOMALIE_CATEGORIE.LIBELLE.`as`("libelle"))
            .from(ANOMALIE_CATEGORIE)
            .orderBy(ANOMALIE_CATEGORIE.ORDRE)
            .fetchInto()

    fun updateOrdre(listeAnomalieCategorie: List<UUID>?) =
        dsl.batch(
            listeAnomalieCategorie?.mapIndexed { idx, it ->
                DSL.update(ANOMALIE_CATEGORIE)
                    .set(ANOMALIE_CATEGORIE.ORDRE, idx)
                    .where(ANOMALIE_CATEGORIE.ID.eq(it))
            },
        ).execute()
}
