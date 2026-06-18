package remocra.data

import java.util.UUID

data class DfciOuvrageData(
    override val id: UUID,
    override val code: String,
    override val version: Int,
    val dfciOuvrageLibelle: String,
) : IdCodeVersion
