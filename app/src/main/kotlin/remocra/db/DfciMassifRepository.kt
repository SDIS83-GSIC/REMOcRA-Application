package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.DFCI_MASSIF

class DfciMassifRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAllDfciMassifIdCodeLibelle(): Collection<GlobalData.IdCodeLibelleData> =
        dsl
            .select(
                DFCI_MASSIF.ID.`as`("id"),
                DFCI_MASSIF.CODE.`as`("code"),
                DFCI_MASSIF.LIBELLE.`as`("libelle"),
            )
            .from(DFCI_MASSIF)
            .orderBy(DFCI_MASSIF.LIBELLE)
            .fetchInto()
}
