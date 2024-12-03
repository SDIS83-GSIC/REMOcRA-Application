package remocra.data

import java.util.UUID

/**
 * Classe permettant de stocker les éléments de base pour déclencher une notification par mail.
 * Ces éléments sont <b>concrets</b>, il convient de les "calculer" par rapport au précâblage défini dans la tâche.
 * L'élément documentId correspond à un UUID permettant la création d'un lien de téléchargement via DocumentEndPoint.telechargerRessource
 */
data class NotificationMailData(
    val destinataires: Set<String>,
    val objet: String,
    val corps: String,
    val documentId: UUID? = null,
) {
    override fun toString(): String {
        return "Destinataires: ${destinataires.joinToString()}, Objet : $objet, Corps : $corps" + documentId?.let { ", DocumentId: $documentId" }
    }
}
