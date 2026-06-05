package remocra.usecase.sig

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.log.LogManagerFactory
import remocra.tasks.SynchronisationSIGTask
import remocra.usecase.AbstractUseCase

class LaunchSigTaskUseCase @Inject constructor(
    private val task: SynchronisationSIGTask,
    private val logManagerFactory: LogManagerFactory,
) : AbstractUseCase() {
    fun execute(userInfo: WrappedUserInfo) {
        task.start(
            logManagerFactory.create(),
            userInfo,
        )
    }
}
