package remocra.data

import java.util.UUID

data class GroupeCoucheData(
    val groupeCoucheId: UUID = UUID.randomUUID(),
    val groupeCoucheCode: String,
    val groupeCoucheLibelle: String,
    val groupeCoucheOrdre: Int,
    val coucheList: Collection<CoucheData> = listOf(),
)
