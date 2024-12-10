package remocra.apimobile.data

import remocra.db.jooq.remocra.enums.TypeVisite
import java.util.UUID

class VisiteForApiMobileData(
    val visiteId: UUID,
    val tourneeId: UUID,
    val peiId: UUID,
    val visiteDate: String,
    val visiteTypeVisite: TypeVisite,
    val ctrDebitPression: Boolean,
    val visiteAgent1: String?,
    val visiteAgent2: String?,
    val visiteCtrlDebitPressionDebit: Int,
    val visiteCtrlDebitPressionPression: Double,
    val visiteCtrlDebitPressionPressionDyn: Double,
    val visiteObservations: String?,
    val hasAnomalieChanges: Boolean,
)
