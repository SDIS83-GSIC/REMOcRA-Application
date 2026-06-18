package remocra.data

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.TypeBzero
import remocra.db.jooq.remocra.enums.TypeEquipement
import remocra.db.jooq.remocra.enums.TypePanneau
import remocra.db.jooq.remocra.enums.TypePosition
import remocra.db.jooq.remocra.tables.pojos.DfciPanneau
import java.time.ZonedDateTime
import java.util.UUID

data class DfciPanneauData(
    override val id: UUID,
    override val code: String,
    override val version: Int,
    val dfciPanneauType: TypePanneau,
    val dfciPanneauEtat: Boolean,
    val dfciPanneauBzero: TypeBzero,
    val dfciPanneauDateGps: ZonedDateTime,
    val dfciPanneauPosition: TypePosition,
    val dfciPanneauEquipement: TypeEquipement,
    val dfciPanneauDfciPisteId: UUID,
    val dfciPanneauNumPiste: Boolean,
    val dfciPanneauLibellePiste: Boolean,
    val dfciPanneauRemarque: String?,
    val dfciPanneauGeometrie: Geometry,
) : IdCodeVersion {

    /**
     * Convertie la classe DfciPanneauData pour récupérer le pojo
     */
    fun convertDfciPanneauDataToPojo(): DfciPanneau =
        DfciPanneau(
            dfciPanneauId = id,
            dfciPanneauType = dfciPanneauType,
            dfciPanneauEtat = dfciPanneauEtat,
            dfciPanneauBzero = dfciPanneauBzero,
            dfciPanneauDateGps = dfciPanneauDateGps,
            dfciPanneauPosition = dfciPanneauPosition,
            dfciPanneauEquipement = dfciPanneauEquipement,
            dfciPanneauDfciPisteId = dfciPanneauDfciPisteId,
            dfciPanneauNumPiste = dfciPanneauNumPiste,
            dfciPanneauLibellePiste = dfciPanneauLibellePiste,
            dfciPanneauRemarque = dfciPanneauRemarque,
            dfciPanneauGeometrie = dfciPanneauGeometrie,
            dfciPanneauCode = code,
            dfciPanneauVersion = version,
        )
}
