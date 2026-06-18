package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.app.AppSettings
import remocra.data.DfciPanneauData
import remocra.data.enums.DfciPanneauColonne
import remocra.db.jooq.entrepotsig.tables.references.V_DFCI_PANNEAU_SIG
import remocra.db.jooq.remocra.tables.pojos.DfciPanneau
import remocra.db.jooq.remocra.tables.references.DFCI_PANNEAU
import remocra.utils.ST_GeomFromWkt
import remocra.utils.ST_Transform
import java.util.LinkedHashMap
import java.util.UUID

/**
 * Repository permettant d'effectuer les requêtes sur la table dfci_panneau
 */
class DfciPanneauRepository @Inject constructor(
    private val dsl: DSLContext,
    private val appSettings: AppSettings,
) : AbstractRepository(), SynchroRequete<DfciPanneauData> {

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

    fun updateDfciPanneauColumn(dfciPanneauColonne: String, value: String?, dfciPanneauId: UUID): Int {
        val setter = LinkedHashMap<Any?, Any?>()
        when (dfciPanneauColonne) {
            DfciPanneauColonne.DFCI_PANNEAU_TYPE.nomColonne -> setter[DFCI_PANNEAU.TYPE] = value
            DfciPanneauColonne.DFCI_PANNEAU_ETAT.nomColonne -> setter[DFCI_PANNEAU.ETAT] = value
            DfciPanneauColonne.DFCI_PANNEAU_BZERO.nomColonne -> setter[DFCI_PANNEAU.BZERO] = value
            DfciPanneauColonne.DFCI_PANNEAU_DATE_GPS.nomColonne -> setter[DFCI_PANNEAU.DATE_GPS] = value
            DfciPanneauColonne.DFCI_PANNEAU_POSITION.nomColonne -> setter[DFCI_PANNEAU.POSITION] = value
            DfciPanneauColonne.DFCI_PANNEAU_EQUIPEMENT.nomColonne -> setter[DFCI_PANNEAU.EQUIPEMENT] = value
            DfciPanneauColonne.DFCI_PANNEAU_DFCI_PISTE_ID.nomColonne -> setter[DFCI_PANNEAU.DFCI_PISTE_ID] = value
            DfciPanneauColonne.DFCI_PANNEAU_NUM_PISTE.nomColonne -> setter[DFCI_PANNEAU.NUM_PISTE] = value
            DfciPanneauColonne.DFCI_PANNEAU_LIBELLE_PISTE.nomColonne -> setter[DFCI_PANNEAU.LIBELLE_PISTE] = value
            DfciPanneauColonne.DFCI_PANNEAU_REMARQUE.nomColonne -> setter[DFCI_PANNEAU.REMARQUE] = value
            DfciPanneauColonne.DFCI_PANNEAU_GEOMETRIE.nomColonne -> {
                if (value != null) {
                    setter[DFCI_PANNEAU.GEOMETRIE] =
                        ST_Transform(ST_GeomFromWkt(value, appSettings.srid), appSettings.srid)
                }
            }
            DfciPanneauColonne.DFCI_PANNEAU_CODE.nomColonne -> setter[DFCI_PANNEAU.CODE] = value
            DfciPanneauColonne.DFCI_PANNEAU_VERSION.nomColonne -> setter[DFCI_PANNEAU.VERSION] = value
        }
        return dsl
            .update(DFCI_PANNEAU)
            .set(setter)
            .where(DFCI_PANNEAU.ID.eq(dfciPanneauId))
            .execute()
    }

    /**
     * Drop la vue faisant le lien entre les schémas remocra et entrepotsig de la table dfci_panneau
     */
    fun dropViewForEntrepotSig() =
        dsl.dropViewIfExists(V_DFCI_PANNEAU_SIG).execute()

    override fun getAllNewElementsFromSig(): List<DfciPanneauData> =
        dsl
            .select(
                V_DFCI_PANNEAU_SIG.ID.`as`("id"),
                V_DFCI_PANNEAU_SIG.CODE.`as`("code"),
                V_DFCI_PANNEAU_SIG.VERSION.`as`("version"),
                V_DFCI_PANNEAU_SIG.TYPE.`as`("dfciPanneauType"),
                V_DFCI_PANNEAU_SIG.ETAT.`as`("dfciPanneauEtat"),
                V_DFCI_PANNEAU_SIG.BZERO.`as`("dfciPanneauBzero"),
                V_DFCI_PANNEAU_SIG.DATE_GPS.`as`("dfciPanneauDateGps"),
                V_DFCI_PANNEAU_SIG.POSITION.`as`("dfciPanneauPosition"),
                V_DFCI_PANNEAU_SIG.EQUIPEMENT.`as`("dfciPanneauEquipement"),
                V_DFCI_PANNEAU_SIG.DFCI_PISTE_ID.`as`("dfciPanneauDfciPisteId"),
                V_DFCI_PANNEAU_SIG.NUM_PISTE.`as`("dfciPanneauNumPiste"),
                V_DFCI_PANNEAU_SIG.LIBELLE_PISTE.`as`("dfciPanneauLibellePiste"),
                V_DFCI_PANNEAU_SIG.REMARQUE.`as`("dfciPanneauRemarque"),
                V_DFCI_PANNEAU_SIG.GEOMETRIE.`as`("dfciPanneauGeometrie"),
            )
            .from(V_DFCI_PANNEAU_SIG)
            .whereNotExists(
                dsl.selectOne()
                    .from(DFCI_PANNEAU)
                    .where(DFCI_PANNEAU.CODE.eq(V_DFCI_PANNEAU_SIG.CODE)),
            )
            .fetchInto()

    // pour le Map ; on est sur que code est non nul
    @Suppress("UNCHECKED_CAST")
    override fun getAllOldElementsFromSig(): Map<String, DfciPanneauData> =
        dsl
            .select(
                V_DFCI_PANNEAU_SIG.ID.`as`("id"),
                V_DFCI_PANNEAU_SIG.CODE.`as`("code"),
                V_DFCI_PANNEAU_SIG.VERSION.`as`("version"),
                V_DFCI_PANNEAU_SIG.TYPE.`as`("dfciPanneauType"),
                V_DFCI_PANNEAU_SIG.ETAT.`as`("dfciPanneauEtat"),
                V_DFCI_PANNEAU_SIG.BZERO.`as`("dfciPanneauBzero"),
                V_DFCI_PANNEAU_SIG.DATE_GPS.`as`("dfciPanneauDateGps"),
                V_DFCI_PANNEAU_SIG.POSITION.`as`("dfciPanneauPosition"),
                V_DFCI_PANNEAU_SIG.EQUIPEMENT.`as`("dfciPanneauEquipement"),
                V_DFCI_PANNEAU_SIG.DFCI_PISTE_ID.`as`("dfciPanneauDfciPisteId"),
                V_DFCI_PANNEAU_SIG.NUM_PISTE.`as`("dfciPanneauNumPiste"),
                V_DFCI_PANNEAU_SIG.LIBELLE_PISTE.`as`("dfciPanneauLibellePiste"),
                V_DFCI_PANNEAU_SIG.REMARQUE.`as`("dfciPanneauRemarque"),
                V_DFCI_PANNEAU_SIG.GEOMETRIE.`as`("dfciPanneauGeometrie"),
            )
            .from(V_DFCI_PANNEAU_SIG)
            .whereExists(
                dsl.selectOne()
                    .from(DFCI_PANNEAU)
                    .where(DFCI_PANNEAU.CODE.eq(V_DFCI_PANNEAU_SIG.CODE)),
            )
            .fetchMap(V_DFCI_PANNEAU_SIG.CODE.`as`("code"), DfciPanneauData::class.java)
            as Map<String, DfciPanneauData>

    override fun getAllOldElementsFromRemocra(): List<DfciPanneauData> =
        dsl
            .select(
                DFCI_PANNEAU.ID.`as`("id"),
                DFCI_PANNEAU.CODE.`as`("code"),
                DFCI_PANNEAU.VERSION.`as`("version"),
                DFCI_PANNEAU.TYPE,
                DFCI_PANNEAU.ETAT,
                DFCI_PANNEAU.BZERO,
                DFCI_PANNEAU.DATE_GPS,
                DFCI_PANNEAU.POSITION,
                DFCI_PANNEAU.EQUIPEMENT,
                DFCI_PANNEAU.DFCI_PISTE_ID,
                DFCI_PANNEAU.NUM_PISTE,
                DFCI_PANNEAU.LIBELLE_PISTE,
                DFCI_PANNEAU.REMARQUE,
                DFCI_PANNEAU.GEOMETRIE,
            )
            .from(DFCI_PANNEAU)
            .whereExists(
                dsl.selectOne()
                    .from(V_DFCI_PANNEAU_SIG)
                    .where(V_DFCI_PANNEAU_SIG.CODE.eq(DFCI_PANNEAU.CODE)),
            )
            .fetchInto()

    /**
     * Met à jour le panneau DFCI en utilisant la classe DfciPanneauData
     */
    fun updateDfciPanneauFromData(dfciPanneauData: DfciPanneauData): Int =
        updateDfciPanneau(dfciPanneauData.convertDfciPanneauDataToPojo())

    /**
     * Insère une nouvelle ligne dans la table via un DfciPanneauData
     */
    fun insertDfciPanneauFromData(dfciPanneauData: DfciPanneauData): Int =
        dsl.insertInto(DFCI_PANNEAU).set(dsl.newRecord(DFCI_PANNEAU, dfciPanneauData.convertDfciPanneauDataToPojo())).execute()
}
