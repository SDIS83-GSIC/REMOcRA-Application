package remocra.data

import remocra.db.jooq.remocra.enums.TypeVisite
import java.math.BigDecimal
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
    var ctrlDebitPression: CreationVisiteCtrl?,
)

/** Reprend les attributs du Pojo VisiteCtrlDebitPression
 * en faisant abstraction de visiteId, non défini lors de la création d'un visite
 */
data class CreationVisiteCtrl(
    val ctrlDebit: Int?,
    val ctrlPression: BigDecimal?,
    val ctrlPressionDyn: BigDecimal?,
)
