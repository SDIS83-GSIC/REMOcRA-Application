package remocra.usecase.admin.relancercalcul

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.db.TransactionManager
import remocra.log.LogManagerFactory
import remocra.tasks.RelanceCalculDispoParameters
import remocra.tasks.RelanceCalculDispoTask
import remocra.usecase.AbstractUseCase

class RelancerCalculDispoUseCase @Inject constructor(
    private val task: RelanceCalculDispoTask,
    private val logManagerFactory: LogManagerFactory,
) : AbstractUseCase() {

    fun execute(userInfo: WrappedUserInfo, eventTracabilite: Boolean, eventNexSis: Boolean, transactionManager: TransactionManager? = null) {
        task.start(
            logManager = logManagerFactory.create(),
            userInfo,
            RelanceCalculDispoParameters().apply {
                this.eventTracabilite = eventTracabilite
                this.eventNexSis = eventNexSis
            },
            transactionManager,
        )
    }
}
