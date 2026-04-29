package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.DFCI_PRESTATAIRE

class DfciPrestataireRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAllDfciPrestataireIdCodeLibelle(): Collection<GlobalData.IdCodeLibelleData> =
        dsl
            .select(
                DFCI_PRESTATAIRE.ID.`as`("id"),
                DFCI_PRESTATAIRE.CODE.`as`("code"),
                DFCI_PRESTATAIRE.LIBELLE.`as`("libelle"),
            )
            .from(DFCI_PRESTATAIRE)
            .orderBy(DFCI_PRESTATAIRE.LIBELLE)
            .fetchInto()
}
