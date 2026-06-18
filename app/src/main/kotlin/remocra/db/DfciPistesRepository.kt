package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.data.DfciPisteData
import remocra.data.GlobalData
import remocra.data.enums.DfciPisteColonne
import remocra.db.jooq.entrepotsig.tables.references.V_DFCI_PISTE_SIG
import remocra.db.jooq.remocra.tables.pojos.DfciPiste
import remocra.db.jooq.remocra.tables.references.DFCI_PISTE
import remocra.utils.ST_DWithin
import remocra.utils.ST_GeomFromWkt
import remocra.utils.ST_Transform
import java.util.LinkedHashMap
import java.util.UUID

/**
 * Repository permettant de réaliser les différentes requêtes liées aux pistes
 */
class DfciPistesRepository @Inject constructor(
    private val dsl: DSLContext,
    private val appSettings: AppSettings,
) : AbstractRepository(), SynchroRequete<DfciPisteData> {

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

    fun updateDfciPisteColumn(dfciPisteColumn: String, value: String?, dfciPisteId: UUID): Int {
        val setter = LinkedHashMap<Any?, Any?>()
        when (dfciPisteColumn) {
            DfciPisteColonne.DFCI_PISTE_ADRESSE.nomColonne -> setter[DFCI_PISTE.ADRESSE] = value
            DfciPisteColonne.DFCI_PISTE_ANNEE_PRGRAMME.nomColonne -> setter[DFCI_PISTE.ANNEE_PROGRAMME] = value
            DfciPisteColonne.DFCI_PISTE_ANNEE_TRAVAUX.nomColonne -> setter[DFCI_PISTE.ANNEE_TRAVAUX] = value
            DfciPisteColonne.DFCI_PISTE_CIRCULATION.nomColonne -> setter[DFCI_PISTE.CIRCULATION] = value
            DfciPisteColonne.DFCI_PISTE_DATE_GPS.nomColonne -> setter[DFCI_PISTE.DATE_GPS] = value
            DfciPisteColonne.DFCI_PISTE_LIBELLE.nomColonne -> setter[DFCI_PISTE.LIBELLE] = value
            DfciPisteColonne.DFCI_PISTE_NUMERO.nomColonne -> setter[DFCI_PISTE.NUMERO] = value
            DfciPisteColonne.DFCI_PISTE_OUVERTURE.nomColonne -> setter[DFCI_PISTE.OUVERTURE] = value
            DfciPisteColonne.DFCI_PISTE_PRATICABILITE.nomColonne -> setter[DFCI_PISTE.PRATICABILITE] = value
            DfciPisteColonne.DFCI_PISTE_EST_DFCI.nomColonne -> setter[DFCI_PISTE.EST_DFCI] = value
            DfciPisteColonne.DFCI_PISTE_RETOURNEMENT.nomColonne -> setter[DFCI_PISTE.RETOURNEMENT] = value
            DfciPisteColonne.DFCI_PISTE_NUM_TRONCON.nomColonne -> setter[DFCI_PISTE.NUM_TRONCON] = value
            DfciPisteColonne.DFCI_PISTE_NUM_OBJECTIF.nomColonne -> setter[DFCI_PISTE.NUM_OBJECTIF] = value
            DfciPisteColonne.DFCI_PISTE_LIBELLE_OBJECTIF.nomColonne -> setter[DFCI_PISTE.LIBELLE_OBJECTIF] = value
            DfciPisteColonne.DFCI_PISTE_GEOMETRIE.nomColonne -> {
                if (value != null) {
                    setter[DFCI_PISTE.GEOMETRIE] =
                        ST_Transform(ST_GeomFromWkt(value, appSettings.srid), appSettings.srid)
                }
            }
            DfciPisteColonne.DFCI_PISTE_REMARQUE.nomColonne -> setter[DFCI_PISTE.REMARQUE] = value
            DfciPisteColonne.DFCI_PISTE_IMPRATICABILITE.nomColonne -> setter[DFCI_PISTE.IMPRATICABILITE] = value
            DfciPisteColonne.DFCI_PISTE_TRAVAUX.nomColonne -> setter[DFCI_PISTE.TRAVAUX] = value
            DfciPisteColonne.DFCI_PISTE_VOIE.nomColonne -> setter[DFCI_PISTE.VOIE] = value
            DfciPisteColonne.DFCI_PISTE_IMPASSE.nomColonne -> setter[DFCI_PISTE.IMPASSE] = value
            DfciPisteColonne.DFCI_PISTE_FONCIER.nomColonne -> setter[DFCI_PISTE.FONCIER] = value
            DfciPisteColonne.DFCI_PISTE_CROISEMENT.nomColonne -> setter[DFCI_PISTE.CROISEMENT] = value
            DfciPisteColonne.DFCI_PISTE_PROGRAMME.nomColonne -> setter[DFCI_PISTE.PROGRAMME] = value
            DfciPisteColonne.DFCI_PISTE_DFCI_CATEGORIE_PISTE_ID.nomColonne -> setter[DFCI_PISTE.DFCI_CATEGORIE_PISTE_ID] = value
            DfciPisteColonne.DFCI_PISTE_DFCI_MASSIF_ID.nomColonne -> setter[DFCI_PISTE.DFCI_MASSIF_ID] = value
            DfciPisteColonne.DFCI_PISTE_DFCI_PRESTATAIRE_ID.nomColonne -> setter[DFCI_PISTE.DFCI_PRESTATAIRE_ID] = value
            DfciPisteColonne.DFCI_PISTE_DFCI_OUVRAGE_ID.nomColonne -> setter[DFCI_PISTE.DFCI_OUVRAGE_ID] = value
            DfciPisteColonne.DFCI_PISTE_CODE.nomColonne -> setter[DFCI_PISTE.CODE] = value
            DfciPisteColonne.DFCI_PISTE_VERSION.nomColonne -> setter[DFCI_PISTE.VERSION] = value
        }
        return dsl
            .update(DFCI_PISTE)
            .set(setter)
            .where(DFCI_PISTE.ID.eq(dfciPisteId))
            .execute()
    }

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

    /**
     * Drop la vue faisant le lien entre les schémas remocra et entrepotsig de la table dfci_piste
     */
    fun dropViewForEntrepotSig() =
        dsl.dropViewIfExists(V_DFCI_PISTE_SIG).execute()

    override fun getAllNewElementsFromSig(): List<DfciPisteData> = dsl
        .select(
            V_DFCI_PISTE_SIG.ID.`as`("id"),
            V_DFCI_PISTE_SIG.CODE.`as`("code"),
            V_DFCI_PISTE_SIG.VERSION.`as`("version"),
            V_DFCI_PISTE_SIG.ADRESSE.`as`("dfciPisteAdresse"),
            V_DFCI_PISTE_SIG.ANNEE_PROGRAMME.`as`("dfciPisteAnneeProgramme"),
            V_DFCI_PISTE_SIG.ANNEE_TRAVAUX.`as`("dfciPisteAnneeTravaux"),
            V_DFCI_PISTE_SIG.CIRCULATION.`as`("dfciPisteCirculation"),
            V_DFCI_PISTE_SIG.DATE_GPS.`as`("dfciPisteDateGps"),
            V_DFCI_PISTE_SIG.LIBELLE.`as`("dfciPisteLibelle"),
            V_DFCI_PISTE_SIG.NUMERO.`as`("dfciPisteNumero"),
            V_DFCI_PISTE_SIG.OUVERTURE.`as`("dfciPisteOuverture"),
            V_DFCI_PISTE_SIG.EST_DFCI.`as`("dfciPisteEstDfci"),
            V_DFCI_PISTE_SIG.RETOURNEMENT.`as`("dfciPisteRetournement"),
            V_DFCI_PISTE_SIG.NUM_TRONCON.`as`("dfciPisteNumTroncon"),
            V_DFCI_PISTE_SIG.NUM_OBJECTIF.`as`("dfciPisteNumObjectif"),
            V_DFCI_PISTE_SIG.LIBELLE_OBJECTIF.`as`("dfciPisteLibelleObjectif"),
            V_DFCI_PISTE_SIG.GEOMETRIE.`as`("dfciPisteGeometrie"),
            V_DFCI_PISTE_SIG.IMPRATICABILITE.`as`("dfciPisteImpraticabilite"),
            V_DFCI_PISTE_SIG.TRAVAUX.`as`("dfciPisteTravaux"),
            V_DFCI_PISTE_SIG.VOIE.`as`("dfciPisteVoie"),
            V_DFCI_PISTE_SIG.IMPASSE.`as`("dfciPisteImpasse"),
            V_DFCI_PISTE_SIG.FONCIER.`as`("dfciPisteFoncier"),
            V_DFCI_PISTE_SIG.CROISEMENT.`as`("dfciPisteCroisement"),
            V_DFCI_PISTE_SIG.PROGRAMME.`as`("dfciPisteProgramme"),
            V_DFCI_PISTE_SIG.PRATICABILITE.`as`("dfciPistePraticabilite"),
            V_DFCI_PISTE_SIG.REMARQUE.`as`("dfciPisteRemarque"),
            V_DFCI_PISTE_SIG.DFCI_CATEGORIE_PISTE_ID.`as`("dfciPisteDfciCategoriePisteId"),
            V_DFCI_PISTE_SIG.DFCI_MASSIF_ID.`as`("dfciPisteDfciMassifId"),
            V_DFCI_PISTE_SIG.DFCI_PRESTATAIRE_ID.`as`("dfciPisteDfciPrestataireId"),
            V_DFCI_PISTE_SIG.DFCI_OUVRAGE_ID.`as`("dfciPisteDfciOuvrageId"),
        )
        .from(V_DFCI_PISTE_SIG)
        .whereNotExists(
            dsl.selectOne()
                .from(DFCI_PISTE)
                .where(DFCI_PISTE.CODE.eq(V_DFCI_PISTE_SIG.CODE)),
        )
        .fetchInto()

    // pour le Map ; on est sur que code est non nul
    @Suppress("UNCHECKED_CAST")
    override fun getAllOldElementsFromSig(): Map<String, DfciPisteData> =
        dsl
            .select(
                V_DFCI_PISTE_SIG.ID.`as`("id"),
                V_DFCI_PISTE_SIG.CODE.`as`("code"),
                V_DFCI_PISTE_SIG.VERSION.`as`("version"),
                V_DFCI_PISTE_SIG.ADRESSE.`as`("dfciPisteAdresse"),
                V_DFCI_PISTE_SIG.ANNEE_PROGRAMME.`as`("dfciPisteAnneeProgramme"),
                V_DFCI_PISTE_SIG.ANNEE_TRAVAUX.`as`("dfciPisteAnneeTravaux"),
                V_DFCI_PISTE_SIG.CIRCULATION.`as`("dfciPisteCirculation"),
                V_DFCI_PISTE_SIG.DATE_GPS.`as`("dfciPisteDateGps"),
                V_DFCI_PISTE_SIG.LIBELLE.`as`("dfciPisteLibelle"),
                V_DFCI_PISTE_SIG.NUMERO.`as`("dfciPisteNumero"),
                V_DFCI_PISTE_SIG.OUVERTURE.`as`("dfciPisteOuverture"),
                V_DFCI_PISTE_SIG.EST_DFCI.`as`("dfciPisteEstDfci"),
                V_DFCI_PISTE_SIG.RETOURNEMENT.`as`("dfciPisteRetournement"),
                V_DFCI_PISTE_SIG.NUM_TRONCON.`as`("dfciPisteNumTroncon"),
                V_DFCI_PISTE_SIG.NUM_OBJECTIF.`as`("dfciPisteNumObjectif"),
                V_DFCI_PISTE_SIG.LIBELLE_OBJECTIF.`as`("dfciPisteLibelleObjectif"),
                V_DFCI_PISTE_SIG.GEOMETRIE.`as`("dfciPisteGeometrie"),
                V_DFCI_PISTE_SIG.IMPRATICABILITE.`as`("dfciPisteImpraticabilite"),
                V_DFCI_PISTE_SIG.TRAVAUX.`as`("dfciPisteTravaux"),
                V_DFCI_PISTE_SIG.VOIE.`as`("dfciPisteVoie"),
                V_DFCI_PISTE_SIG.IMPASSE.`as`("dfciPisteImpasse"),
                V_DFCI_PISTE_SIG.FONCIER.`as`("dfciPisteFoncier"),
                V_DFCI_PISTE_SIG.CROISEMENT.`as`("dfciPisteCroisement"),
                V_DFCI_PISTE_SIG.PROGRAMME.`as`("dfciPisteProgramme"),
                V_DFCI_PISTE_SIG.PRATICABILITE.`as`("dfciPistePraticabilite"),
                V_DFCI_PISTE_SIG.REMARQUE.`as`("dfciPisteRemarque"),
                V_DFCI_PISTE_SIG.DFCI_CATEGORIE_PISTE_ID.`as`("dfciPisteDfciCategoriePisteId"),
                V_DFCI_PISTE_SIG.DFCI_MASSIF_ID.`as`("dfciPisteDfciMassifId"),
                V_DFCI_PISTE_SIG.DFCI_PRESTATAIRE_ID.`as`("dfciPisteDfciPrestataireId"),
                V_DFCI_PISTE_SIG.DFCI_OUVRAGE_ID.`as`("dfciPisteDfciOuvrageId"),
            )
            .from(V_DFCI_PISTE_SIG)
            .whereExists(
                dsl.selectOne()
                    .from(DFCI_PISTE)
                    .where(DFCI_PISTE.CODE.eq(V_DFCI_PISTE_SIG.CODE)),
            )
            .fetchMap(V_DFCI_PISTE_SIG.CODE.`as`("code"), DfciPisteData::class.java)
            as Map<String, DfciPisteData>

    override fun getAllOldElementsFromRemocra(): List<DfciPisteData> = dsl
        .select(
            DFCI_PISTE.ID.`as`("id"),
            DFCI_PISTE.CODE.`as`("code"),
            DFCI_PISTE.VERSION.`as`("version"),
            DFCI_PISTE.ADRESSE,
            DFCI_PISTE.ANNEE_PROGRAMME,
            DFCI_PISTE.ANNEE_TRAVAUX,
            DFCI_PISTE.CIRCULATION,
            DFCI_PISTE.DATE_GPS,
            DFCI_PISTE.LIBELLE,
            DFCI_PISTE.NUMERO,
            DFCI_PISTE.OUVERTURE,
            DFCI_PISTE.EST_DFCI,
            DFCI_PISTE.RETOURNEMENT,
            DFCI_PISTE.NUM_TRONCON,
            DFCI_PISTE.NUM_OBJECTIF,
            DFCI_PISTE.LIBELLE_OBJECTIF,
            DFCI_PISTE.GEOMETRIE,
            DFCI_PISTE.IMPRATICABILITE,
            DFCI_PISTE.TRAVAUX,
            DFCI_PISTE.VOIE,
            DFCI_PISTE.IMPASSE,
            DFCI_PISTE.FONCIER,
            DFCI_PISTE.CROISEMENT,
            DFCI_PISTE.PROGRAMME,
            DFCI_PISTE.PRATICABILITE,
            DFCI_PISTE.REMARQUE,
            DFCI_PISTE.DFCI_CATEGORIE_PISTE_ID,
            DFCI_PISTE.DFCI_MASSIF_ID,
            DFCI_PISTE.DFCI_PRESTATAIRE_ID,
            DFCI_PISTE.DFCI_OUVRAGE_ID,
        )
        .from(DFCI_PISTE)
        .whereExists(
            dsl.selectOne()
                .from(V_DFCI_PISTE_SIG)
                .where(V_DFCI_PISTE_SIG.CODE.eq(DFCI_PISTE.CODE)),
        )
        .fetchInto()

    /**
     * Met à jour la piste via la classe DfciPisteData
     */
    fun updateDfciPisteFromData(dfciPisteData: DfciPisteData) =
        updateDfciPiste(dfciPisteData.convertPisteDataToPojo())

    /**
     * Insère une piste dans la table via la classe DfciPisteData
     */
    fun insertDfciPisteFromData(dfciPisteData: DfciPisteData): Int =
        dsl.insertInto(DFCI_PISTE).set(dsl.newRecord(DFCI_PISTE, dfciPisteData.convertPisteDataToPojo())).execute()
}
