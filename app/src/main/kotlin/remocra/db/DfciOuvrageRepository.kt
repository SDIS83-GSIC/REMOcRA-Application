package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.DFCI_OUVRAGE

class DfciOuvrageRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAllDfciOuvrageIdCodeLibelle(): Collection<GlobalData.IdCodeLibelleData> =
        dsl
            .select(
                DFCI_OUVRAGE.ID.`as`("id"),
                DFCI_OUVRAGE.CODE.`as`("code"),
                DFCI_OUVRAGE.LIBELLE.`as`("libelle"),
            )
            .from(DFCI_OUVRAGE)
            .orderBy(DFCI_OUVRAGE.LIBELLE)
            .fetchInto()
}
