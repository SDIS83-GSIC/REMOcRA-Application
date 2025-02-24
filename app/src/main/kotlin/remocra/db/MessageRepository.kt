package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.MessageData
import remocra.db.jooq.remocra.tables.references.MESSAGE_EVENEMENT
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.time.ZonedDateTime
import java.util.*

class MessageRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    data class Message(
        val messageObjet: String?,
        val messageDescription: String?,
        val messageDateConstat: ZonedDateTime?,
        val messageImportance: Int?,
        val messageOrigine: String?,
        val messageTags: String?,
        val messageId: UUID,
        val messageEvenementId: UUID,
        val messageUtilisateur: String?,
    )

    fun getAllMessages(): Collection<Message> =
        dsl.select(
            MESSAGE_EVENEMENT.MESSAGE_ID,
            MESSAGE_EVENEMENT.MESSAGE_DATE_CONSTAT,
            MESSAGE_EVENEMENT.MESSAGE_OBJET,
            MESSAGE_EVENEMENT.MESSAGE_DESCRIPTION,
            MESSAGE_EVENEMENT.MESSAGE_TAG.`as`("messageTags"),
            MESSAGE_EVENEMENT.MESSAGE_ORIGINE,
            MESSAGE_EVENEMENT.MESSAGE_IMPORTANCE,
            UTILISATEUR.NOM.`as`("messageUtilisateur"),
            MESSAGE_EVENEMENT.EVENEMENT_ID.`as`("messageEvenementId"),
        )
            .from(MESSAGE_EVENEMENT)
            .join(UTILISATEUR)
            .on(UTILISATEUR.ID.eq(MESSAGE_EVENEMENT.UTILISATEUR_ID))
            .orderBy(MESSAGE_EVENEMENT.MESSAGE_DATE_CONSTAT.desc())
            .fetchInto()

    fun checkNumeroExists(messageNumero: UUID): Boolean =
        dsl.fetchExists(dsl.select(MESSAGE_EVENEMENT.MESSAGE_ID).from(MESSAGE_EVENEMENT).where(MESSAGE_EVENEMENT.MESSAGE_ID.eq(messageNumero)))

    fun add(element: MessageData) {
        dsl.insertInto(
            MESSAGE_EVENEMENT,
            MESSAGE_EVENEMENT.MESSAGE_ID,
            MESSAGE_EVENEMENT.MESSAGE_DATE_CONSTAT,
            MESSAGE_EVENEMENT.MESSAGE_OBJET,
            MESSAGE_EVENEMENT.MESSAGE_TAG,
            MESSAGE_EVENEMENT.MESSAGE_DESCRIPTION,
            MESSAGE_EVENEMENT.MESSAGE_ORIGINE,
            MESSAGE_EVENEMENT.MESSAGE_IMPORTANCE,
            MESSAGE_EVENEMENT.UTILISATEUR_ID,
            MESSAGE_EVENEMENT.EVENEMENT_ID,
        ).values(
            element.messageId,
            element.messageDateConstat,
            element.messageObjet,
            element.messageTags,
            element.messageDescription,
            element.messageOrigine,
            element.messageImportance,
            element.messageUtilisateurId,
            element.messageEvenementId,
        ).execute()
    }
}
