package remocra.data

import org.locationtech.jts.geom.Geometry
import java.util.UUID

data class AireAspirationUpsertData(
    val penaAspirationId: UUID?, // Nullable si création
    val numero: String,
    val estNormalise: Boolean,
    val typePenaAspirationId: UUID?,
    val hauteurSuperieure3Metres: Boolean,
    val estDeporte: Boolean,
    val geometrie: Geometry?,
)
