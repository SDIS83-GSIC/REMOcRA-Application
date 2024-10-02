package remocra.data

import java.util.UUID

data class AireAspirationUpsertData(
    val penaAspirationId: UUID?, // Nullable si création
    val numero: String,
    val estNormalise: Boolean,
    val typePenaAspirationId: UUID?,
    val hauteurSuperieure3Metres: Boolean,
    val estDeporte: Boolean,
    val coordonneeX: String?,
    val coordonneeY: String?,
)
