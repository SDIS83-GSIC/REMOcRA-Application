package remocra.data

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.TypeAire
import remocra.db.jooq.remocra.tables.pojos.DfciAire
import java.time.ZonedDateTime
import java.util.UUID

data class DfciAireData(
    override val id: UUID,
    override val code: String,
    override val version: Int,
    val dfciAireAmenagement: Boolean,
    val dfciAireDateGps: ZonedDateTime,
    val dfciAireGrandeDimension: Double?,
    val dfciAirePetiteDimension: Double?,
    val dfciAireType: TypeAire,
    val dfciAireDfciPisteId: UUID?,
    val dfciAireGeometrie: Geometry,
    val dfciAireRemarque: String?,
) : IdCodeVersion {

    /**
     * Convertie la classe DfciAireData pour récupérer le pojo
     */
    fun convertAireDataToPojo(): DfciAire =
        DfciAire(
            dfciAireId = id,
            dfciAireAmenagement = dfciAireAmenagement,
            dfciAireDateGps = dfciAireDateGps,
            dfciAireGrandeDimension = dfciAireGrandeDimension,
            dfciAirePetiteDimension = dfciAirePetiteDimension,
            dfciAireType = dfciAireType,
            dfciAireDfciPisteId = dfciAireDfciPisteId,
            dfciAireGeometrie = dfciAireGeometrie,
            dfciAireRemarque = dfciAireRemarque,
            dfciAireCode = code,
            dfciAireVersion = version,
        )
}
