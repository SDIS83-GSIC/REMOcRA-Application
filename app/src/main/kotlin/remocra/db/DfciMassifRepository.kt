package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.DfciMassifData
import remocra.data.GlobalData
import remocra.db.jooq.entrepotsig.tables.references.V_DFCI_MASSIF_SIG
import remocra.db.jooq.remocra.tables.pojos.DfciMassif
import remocra.db.jooq.remocra.tables.references.DFCI_MASSIF

class DfciMassifRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository(), SynchroRequete<DfciMassifData> {

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

    /**
     * Met à jour toute la table suivant les données du SIG
     */
    fun updateElementFromSig(): Int =
        dsl.update(DFCI_MASSIF)
            .set(DFCI_MASSIF.LIBELLE, V_DFCI_MASSIF_SIG.LIBELLE)
            .from(V_DFCI_MASSIF_SIG)
            .where(DFCI_MASSIF.CODE.eq(V_DFCI_MASSIF_SIG.CODE))
            .execute()

    override fun getAllNewElementsFromSig(): List<DfciMassifData> =
        dsl
            .select(
                V_DFCI_MASSIF_SIG.ID.`as`("id"),
                V_DFCI_MASSIF_SIG.CODE.`as`("code"),
                V_DFCI_MASSIF_SIG.LIBELLE.`as`("dfciMassifLibelle"),
            )
            .from(V_DFCI_MASSIF_SIG)
            .whereNotExists(
                dsl.selectOne()
                    .from(DFCI_MASSIF)
                    .where(V_DFCI_MASSIF_SIG.CODE.eq(DFCI_MASSIF.CODE)),
            )
            .fetchInto()

    /**
     * Insère un nouveau massif dans la table via un DfciMassifData
     */
    fun insertDfciMassifFromData(dfciMassifData: DfciMassifData): Int =
        dsl
            .insertInto(DFCI_MASSIF)
            .set(
                dsl.newRecord(
                    DFCI_MASSIF,
                    DfciMassif(
                        dfciMassifData.id,
                        dfciMassifData.dfciMassifLibelle,
                        dfciMassifData.code,
                    ),
                ),
            )
            .execute()

    /**
     * Drop la vue faisant le lien entre les schémas remocra et entrepotsig pour la table dfci_massif
     */
    fun dropViewForEntrepotSig() =
        dsl.dropViewIfExists(V_DFCI_MASSIF_SIG).execute()

    override fun getAllOldElementsFromSig(): Map<String, DfciMassifData> {
        // On n'a pas besoin de récupérer les anciennes données, car aucun conflit ne peut avoir lieu pour le moment.
        return emptyMap()
    }

    override fun getAllOldElementsFromRemocra(): List<DfciMassifData> {
        // On n'a pas besoin de récupérer les anciennes données, car aucun conflit ne peut avoir lieu pour le moment.
        return emptyList()
    }
}
