package remocra.usecase.admin.relancercalcul

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.log.LogManagerFactory
import remocra.tasks.RelanceNumerotationParameters
import remocra.tasks.RelanceNumerotationTask
import remocra.usecase.AbstractUseCase

class RelancerCalculNumerotationUseCase @Inject constructor(
    private val task: RelanceNumerotationTask,
    private val logManagerFactory: LogManagerFactory,
) : AbstractUseCase() {

    fun execute(userInfo: WrappedUserInfo, eventTracabilite: Boolean, eventNexSis: Boolean) {
        task.start(
            logManager = logManagerFactory.create(),
            userInfo,
            RelanceNumerotationParameters().apply {
                this.eventTracabilite = eventTracabilite
                this.eventNexSis = eventNexSis
            },
        )
    }
}
