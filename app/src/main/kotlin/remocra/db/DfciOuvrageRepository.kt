package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.DfciOuvrageData
import remocra.data.GlobalData
import remocra.db.jooq.entrepotsig.tables.references.V_DFCI_OUVRAGE_SIG
import remocra.db.jooq.remocra.tables.pojos.DfciOuvrage
import remocra.db.jooq.remocra.tables.references.DFCI_OUVRAGE

class DfciOuvrageRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository(), SynchroRequete<DfciOuvrageData> {
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

    /**
     * Met à jour toute la table suivant les données du SIG
     */
    fun updateElementFromSig(): Int =
        dsl
            .update(DFCI_OUVRAGE)
            .set(DFCI_OUVRAGE.LIBELLE, V_DFCI_OUVRAGE_SIG.LIBELLE)
            .from(V_DFCI_OUVRAGE_SIG)
            .where(DFCI_OUVRAGE.CODE.eq(V_DFCI_OUVRAGE_SIG.CODE))
            .execute()

    override fun getAllNewElementsFromSig(): List<DfciOuvrageData> =
        dsl
            .select(
                V_DFCI_OUVRAGE_SIG.ID.`as`("id"),
                V_DFCI_OUVRAGE_SIG.CODE.`as`("code"),
                V_DFCI_OUVRAGE_SIG.LIBELLE.`as`("dfciOuvrageLibelle"),
            )
            .from(V_DFCI_OUVRAGE_SIG)
            .whereNotExists(
                dsl.selectOne()
                    .from(DFCI_OUVRAGE)
                    .where(V_DFCI_OUVRAGE_SIG.CODE.eq(DFCI_OUVRAGE.CODE)),
            )
            .fetchInto()

    /**
     * Insère un nouveau massif dans la table via un DfciOuvrageData
     */
    fun insertDfciOuvrageFromData(dfciOuvrageData: DfciOuvrageData): Int =
        dsl
            .insertInto(DFCI_OUVRAGE)
            .set(
                dsl.newRecord(
                    DFCI_OUVRAGE,
                    DfciOuvrage(
                        dfciOuvrageData.id,
                        dfciOuvrageData.dfciOuvrageLibelle,
                        dfciOuvrageData.code,
                    ),
                ),
            )
            .execute()

    /**
     * Drop la vue faisant le lien entre les schémas remocra et entrepotsig pour la table dfci_ouvrage
     */
    fun dropViewForEntrepotSig() =
        dsl.dropViewIfExists(V_DFCI_OUVRAGE_SIG).execute()

    override fun getAllOldElementsFromSig(): Map<String, DfciOuvrageData> {
        // On n'a pas besoin de récupérer les anciennes données, car aucun conflit ne peut avoir lieu pour le moment.
        return emptyMap()
    }

    override fun getAllOldElementsFromRemocra(): List<DfciOuvrageData> {
        // On n'a pas besoin de récupérer les anciennes données, car aucun conflit ne peut avoir lieu pour le moment.
        return emptyList()
    }
}
