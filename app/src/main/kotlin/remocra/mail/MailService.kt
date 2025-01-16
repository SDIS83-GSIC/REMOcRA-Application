package remocra.mail

import jakarta.inject.Inject
import org.apache.commons.mail.EmailException
import org.apache.commons.mail.HtmlEmail
import kotlin.jvm.Throws

class MailService @Inject constructor(private val settings: MailSettings) {
    @Throws(EmailException::class)
    fun send(
        subject: String,
        body: String,
        bcc: Set<String> = emptySet(),
    ) {
        val email = createEmail()

        bcc.forEach(email::addBcc)
        email.subject = subject
        email.setMsg(body)

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
        return email
    }
}
