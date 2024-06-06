package remocra.eventbus.notification

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import org.apache.commons.mail.EmailException
import org.apache.commons.mail.HtmlEmail
import remocra.db.JobRepository
import remocra.eventbus.EventListener
import remocra.eventbus.MailSettings
import remocra.log.LogManagerFactory

class NotificationEventListener @Inject constructor() :
    EventListener<NotificationEvent> {

    @Inject
    protected lateinit var logManagerFactory: LogManagerFactory

    @Inject
    protected lateinit var jobRepository: JobRepository

    @Inject
    protected lateinit var settings: MailSettings

    @Subscribe
    override fun onEvent(event: NotificationEvent) {
        val logManager = logManagerFactory.create(event.idJob)
        try {
            val email = HtmlEmail()
            email.hostName = settings.smtpUrl
            email.setSmtpPort(settings.smtpPort)
            if (settings.smtpUser != null && settings.smtpPassword != null) {
                email.setAuthentication(settings.smtpUser, settings.smtpPassword)
            }
            // TODO prendre en compte le SSL sur demande, tout le temps ? overrider pour la conf maildev
            email.isSSLOnConnect = false
            email.setFrom(settings.from)

            event.notificationData.destinataires.forEach { email.addBcc(it) }

            email.setSubject(event.notificationData.objet)
            email.setMsg(event.notificationData.corps)

            email.send()

            // Mise à jour du job après notification
            logManager.info("${event.notificationData}")
            jobRepository.setNotifie(event.idJob)
        } catch (ee: EmailException) {
            logManager.error("Problème d'envoi du mail : ${ee.message}")
        }
    }
}
