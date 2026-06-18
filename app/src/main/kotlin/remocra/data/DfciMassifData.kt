package remocra.data

import java.util.UUID

data class DfciMassifData(
    override val id: UUID,
    override val code: String,
    override val version: Int,
    val dfciMassifLibelle: String,
) : IdCodeVersion
