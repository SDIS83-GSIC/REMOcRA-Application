package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.DfciPanneau
import remocra.db.jooq.remocra.tables.references.DFCI_PANNEAU
import java.util.UUID

/**
 * Repository permettant d'effectuer les requêtes sur la table dfci_panneau
 */
class DfciPanneauRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Requête permettant de récupérer un panneau selon son id
     * @param dfciPanneauId id du panneau
     */
    fun getDfciPanneauById(dfciPanneauId: UUID): DfciPanneau =
        dsl.selectFrom(DFCI_PANNEAU)
            .where(DFCI_PANNEAU.ID.eq(dfciPanneauId))
            .fetchSingleInto()

    /**
     * Requête permettant de mettre à jour le panneau
     */
    fun updateDfciPanneau(data: DfciPanneau) =
        dsl
            .update(DFCI_PANNEAU)
            .set(DFCI_PANNEAU.TYPE, data.dfciPanneauType)
            .set(DFCI_PANNEAU.ETAT, data.dfciPanneauEtat)
            .set(DFCI_PANNEAU.BZERO, data.dfciPanneauBzero)
            .set(DFCI_PANNEAU.DATE_GPS, data.dfciPanneauDateGps)
            .set(DFCI_PANNEAU.POSITION, data.dfciPanneauPosition)
            .set(DFCI_PANNEAU.EQUIPEMENT, data.dfciPanneauEquipement)
            .set(DFCI_PANNEAU.DFCI_PISTE_ID, data.dfciPanneauDfciPisteId)
            .set(DFCI_PANNEAU.NUM_PISTE, data.dfciPanneauNumPiste)
            .set(DFCI_PANNEAU.LIBELLE_PISTE, data.dfciPanneauLibellePiste)
            .set(DFCI_PANNEAU.REMARQUE, data.dfciPanneauRemarque)
            .set(DFCI_PANNEAU.VERSION, data.dfciPanneauVersion)
            .where(DFCI_PANNEAU.ID.eq(data.dfciPanneauId))
            .execute()
}
