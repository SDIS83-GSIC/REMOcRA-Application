package remocra.data

import java.util.UUID

data class CriseDocumentData(
    val criseId: UUID,
    val listDocument: DocumentsData.DocumentsEvenement?,
)
