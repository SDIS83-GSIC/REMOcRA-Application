package remocra.apiapachehop.usecase

import jakarta.inject.Inject
import remocra.data.NotificationMailData
import remocra.db.LogLineRepository
import remocra.db.jooq.remocra.enums.LogLineGravity
import remocra.db.jooq.remocra.tables.pojos.LogLine
import remocra.eventbus.EventBus
import remocra.eventbus.notification.NotificationEvent
import remocra.usecase.AbstractUseCase
import java.util.UUID

class NotifieUseCase @Inject constructor(
    private val eventBus: EventBus,
    private val logLineRepository: LogLineRepository,
) : AbstractUseCase() {

    fun execute(notificationMailData: NotificationMailData, idJob: UUID) {
        logLineRepository.writeLogLine(
            LogLine(
                logLineId = UUID.randomUUID(),
                logLineJobId = idJob,
                logLineGravity = LogLineGravity.INFO,
                logLineDate = dateUtils.now(),
                logLineObjectId = null,
                logLineMessage = "Envoi d'une notification mail pour le job $idJob avec les données : $notificationMailData",
            ),
        )
        eventBus.post(
            NotificationEvent(
                notificationData = notificationMailData,
                idJob = idJob,
            ),
        )
    }
}
