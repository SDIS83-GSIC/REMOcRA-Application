package remocra.data

import java.time.ZonedDateTime
import java.util.UUID

data class MessageData(
    val messageObjet: String?,
    val messageDescription: String?,
    val messageDateConstat: ZonedDateTime?,
    val messageImportance: Int?,
    val messageOrigine: String?,
    val messageTags: String?,
    val messageId: UUID,
    val messageEvenementId: UUID?,
    val messageUtilisateurId: UUID?,
)
