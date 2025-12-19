package remocra.data.couche

import java.util.UUID

data class GroupeCoucheData(
    val groupeCoucheId: UUID = UUID.randomUUID(),
    val groupeCoucheCode: String,
    val groupeCoucheLibelle: String,
    val groupeCoucheProtected: Boolean,
)
