package remocra.data

import java.util.UUID

data class DfciPrestataireData(
    override val id: UUID,
    override val code: String,
    override val version: Int,
    val dfciPrestataireLibelle: String,
) : IdCodeVersion
