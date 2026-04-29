package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.DFCI_CATEGORIE_PISTE

class DfciCategoriePisteRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAllDfciCategoriePisteIdCodeLibelle(): Collection<GlobalData.IdCodeLibelleData> =
        dsl
            .select(
                DFCI_CATEGORIE_PISTE.ID.`as`("id"),
                DFCI_CATEGORIE_PISTE.CODE.`as`("code"),
                DFCI_CATEGORIE_PISTE.LIBELLE.`as`("libelle"),
            )
            .from(DFCI_CATEGORIE_PISTE)
            .orderBy(DFCI_CATEGORIE_PISTE.LIBELLE)
            .fetchInto()
}
