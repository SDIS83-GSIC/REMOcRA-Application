package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.data.DfciAireData
import remocra.data.enums.DfciAireColonne
import remocra.db.jooq.entrepotsig.tables.references.V_DFCI_AIRE_SIG
import remocra.db.jooq.remocra.tables.pojos.DfciAire
import remocra.db.jooq.remocra.tables.references.DFCI_AIRE
import remocra.utils.ST_GeomFromWkt
import remocra.utils.ST_Transform
import java.util.LinkedHashMap
import java.util.UUID

/**
 * Classe repository permettant de récupérer des données dans la BD en lien avec les aires du module DFCI
 */
class DfciAiresRepository @Inject constructor(
    private val dsl: DSLContext,
    private val appSettings: AppSettings,
) : AbstractRepository(), SynchroRequete<DfciAireData> {

    /**
     * Récupère l'aire dans la bd correspondant à l'id donné
     */
    fun getDfciAireByID(dfciAireId: UUID): DfciAire =
        dsl
            .selectFrom(DFCI_AIRE)
            .where(DFCI_AIRE.ID.eq(dfciAireId))
            .fetchSingleInto()

    fun updateDfciAire(data: DfciAire) =
        dsl
            .update(DFCI_AIRE)
            .set(DFCI_AIRE.AMENAGEMENT, data.dfciAireAmenagement)
            .set(DFCI_AIRE.DATE_GPS, data.dfciAireDateGps)
            .set(DFCI_AIRE.GRANDE_DIMENSION, data.dfciAireGrandeDimension)
            .set(DFCI_AIRE.PETITE_DIMENSION, data.dfciAirePetiteDimension)
            .set(DFCI_AIRE.TYPE, data.dfciAireType)
            .set(DFCI_AIRE.DFCI_PISTE_ID, data.dfciAireDfciPisteId)
            .set(DFCI_AIRE.REMARQUE, data.dfciAireRemarque)
            .set(DFCI_AIRE.VERSION, data.dfciAireVersion)
            .where(DFCI_AIRE.ID.eq(data.dfciAireId))
            .execute()

    fun updateDfciAireColumn(dfciAireColonne: String, value: String?, dfciAireId: UUID): Int {
        val setter = LinkedHashMap<Any?, Any?>()
        when (dfciAireColonne) {
            DfciAireColonne.DFCI_AIRE_AMENAGEMENT.nomColonne -> setter[DFCI_AIRE.AMENAGEMENT] = value
            DfciAireColonne.DFCI_AIRE_DATE_GPS.nomColonne -> setter[DFCI_AIRE.DATE_GPS] = value
            DfciAireColonne.DFCI_AIRE_GRANDE_DIMENSION.nomColonne -> setter[DFCI_AIRE.GRANDE_DIMENSION] = value
            DfciAireColonne.DFCI_AIRE_PETITE_DIMENSION.nomColonne -> setter[DFCI_AIRE.PETITE_DIMENSION] = value
            DfciAireColonne.DFCI_AIRE_TYPE.nomColonne -> setter[DFCI_AIRE.TYPE] = value
            DfciAireColonne.DFCI_AIRE_GEOMETRIE.nomColonne -> {
                if (value != null) {
                    setter[DFCI_AIRE.GEOMETRIE] =
                        ST_Transform(ST_GeomFromWkt(value, appSettings.srid), appSettings.srid)
                }
            }
            DfciAireColonne.DFCI_AIRE_DFCI_PISTE_ID.nomColonne -> setter[DFCI_AIRE.DFCI_PISTE_ID] = value
            DfciAireColonne.DFCI_AIRE_REMARQUE.nomColonne -> setter[DFCI_AIRE.REMARQUE] = value
            DfciAireColonne.DFCI_AIRE_CODE.nomColonne -> setter[DFCI_AIRE.CODE] = value
            DfciAireColonne.DFCI_AIRE_VERSION.nomColonne -> setter[DFCI_AIRE.VERSION] = value
        }
        return dsl
            .update(DFCI_AIRE)
            .set(setter)
            .where(DFCI_AIRE.ID.eq(dfciAireId))
            .execute()
    }

    /**
     * Drop la vue faisant le lien entre les schémas remocra et entrepotsig de la table dfci_aire
     */
    fun dropViewForEntrepotSig() =
        dsl.dropViewIfExists(V_DFCI_AIRE_SIG).execute()

    override fun getAllNewElementsFromSig(): List<DfciAireData> =
        dsl
            .select(
                V_DFCI_AIRE_SIG.ID.`as`("id"),
                V_DFCI_AIRE_SIG.CODE.`as`("code"),
                V_DFCI_AIRE_SIG.VERSION.`as`("version"),
                V_DFCI_AIRE_SIG.AMENAGEMENT.`as`("dfciAireAmenagement"),
                V_DFCI_AIRE_SIG.DATE_GPS.`as`("dfciAireDateGps"),
                V_DFCI_AIRE_SIG.GRANDE_DIMENSION.`as`("dfciAireGrandeDimension"),
                V_DFCI_AIRE_SIG.PETITE_DIMENSION.`as`("dfciAirePetiteDimension"),
                V_DFCI_AIRE_SIG.TYPE.`as`("dfciAireType"),
                V_DFCI_AIRE_SIG.GEOMETRIE.`as`("dfciAireGeometrie"),
                V_DFCI_AIRE_SIG.DFCI_PISTE_ID.`as`("dfciAireDfciPisteId"),
                V_DFCI_AIRE_SIG.REMARQUE.`as`("dfciAireRemarque"),
            )
            .from(V_DFCI_AIRE_SIG)
            .whereNotExists(
                dsl.selectOne()
                    .from(DFCI_AIRE)
                    .where(DFCI_AIRE.CODE.eq(V_DFCI_AIRE_SIG.CODE)),
            )
            .fetchInto()

    // pour le Map ; on est sur que code est non nul
    @Suppress("UNCHECKED_CAST")
    override fun getAllOldElementsFromSig(): Map<String, DfciAireData> =
        dsl
            .select(
                V_DFCI_AIRE_SIG.ID.`as`("id"),
                V_DFCI_AIRE_SIG.CODE.`as`("code"),
                V_DFCI_AIRE_SIG.VERSION.`as`("version"),
                V_DFCI_AIRE_SIG.AMENAGEMENT.`as`("dfciAireAmenagement"),
                V_DFCI_AIRE_SIG.DATE_GPS.`as`("dfciAireDateGps"),
                V_DFCI_AIRE_SIG.GRANDE_DIMENSION.`as`("dfciAireGrandeDimension"),
                V_DFCI_AIRE_SIG.PETITE_DIMENSION.`as`("dfciAirePetiteDimension"),
                V_DFCI_AIRE_SIG.TYPE.`as`("dfciAireType"),
                V_DFCI_AIRE_SIG.GEOMETRIE.`as`("dfciAireGeometrie"),
                V_DFCI_AIRE_SIG.DFCI_PISTE_ID.`as`("dfciAireDfciPisteId"),
                V_DFCI_AIRE_SIG.REMARQUE.`as`("dfciAireRemarque"),
            )
            .from(V_DFCI_AIRE_SIG)
            .whereExists(
                dsl.selectOne()
                    .from(DFCI_AIRE)
                    .where(DFCI_AIRE.CODE.eq(V_DFCI_AIRE_SIG.CODE)),
            )
            .fetchMap(V_DFCI_AIRE_SIG.CODE.`as`("code"), DfciAireData::class.java)
            as Map<String, DfciAireData>

    override fun getAllOldElementsFromRemocra(): List<DfciAireData> =
        dsl
            .select(
                DFCI_AIRE.ID.`as`("id"),
                DFCI_AIRE.CODE.`as`("code"),
                DFCI_AIRE.VERSION.`as`("version"),
                DFCI_AIRE.AMENAGEMENT.`as`("dfciAireAmenagement"),
                DFCI_AIRE.DATE_GPS.`as`("dfciAireDateGps"),
                DFCI_AIRE.GRANDE_DIMENSION.`as`("dfciAireGrandeDimension"),
                DFCI_AIRE.PETITE_DIMENSION.`as`("dfciAirePetiteDimension"),
                DFCI_AIRE.TYPE.`as`("dfciAireType"),
                DFCI_AIRE.GEOMETRIE.`as`("dfciAireGeometrie"),
                DFCI_AIRE.DFCI_PISTE_ID.`as`("dfciAireDfciPisteId"),
                DFCI_AIRE.REMARQUE.`as`("dfciAireRemarque"),
            )
            .from(DFCI_AIRE)
            .whereExists(
                dsl.selectOne()
                    .from(V_DFCI_AIRE_SIG)
                    .where(V_DFCI_AIRE_SIG.CODE.eq(DFCI_AIRE.CODE)),
            )
            .fetchInto()

    /**
     * Met à jour l'aire via la classe DfciAireData
     */
    fun updateDfciAireFromData(dfciAireData: DfciAireData): Int =
        updateDfciAire(dfciAireData.convertAireDataToPojo())

    /**
     * Insère une nouvelle aire dans la table via la classe DfciAireData
     */
    fun insertDfciAireFromData(dfciAireData: DfciAireData): Int =
        dsl.insertInto(DFCI_AIRE).set(dsl.newRecord(DFCI_AIRE, dfciAireData.convertAireDataToPojo())).execute()

    fun getGeometrieDfciAire(dfciAireId: UUID): DfciAireGeometrie =
        dsl
            .select(DFCI_AIRE.GEOMETRIE)
            .from(DFCI_AIRE)
            .where(DFCI_AIRE.ID.eq(dfciAireId))
            .fetchSingleInto()

    data class DfciAireGeometrie(
        val geometrie: Geometry,
    )
}
