package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE

class GestionnaireRepository @Inject constructor(private val dsl: DSLContext) {

    fun getAll(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(GESTIONNAIRE.ID.`as`("id"), GESTIONNAIRE.CODE.`as`("code"), GESTIONNAIRE.LIBELLE.`as`("libelle"))
            .from(GESTIONNAIRE)
            .where(GESTIONNAIRE.ACTIF)
            .fetchInto()
}
