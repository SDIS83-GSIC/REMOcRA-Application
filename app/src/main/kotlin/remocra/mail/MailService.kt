package remocra.mail

import jakarta.inject.Inject
import org.apache.commons.mail2.core.EmailException
import org.apache.commons.mail2.jakarta.EmailAttachment
import org.apache.commons.mail2.jakarta.HtmlEmail
import remocra.log.LogManager
import java.io.File

class MailService @Inject constructor(
    private val settings: MailSettings,
) {

    @Throws(EmailException::class)
    fun send(
        subject: String,
        body: String,
        bcc: Set<String> = emptySet(),
        attachment: File? = null,
        logManager: LogManager?,
    ) {
        val email = createEmail()

        bcc.forEach(email::addBcc)
        email.subject = subject
        email.setHtmlMsg("<html><body><p>$body</p></body></html>")
        email.setTextMsg(body)

        if (attachment != null) {
            if (attachment.exists() && attachment.isFile) {
                val att = EmailAttachment().apply {
                    this.path = attachment.absolutePath
                    disposition = EmailAttachment.ATTACHMENT
                    name = attachment.name
                }
                email.attach(att)
            } else {
                logManager?.error("Envoi du mail annulé : la pièce jointe est attendue mais introuvable : '${attachment.absolutePath}'")
                return
            }
        }

        email.send()
    }

    fun checkConnection(): Boolean {
        createEmail().mailSession.transport.use {
            it.connect()
            return it.isConnected
        }
    }

    private fun createEmail(): HtmlEmail {
        val email = HtmlEmail()
        email.hostName = settings.smtpUrl
        email.setSmtpPort(settings.smtpPort)
        if (settings.smtpUser != null && settings.smtpPassword != null) {
            email.setAuthentication(settings.smtpUser, settings.smtpPassword)
        }
        // TODO prendre en compte le SSL sur demande, tout le temps ? overrider pour la conf maildev
        email.isSSLOnConnect = false
        email.setFrom(settings.from)

        // Définir l'encodage UTF-8
        email.setCharset("UTF-8")

        return email
    }
}
