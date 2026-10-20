package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.data.DfciDebData
import remocra.data.enums.DfciDebColonne
import remocra.db.jooq.entrepotsig.tables.references.V_DFCI_DEB_SIG
import remocra.db.jooq.remocra.tables.pojos.DfciDeb
import remocra.db.jooq.remocra.tables.references.DFCI_DEB
import remocra.utils.ST_GeomFromWkt
import remocra.utils.ST_Transform
import java.util.LinkedHashMap
import java.util.UUID

/**
 * Classe permettant de faire les requêtes liées aux débroussaillements sur la bd
 */
class DfciDebRepository @Inject constructor(
    private val dsl: DSLContext,
    private val appSettings: AppSettings,
) : AbstractRepository(), SynchroRequete<DfciDebData> {

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

    fun updateDfciDebColumn(dfcidebColumn: String, value: String?, dfciDebId: UUID): Int {
        val setter = LinkedHashMap<Any?, Any?>()
        when (dfcidebColumn) {
            DfciDebColonne.DFCI_DEB_LIBELLE.nomColonne -> setter[DFCI_DEB.LIBELLE] = value
            DfciDebColonne.DFCI_DEB_ANNEE_PRGRAMME.nomColonne -> setter[DFCI_DEB.ANNEE_PROGRAMME] = value
            DfciDebColonne.DFCI_DEB_ANNEE_TRAVAUX.nomColonne -> setter[DFCI_DEB.ANNEE_TRAVAUX] = value
            DfciDebColonne.DFCI_DEB_MOIS_TRAVAUX.nomColonne -> setter[DFCI_DEB.MOIS_TRAVAUX] = value
            DfciDebColonne.DFCI_DEB_ANNEE_EDITION.nomColonne -> setter[DFCI_DEB.ANNEE_EDITION] = value
            DfciDebColonne.DFCI_DEB_LARGEUR.nomColonne -> setter[DFCI_DEB.LARGEUR] = value
            DfciDebColonne.DFCI_DEB_SURFACE.nomColonne -> setter[DFCI_DEB.SURFACE] = value
            DfciDebColonne.DFCI_DEB_TYPE.nomColonne -> setter[DFCI_DEB.TYPE] = value
            DfciDebColonne.DFCI_DEB_GEOMETRIE.nomColonne -> {
                if (value != null) {
                    setter[DFCI_DEB.GEOMETRIE] =
                        ST_Transform(ST_GeomFromWkt(value, appSettings.srid), appSettings.srid)
                }
            }
            DfciDebColonne.DFCI_DEB_REMARQUE.nomColonne -> setter[DFCI_DEB.REMARQUE] = value
            DfciDebColonne.DFCI_DEB_PROGRAMME.nomColonne -> setter[DFCI_DEB.PROGRAMME] = value
            DfciDebColonne.DFCI_DEB_TRAVAUX.nomColonne -> setter[DFCI_DEB.TRAVAUX] = value
            DfciDebColonne.DFCI_DEB_DFCI_MASSIF_ID.nomColonne -> setter[DFCI_DEB.DFCI_MASSIF_ID] = value
            DfciDebColonne.DFCI_DEB_DFCI_OUVRAGE_ID.nomColonne -> setter[DFCI_DEB.DFCI_OUVRAGE_ID] = value
            DfciDebColonne.DFCI_DEB_DFCI_PRESTATAIRE_ID.nomColonne -> setter[DFCI_DEB.DFCI_PRESTATAIRE_ID] = value
            DfciDebColonne.DFCI_DEB_CODE.nomColonne -> setter[DFCI_DEB.CODE] = value
            DfciDebColonne.DFCI_DEB_VERSION.nomColonne -> setter[DFCI_DEB.VERSION] = value
        }
        return dsl
            .update(DFCI_DEB)
            .set(setter)
            .where(DFCI_DEB.ID.eq(dfciDebId))
            .execute()
    }

    /**
     * Drop la vue faisant le lien entre les tables des schémas remocra et entrepotsig
     */
    fun dropViewForEntrepotSig() =
        dsl.dropViewIfExists(V_DFCI_DEB_SIG).execute()

    override fun getAllNewElementsFromSig(): List<DfciDebData> =
        dsl
            .select(
                V_DFCI_DEB_SIG.ID.`as`("id"),
                V_DFCI_DEB_SIG.CODE.`as`("code"),
                V_DFCI_DEB_SIG.VERSION.`as`("version"),
                V_DFCI_DEB_SIG.LIBELLE.`as`("dfciDebLibelle"),
                V_DFCI_DEB_SIG.ANNEE_PROGRAMME.`as`("dfciDebAnneeProgramme"),
                V_DFCI_DEB_SIG.ANNEE_TRAVAUX.`as`("dfciDebAnneeTravaux"),
                V_DFCI_DEB_SIG.MOIS_TRAVAUX.`as`("dfciDebMoisTravaux"),
                V_DFCI_DEB_SIG.LARGEUR.`as`("dfciDebLargeur"),
                V_DFCI_DEB_SIG.SURFACE.`as`("dfciDebSurface"),
                V_DFCI_DEB_SIG.GEOMETRIE.`as`("dfciDebGeometrie"),
                V_DFCI_DEB_SIG.TYPE.`as`("dfciDebType"),
                V_DFCI_DEB_SIG.PROGRAMME.`as`("dfciDebProgramme"),
                V_DFCI_DEB_SIG.TRAVAUX.`as`("dfciDebTravaux"),
                V_DFCI_DEB_SIG.REMARQUE.`as`("dfciDebRemarque"),
                V_DFCI_DEB_SIG.DFCI_MASSIF_ID.`as`("dfciDebDfciMassifId"),
                V_DFCI_DEB_SIG.DFCI_OUVRAGE_ID.`as`("dfciDebDfciOuvrageId"),
                V_DFCI_DEB_SIG.DFCI_PRESTATAIRE_ID.`as`("dfciDebDfciPrestataireId"),
            )
            .from(V_DFCI_DEB_SIG)
            .whereNotExists(
                dsl.selectOne()
                    .from(DFCI_DEB)
                    .where(DFCI_DEB.CODE.eq(V_DFCI_DEB_SIG.CODE)),
            )
            .fetchInto()

    // pour le Map ; on est sur que code est non nul
    @Suppress("UNCHECKED_CAST")
    override fun getAllOldElementsFromSig(): Map<String, DfciDebData> =
        dsl
            .select(
                V_DFCI_DEB_SIG.ID.`as`("id"),
                V_DFCI_DEB_SIG.CODE.`as`("code"),
                V_DFCI_DEB_SIG.VERSION.`as`("version"),
                V_DFCI_DEB_SIG.LIBELLE.`as`("dfciDebLibelle"),
                V_DFCI_DEB_SIG.ANNEE_PROGRAMME.`as`("dfciDebAnneeProgramme"),
                V_DFCI_DEB_SIG.ANNEE_TRAVAUX.`as`("dfciDebAnneeTravaux"),
                V_DFCI_DEB_SIG.MOIS_TRAVAUX.`as`("dfciDebMoisTravaux"),
                V_DFCI_DEB_SIG.LARGEUR.`as`("dfciDebLargeur"),
                V_DFCI_DEB_SIG.SURFACE.`as`("dfciDebSurface"),
                V_DFCI_DEB_SIG.GEOMETRIE.`as`("dfciDebGeometrie"),
                V_DFCI_DEB_SIG.TYPE.`as`("dfciDebType"),
                V_DFCI_DEB_SIG.PROGRAMME.`as`("dfciDebProgramme"),
                V_DFCI_DEB_SIG.TRAVAUX.`as`("dfciDebTravaux"),
                V_DFCI_DEB_SIG.REMARQUE.`as`("dfciDebRemarque"),
                V_DFCI_DEB_SIG.DFCI_MASSIF_ID.`as`("dfciDebDfciMassifId"),
                V_DFCI_DEB_SIG.DFCI_OUVRAGE_ID.`as`("dfciDebDfciOuvrageId"),
                V_DFCI_DEB_SIG.DFCI_PRESTATAIRE_ID.`as`("dfciDebDfciPrestataireId"),
            )
            .from(V_DFCI_DEB_SIG)
            .whereExists(
                dsl.selectOne()
                    .from(DFCI_DEB)
                    .where(DFCI_DEB.CODE.eq(V_DFCI_DEB_SIG.CODE)),
            )
            .fetchMap(V_DFCI_DEB_SIG.CODE.`as`("code"), DfciDebData::class.java)
            as Map<String, DfciDebData>

    override fun getAllOldElementsFromRemocra(): List<DfciDebData> = dsl
        .select(
            DFCI_DEB.ID.`as`("id"),
            DFCI_DEB.CODE.`as`("code"),
            DFCI_DEB.VERSION.`as`("version"),
            DFCI_DEB.LIBELLE,
            DFCI_DEB.ANNEE_PROGRAMME,
            DFCI_DEB.ANNEE_TRAVAUX,
            DFCI_DEB.MOIS_TRAVAUX,
            DFCI_DEB.ANNEE_EDITION,
            DFCI_DEB.LARGEUR,
            DFCI_DEB.SURFACE,
            DFCI_DEB.GEOMETRIE,
            DFCI_DEB.TYPE,
            DFCI_DEB.PROGRAMME,
            DFCI_DEB.TRAVAUX,
            DFCI_DEB.REMARQUE,
            DFCI_DEB.DFCI_MASSIF_ID,
            DFCI_DEB.DFCI_OUVRAGE_ID,
            DFCI_DEB.DFCI_PRESTATAIRE_ID,
        )
        .from(DFCI_DEB)
        .whereExists(
            dsl.selectOne()
                .from(V_DFCI_DEB_SIG)
                .where(V_DFCI_DEB_SIG.CODE.eq(DFCI_DEB.CODE)),
        )
        .fetchInto()

    /**
     * Met à jour le débroussaillement via sa classe DfciDebData
     */
    fun updateDfciDebFromData(dfciDebData: DfciDebData) =
        updateDeb(dfciDebData.convertDebDataToPojo())

    /**
     * Insère une nouvelle ligne dans la table via un DfciDebData
     */
    fun insertDfciDebFromData(dfciDebData: DfciDebData): Int =
        dsl.insertInto(DFCI_DEB).set(dsl.newRecord(DFCI_DEB, dfciDebData.convertDebDataToPojo())).execute()

    fun getGeometrieDfciDeb(dfciDebId: UUID): DfciDebGeometrie =
        dsl
            .select(DFCI_DEB.GEOMETRIE)
            .from(DFCI_DEB)
            .where(DFCI_DEB.ID.eq(dfciDebId))
            .fetchSingleInto()

    data class DfciDebGeometrie(
        val geometrie: Geometry,
    )
}
