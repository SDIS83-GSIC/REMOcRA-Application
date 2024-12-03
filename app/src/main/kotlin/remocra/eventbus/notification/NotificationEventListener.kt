package remocra.eventbus.notification

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import org.apache.commons.mail.EmailException
import org.apache.commons.mail.HtmlEmail
import org.slf4j.LoggerFactory
import remocra.auth.AuthnConstants
import remocra.db.JobRepository
import remocra.eventbus.EventListener
import remocra.eventbus.MailSettings
import remocra.log.LogManagerFactory
import remocra.web.documents.DocumentEndPoint
import kotlin.reflect.jvm.javaMethod

class NotificationEventListener @Inject constructor() :
    EventListener<NotificationEvent> {

    @Inject
    private lateinit var logManagerFactory: LogManagerFactory

    @Inject
    private lateinit var jobRepository: JobRepository

    @Inject
    private lateinit var settings: MailSettings

    @Subscribe
    override fun onEvent(event: NotificationEvent) {
        val idJob = event.idJob

        val logManager = idJob?.let { logManagerFactory.create(idJob) }
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

            var stringURI: String? = null
            if (event.notificationData.documentId != null) {
                stringURI = UriBuilder
                    .fromUri(settings.urlSite)
                    .path(AuthnConstants.API_PATH)
                    .path(DocumentEndPoint::class.java)
                    .path(DocumentEndPoint::telechargerRessource.javaMethod)
                    .build(event.notificationData.documentId).toString()
            }
            email.setMsg(
                stringURI?.let { event.notificationData.corps.replace("#[LIEN_TELECHARGEMENT]#", it) }
                    ?: event.notificationData.corps,
            )

            email.send()

            if (idJob != null) {
                // Mise à jour du job après notification
                logManager!!.info("${event.notificationData}")
                jobRepository.setNotifie(event.idJob)
            }
        } catch (ee: EmailException) {
            if (idJob != null) {
                logManager!!.error("Problème d'envoi du mail : ${ee.message}")
            } else {
                LoggerFactory.getLogger(NotificationEventListener::class.java).error("Problème d'envoi du mail : ${ee.message}")
            }
        }
    }
}
