package remocra.data.groupecouche

import java.util.UUID

data class GroupeCoucheTableData(
    val groupeCoucheId: UUID,
    val groupeCoucheCode: String,
    val groupeCoucheOrdre: Int,
    val groupeCoucheLibelle: String,
    val groupeCoucheProtected: Boolean,
    val nombreCouche: Int,
)
