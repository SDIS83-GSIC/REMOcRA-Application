package remocra.data

import java.util.UUID

data class GroupeCoucheData(
    val groupeCoucheId: UUID = UUID.randomUUID(),
    val groupeCoucheCode: String,
    val groupeCoucheLibelle: String,
    val groupeCoucheOrdre: Int,
    val groupeCoucheProtected: Boolean = false,

    val coucheList: Collection<CoucheData> = listOf(),
)

data class StyleGroupeCoucheData(
    val groupeCoucheId: UUID = UUID.randomUUID(),
    val groupeCoucheCode: String,
    val groupeCoucheLibelle: String,
    val coucheList: Collection<SimplifiedCoucheData> = listOf(),
)
