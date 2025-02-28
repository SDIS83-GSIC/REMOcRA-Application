package remocra.data.courrier.form

import remocra.data.DestinataireData
import java.util.UUID

data class CourrierData(
    val courrierId: UUID = UUID.randomUUID(),
    val documentId: UUID = UUID.randomUUID(),
    val courrierReference: String,
    val modeleCourrierId: UUID,
    val nomDocumentTmp: String,
    val listeDestinataire: Collection<DestinataireData>,
)
