package remocra.eventbus.notification

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import org.apache.commons.mail2.core.EmailException
import org.slf4j.LoggerFactory
import remocra.auth.AuthnConstants
import remocra.db.JobRepository
import remocra.eventbus.EventListener
import remocra.log.LogManagerFactory
import remocra.mail.MailService
import remocra.mail.MailSettings
import remocra.web.documents.DocumentEndPoint
import kotlin.reflect.jvm.javaMethod

class NotificationEventListener @Inject constructor(
    private val logManagerFactory: LogManagerFactory,
    private val jobRepository: JobRepository,
    private val settings: MailSettings,
    private val mailService: MailService,
) :
    EventListener<NotificationEvent> {

    @Subscribe
    override fun onEvent(event: NotificationEvent) {
        val idJob = event.idJob

        val logManager = idJob?.let { logManagerFactory.create(idJob) }
        try {
            mailService.send(
                subject = event.notificationData.objet,
                body = if (event.notificationData.documentId != null) {
                    event.notificationData.corps.replace(
                        "#[LIEN_TELECHARGEMENT]#",
                        UriBuilder
                            .fromUri(settings.urlSite)
                            .path(AuthnConstants.API_PATH)
                            .path(DocumentEndPoint::class.java)
                            .path(DocumentEndPoint::telechargerRessource.javaMethod)
                            .build(event.notificationData.documentId)
                            .toString(),
                    )
                } else {
                    event.notificationData.corps
                },
                bcc = event.notificationData.destinataires,
            )

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
