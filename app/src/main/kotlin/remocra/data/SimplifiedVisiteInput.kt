package remocra.data

import java.util.UUID

data class SimplifiedVisiteInput(
    val visitePeiId: UUID,
    val visiteObservation: String? = null,
    val listeAnomalie: List<UUID> = listOf(),
    val ctrlDebitPression: CreationVisiteCtrl? = null,
)
