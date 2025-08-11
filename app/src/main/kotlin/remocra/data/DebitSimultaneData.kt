package remocra.data

import jakarta.servlet.http.Part
import java.time.ZonedDateTime
import java.util.UUID

data class DebitSimultaneData(
    val debitSimultaneId: UUID,
    val debitSimultaneNumeroDossier: String,
    val listeDebitSimultaneMesure: List<DebitSimultaneMesureData>,
    val listeDocument: Collection<Part>? = null,
)

data class DebitSimultaneMesureData(
    val debitSimultaneMesureId: UUID?,
    val debitSimultaneMesureDebitRequis: Int?,
    val debitSimultaneMesureDebitMesure: Int?,
    val debitSimultaneMesureDebitRetenu: Int?,
    val debitSimultaneMesureDateMesure: ZonedDateTime,
    val debitSimultaneMesureCommentaire: String?,
    val debitSimultaneMesureIdentiqueReseauVille: Boolean = false,
    val listePeiId: Collection<UUID>,
    val documentNomFichier: String?,
    val documentId: UUID?,
)
