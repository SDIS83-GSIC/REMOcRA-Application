package remocra.data

import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.web.visite.VisiteEndPoint
import java.time.ZonedDateTime
import java.util.UUID

data class VisiteData(
    val visiteId: UUID,
    val visitePeiId: UUID,
    val visiteDate: ZonedDateTime,
    val visiteTypeVisite: TypeVisite,
    val visiteAgent1: String?,
    val visiteAgent2: String?,
    val visiteObservation: String?,
    var listeAnomalie: List<UUID>,
    val isCtrlDebitPression: Boolean,
    var ctrlDebitPression: VisiteEndPoint.CreationVisiteCtrl?,
)
