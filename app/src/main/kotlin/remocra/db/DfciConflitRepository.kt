package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.DfciConflit
import remocra.db.jooq.remocra.tables.references.DFCI_CONFLIT
import java.util.UUID

class DfciConflitRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Insère un nouveau conflit dans la table
     */
    fun insertDfciConflit(dfciConflit: DfciConflit) =
        dsl.insertInto(DFCI_CONFLIT).set(dsl.newRecord(DFCI_CONFLIT, dfciConflit)).execute()

    /**
     * Vérifie si un conflit existe déjà dans la table
     */
    fun getExistDfciConflit(elementId: UUID, champ: String): Boolean =
        dsl
            .fetchExists(
                DFCI_CONFLIT,
                DFCI_CONFLIT.ELEMENT_ID.eq(elementId),
                DFCI_CONFLIT.CHAMP.eq(champ),
            )

    /**
     * Met à jour le conflit existant avec ses nouvelles données
     */
    fun updateDfciConflit(dfciConflit: DfciConflit) =
        dsl
            .update(DFCI_CONFLIT)
            .set(DFCI_CONFLIT.VALEUR_REMOCRA, dfciConflit.dfciConflitValeurRemocra)
            .set(DFCI_CONFLIT.VALEUR_SIG, dfciConflit.dfciConflitValeurSig)
            .set(DFCI_CONFLIT.DATE, dfciConflit.dfciConflitDate)
            .where(DFCI_CONFLIT.ELEMENT_ID.eq(dfciConflit.dfciConflitElementId))
            .and(DFCI_CONFLIT.CHAMP.eq(dfciConflit.dfciConflitChamp))
            .execute()
}
