package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.DfciPrestataireData
import remocra.data.GlobalData
import remocra.db.jooq.entrepotsig.tables.references.V_DFCI_PRESTATAIRE_SIG
import remocra.db.jooq.remocra.tables.pojos.DfciPrestataire
import remocra.db.jooq.remocra.tables.references.DFCI_PRESTATAIRE

class DfciPrestataireRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository(), SynchroRequete<DfciPrestataireData> {

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

    /**
     * Met à jour toute la table suivant les données du SIG
     */
    fun updateDfciPrestataireFromSig(): Int =
        dsl
            .update(DFCI_PRESTATAIRE)
            .set(DFCI_PRESTATAIRE.LIBELLE, V_DFCI_PRESTATAIRE_SIG.LIBELLE)
            .from(V_DFCI_PRESTATAIRE_SIG)
            .where(DFCI_PRESTATAIRE.CODE.eq(V_DFCI_PRESTATAIRE_SIG.CODE))
            .execute()

    override fun getAllNewElementsFromSig(): List<DfciPrestataireData> =
        dsl
            .select(
                V_DFCI_PRESTATAIRE_SIG.ID.`as`("id"),
                V_DFCI_PRESTATAIRE_SIG.CODE.`as`("code"),
                V_DFCI_PRESTATAIRE_SIG.LIBELLE.`as`("dfciPrestataireLibelle"),
            )
            .from(V_DFCI_PRESTATAIRE_SIG)
            .whereNotExists(
                dsl.selectOne()
                    .from(DFCI_PRESTATAIRE)
                    .where(V_DFCI_PRESTATAIRE_SIG.CODE.eq(DFCI_PRESTATAIRE.CODE)),
            )
            .fetchInto()

    /**
     * Insère un nouveau massif dans la table via un DfciPrestataireData
     */
    fun insertDfciPrestataireFromData(dfciPrestataireData: DfciPrestataireData): Int =
        dsl
            .insertInto(DFCI_PRESTATAIRE)
            .set(
                dsl.newRecord(
                    DFCI_PRESTATAIRE,
                    DfciPrestataire(
                        dfciPrestataireData.id,
                        dfciPrestataireData.dfciPrestataireLibelle,
                        dfciPrestataireData.code,
                    ),
                ),
            )
            .execute()

    override fun getAllOldElementsFromSig(): Map<String, DfciPrestataireData> {
        // On n'a pas besoin de récupérer les anciennes données, car aucun conflit ne peut avoir lieu pour le moment.
        return emptyMap()
    }

    override fun getAllOldElementsFromRemocra(): List<DfciPrestataireData> {
        // On n'a pas besoin de récupérer les anciennes données, car aucun conflit ne peut avoir lieu pour le moment.
        return emptyList()
    }

    /**
     * Drop la vue faisant le lien entre les schémas remocra et entrepotsig pour la table dfci_prestataire
     */
    fun dropViewForEntrepotSig() =
        dsl.dropViewIfExists(V_DFCI_PRESTATAIRE_SIG).execute()
}
