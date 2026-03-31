package remocra.eventbus.notification

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import org.apache.commons.mail2.core.EmailException
import org.owasp.html.PolicyFactory
import org.slf4j.LoggerFactory
import remocra.db.JobRepository
import remocra.eventbus.EventListener
import remocra.log.LogManagerFactory
import remocra.mail.MailService
import remocra.mail.MailSettings
import remocra.web.documentTelechargerRessourceFrom

class NotificationEventListener @Inject constructor(
    private val logManagerFactory: LogManagerFactory,
    private val jobRepository: JobRepository,
    private val settings: MailSettings,
    private val mailService: MailService,
    private val policyFactory: PolicyFactory,
) :
    EventListener<NotificationEvent> {

    @Subscribe
    override fun onEvent(event: NotificationEvent) {
        val idJob = event.idJob

        val logManager = idJob?.let { logManagerFactory.create(idJob) }
        try {
            mailService.send(
                subject = event.notificationData.objet,
                body = policyFactory.sanitize(
                    if (event.notificationData.documentId != null) {
                        event.notificationData.corps.replace(
                            "#[LIEN_TELECHARGEMENT]#",
                            documentTelechargerRessourceFrom(
                                urlSite = settings.urlSite,
                                documentId = event.notificationData.documentId,
                            ),
                        )
                    } else {
                        event.notificationData.corps
                    },
                ),
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
