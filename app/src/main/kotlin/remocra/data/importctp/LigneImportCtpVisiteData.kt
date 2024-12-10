package remocra.data.importctp

import java.time.ZonedDateTime
import java.util.UUID

/** Permet de stocker les infos des visites relatives à une ligne d'import CTP  */
data class LigneImportCtpVisiteData(
    val importListeAnomalies: Set<UUID>,
    val importDate: ZonedDateTime,
    val importPeiId: UUID,
    val importAgent1: String,
    val importDebit: Int?,
    val importPression: Double?,
    val importObservation: String?,

    var importLatitude: Double?,
    var importLongitude: Double?,

)
