package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.DfciDeb
import remocra.db.jooq.remocra.tables.references.DFCI_DEB
import java.util.UUID

/**
 * Classe permettant de faire les requêtes liées aux débroussaillements sur la bd
 */
class DfciDebRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Requête permettant de récupérer un débroussaillement suivant l'id donné
     * @param dfciDebId id du débroussaillement
     * @return les données du débroussaillement
     */
    fun getDfciDebById(dfciDebId: UUID): DfciDeb =
        dsl
            .selectFrom(DFCI_DEB)
            .where(DFCI_DEB.ID.eq(dfciDebId))
            .fetchSingleInto()

    fun updateDeb(dfciDeb: DfciDeb) =
        dsl
            .update(DFCI_DEB)
            .set(DFCI_DEB.LIBELLE, dfciDeb.dfciDebLibelle)
            .set(DFCI_DEB.ANNEE_TRAVAUX, dfciDeb.dfciDebAnneeTravaux)
            .set(DFCI_DEB.MOIS_TRAVAUX, dfciDeb.dfciDebMoisTravaux)
            .set(DFCI_DEB.ANNEE_PROGRAMME, dfciDeb.dfciDebAnneeProgramme)
            .set(DFCI_DEB.ANNEE_EDITION, dfciDeb.dfciDebAnneeEdition)
            .set(DFCI_DEB.LARGEUR, dfciDeb.dfciDebLargeur)
            .set(DFCI_DEB.SURFACE, dfciDeb.dfciDebSurface)
            .set(DFCI_DEB.REMARQUE, dfciDeb.dfciDebRemarque)
            .set(DFCI_DEB.TYPE, dfciDeb.dfciDebType)
            .set(DFCI_DEB.PROGRAMME, dfciDeb.dfciDebProgramme)
            .set(DFCI_DEB.TRAVAUX, dfciDeb.dfciDebTravaux)
            .set(DFCI_DEB.DFCI_MASSIF_ID, dfciDeb.dfciDebDfciMassifId)
            .set(DFCI_DEB.DFCI_OUVRAGE_ID, dfciDeb.dfciDebDfciOuvrageId)
            .set(DFCI_DEB.DFCI_PRESTATAIRE_ID, dfciDeb.dfciDebDfciPrestataireId)
            .set(DFCI_DEB.VERSION, dfciDeb.dfciDebVersion)
            .where(DFCI_DEB.ID.eq(dfciDeb.dfciDebId))
            .execute()
}
