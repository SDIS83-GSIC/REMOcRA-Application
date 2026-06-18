package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.DfciCategoriePisteData
import remocra.data.GlobalData
import remocra.db.jooq.entrepotsig.tables.references.V_DFCI_CATEGORIE_PISTE_SIG
import remocra.db.jooq.remocra.tables.pojos.DfciCategoriePiste
import remocra.db.jooq.remocra.tables.references.DFCI_CATEGORIE_PISTE

class DfciCategoriePisteRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository(), SynchroRequete<DfciCategoriePisteData> {

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

    /**
     * Met à jour toute la table suivant les données du SIG
     */
    fun updateElementFromSig(): Int =
        dsl
            .update(DFCI_CATEGORIE_PISTE)
            .set(DFCI_CATEGORIE_PISTE.LIBELLE, V_DFCI_CATEGORIE_PISTE_SIG.LIBELLE)
            .from(V_DFCI_CATEGORIE_PISTE_SIG)
            .where(DFCI_CATEGORIE_PISTE.CODE.eq(V_DFCI_CATEGORIE_PISTE_SIG.CODE))
            .execute()

    override fun getAllNewElementsFromSig(): List<DfciCategoriePisteData> =
        dsl
            .select(
                V_DFCI_CATEGORIE_PISTE_SIG.ID.`as`("id"),
                V_DFCI_CATEGORIE_PISTE_SIG.CODE.`as`("code"),
                V_DFCI_CATEGORIE_PISTE_SIG.LIBELLE.`as`("dfciCategoriePisteLibelle"),
            )
            .from(V_DFCI_CATEGORIE_PISTE_SIG)
            .whereNotExists(
                dsl.selectOne()
                    .from(DFCI_CATEGORIE_PISTE)
                    .where(V_DFCI_CATEGORIE_PISTE_SIG.CODE.eq(DFCI_CATEGORIE_PISTE.CODE)),
            )
            .fetchInto()

    /**
     * Insère une nouvelle catégorie de piste via sa classe DfciCategoriePisteData
     */
    fun insertDfciCategoriePisteFromData(dfciCategoriePisteData: DfciCategoriePisteData): Int =
        dsl
            .insertInto(DFCI_CATEGORIE_PISTE)
            .set(
                dsl.newRecord(
                    DFCI_CATEGORIE_PISTE,
                    DfciCategoriePiste(
                        dfciCategoriePisteData.id,
                        dfciCategoriePisteData.dfciCategoriePisteLibelle,
                        dfciCategoriePisteData.code,
                    ),
                ),
            )
            .execute()

    override fun getAllOldElementsFromSig(): Map<String, DfciCategoriePisteData> {
        // On n'a pas besoin de récupérer les anciennes données, car aucun conflit ne peut avoir lieu pour le moment.
        return emptyMap()
    }

    override fun getAllOldElementsFromRemocra(): List<DfciCategoriePisteData> {
        // On n'a pas besoin de récupérer les anciennes données, car aucun conflit ne peut avoir lieu pour le moment.
        return emptyList()
    }

    /**
     * Drop la vue faisant le lien entre les schémas remocra et sig pour la table dfci_categorie_piste
     */
    fun dropViewForEntrepotSig() =
        dsl.dropViewIfExists(V_DFCI_CATEGORIE_PISTE_SIG).execute()
}
