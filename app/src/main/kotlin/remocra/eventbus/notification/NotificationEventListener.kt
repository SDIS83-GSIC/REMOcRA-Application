package remocra.eventbus.notification

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import remocra.db.JobRepository
import remocra.eventbus.EventListener
import remocra.log.LogManagerFactory

class NotificationEventListener @Inject constructor() :
    EventListener<NotificationEvent> {

    @Inject
    protected lateinit var logManagerFactory: LogManagerFactory

    @Inject
    protected lateinit var jobRepository: JobRepository

    @Subscribe
    override fun onEvent(event: NotificationEvent) {
        val logManager = logManagerFactory.create(event.idJob)
        // TODO plutôt qu'une boucle, rajouter autant de BCC ? à voir
        event.notificationData.destinataires.forEach {
            //            val email = HtmlEmail()
            // TODO créer le mail, et l'envoyer !
            //            email.setFrom("")
            //            email.addTo(it)
            //            email.setSubject(event.notificationData.objet)
            //            email.setMsg(event.notificationData.corps)

            //            email.send()
        }
        logManager.info("${event.notificationData}")
        jobRepository.setNotifie(event.idJob)
    }
}
