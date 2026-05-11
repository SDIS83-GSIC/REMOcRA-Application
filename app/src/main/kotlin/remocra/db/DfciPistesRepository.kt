package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.pojos.DfciPiste
import remocra.db.jooq.remocra.tables.references.DFCI_PISTE
import remocra.utils.ST_DWithin
import remocra.utils.ST_Transform
import java.util.UUID

/**
 * Repository permettant de réaliser les différentes requêtes liées aux pistes
 */
class DfciPistesRepository @Inject constructor(
    private val dsl: DSLContext,
    private val appSettings: AppSettings,
) : AbstractRepository() {

    /**
     * Récupère la liste des id, code et libelle des pistes
     * @return la collection
     */
    fun getIdCodeLibelleDfciPiste(
        geometrie: Field<Geometry?>,
        tolerance: Int,
    ): Collection<GlobalData.IdCodeLibelleData> {
        val transfoGeom = ST_Transform(geometrie, appSettings.srid)
        return dsl
            .select(
                DFCI_PISTE.ID.`as`("id"),
                DFCI_PISTE.CODE.`as`("code"),
                DFCI_PISTE.LIBELLE.`as`("libelle"),
            )
            .from(DFCI_PISTE)
            .where(
                ST_DWithin(
                    DFCI_PISTE.GEOMETRIE,
                    transfoGeom,
                    tolerance.toDouble(),
                ),
            )
            .orderBy(DFCI_PISTE.LIBELLE)
            .fetchInto()
    }

    /**
     * Récupère les données de la piste dont l'id est passé en paramètre
     * @param pisteId id de la piste a récupérer
     */
    fun getDfciPistebyId(pisteId: UUID): DfciPiste =
        dsl
            .selectFrom(DFCI_PISTE)
            .where(DFCI_PISTE.ID.eq(pisteId))
            .fetchSingleInto()

    fun updateDfciPiste(data: DfciPiste) =
        dsl
            .update(DFCI_PISTE)
            .set(DFCI_PISTE.ADRESSE, data.dfciPisteAdresse)
            .set(DFCI_PISTE.ANNEE_PROGRAMME, data.dfciPisteAnneeProgramme)
            .set(DFCI_PISTE.ANNEE_TRAVAUX, data.dfciPisteAnneeTravaux)
            .set(DFCI_PISTE.CIRCULATION, data.dfciPisteCirculation)
            .set(DFCI_PISTE.DATE_GPS, data.dfciPisteDateGps)
            .set(DFCI_PISTE.LIBELLE, data.dfciPisteLibelle)
            .set(DFCI_PISTE.NUMERO, data.dfciPisteNumero)
            .set(DFCI_PISTE.OUVERTURE, data.dfciPisteOuverture)
            .set(DFCI_PISTE.EST_DFCI, data.dfciPisteEstDfci)
            .set(DFCI_PISTE.RETOURNEMENT, data.dfciPisteRetournement)
            .set(DFCI_PISTE.NUM_TRONCON, data.dfciPisteNumTroncon)
            .set(DFCI_PISTE.NUM_OBJECTIF, data.dfciPisteNumObjectif)
            .set(DFCI_PISTE.LIBELLE_OBJECTIF, data.dfciPisteLibelleObjectif)
            .set(DFCI_PISTE.IMPRATICABILITE, data.dfciPisteImpraticabilite)
            .set(DFCI_PISTE.TRAVAUX, data.dfciPisteTravaux)
            .set(DFCI_PISTE.VOIE, data.dfciPisteVoie)
            .set(DFCI_PISTE.IMPASSE, data.dfciPisteImpasse)
            .set(DFCI_PISTE.FONCIER, data.dfciPisteFoncier)
            .set(DFCI_PISTE.CROISEMENT, data.dfciPisteCroisement)
            .set(DFCI_PISTE.PROGRAMME, data.dfciPisteProgramme)
            .set(DFCI_PISTE.REMARQUE, data.dfciPisteRemarque)
            .set(DFCI_PISTE.PRATICABILITE, data.dfciPistePraticabilite)
            .set(DFCI_PISTE.DFCI_CATEGORIE_PISTE_ID, data.dfciPisteDfciCategoriePisteId)
            .set(DFCI_PISTE.DFCI_MASSIF_ID, data.dfciPisteDfciMassifId)
            .set(DFCI_PISTE.DFCI_PRESTATAIRE_ID, data.dfciPisteDfciPrestataireId)
            .set(DFCI_PISTE.DFCI_OUVRAGE_ID, data.dfciPisteDfciOuvrageId)
            .set(DFCI_PISTE.VERSION, data.dfciPisteVersion)
            .where(DFCI_PISTE.ID.eq(data.dfciPisteId))
            .execute()
}
