package remocra.app

import com.google.inject.Provider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import remocra.data.ParametresData
import remocra.db.ParametreRepository
import remocra.db.TaskRepository
import remocra.schedule.SchedulableTasksExecutor

@Singleton
class ParametresProvider
@Inject
constructor(
    private val parametreRepository: ParametreRepository,
    private val taskRepository: TaskRepository,
    private val schedulableTasksExecutor: SchedulableTasksExecutor,
) : Provider<ParametresData> {
    private lateinit var parametres: ParametresData
    override fun get(): ParametresData {
        if (!this::parametres.isInitialized) {
            parametres = getData()
        }
        return parametres
    }

    fun reloadParametres() {
        parametres = getData()
        schedulableTasksExecutor.start()
    }

    private fun getData(): ParametresData {
        val mapParametres = parametreRepository.getMapParametres()
        val mapTasks = taskRepository.getMapTasks()

        return ParametresData(mapParametres = mapParametres, mapTasksInfo = mapTasks)
    }
}
