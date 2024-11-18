package remocra.data

import java.time.ZonedDateTime
import java.util.UUID

data class DocumentCourrierData(
    val id: UUID,
    val libelle: String?,
    val date: ZonedDateTime?,
)
