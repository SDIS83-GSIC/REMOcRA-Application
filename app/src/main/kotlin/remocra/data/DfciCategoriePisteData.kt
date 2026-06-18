package remocra.data

import java.util.UUID

data class DfciCategoriePisteData(
    override val id: UUID,
    override val code: String,
    override val version: Int,
    val dfciCategoriePisteLibelle: String,
) : IdCodeVersion
