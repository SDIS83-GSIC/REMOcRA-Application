package remocra.app

import com.google.inject.Provider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import remocra.data.ParametresData
import remocra.db.ParametreRepository
import remocra.db.TaskRepository
import remocra.db.jooq.remocra.tables.pojos.Parametre
import remocra.schedule.SchedulableTasksExecutor
import remocra.web.admin.getBooleanOrNull
import remocra.web.admin.getDoubleOrNull
import remocra.web.admin.getIntOrNull
import remocra.web.admin.getParametre
import remocra.web.admin.getStringOrNull

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

    /**
     * Méthode permettant de recharger les paramètres à partir de la BDD.
     * TODO ne devrait pas servir en externe si on passe bien par un ParametreModifiedEventListener
     */
    private fun reloadParametres() {
        parametres = getData()
        schedulableTasksExecutor.start()
    }

    private fun getData(): ParametresData {
        val mapParametres = parametreRepository.getMapParametres()
        val mapTasks = taskRepository.getMapTasks()

        return ParametresData(mapParametres = mapParametres, mapTasksInfo = mapTasks)
    }

    private fun getParametre(key: String): Parametre {
        return get().mapParametres.getParametre(key)
    }

    fun getParametreValue(key: String): Any? {
        return getParametre(key).parametreValeur
    }

    /**
     * Retourne la valeur booléenne d'un paramètre dont la clé est fournie en paramètre.
     *
     * */
    fun getParametreBoolean(key: String): Boolean? {
        return get().mapParametres.getBooleanOrNull(key)
    }

    /**
     * Retourne la valeur Int d'un paramètre dont la clé est fournie en paramètre.
     *
     * */
    fun getParametreInt(key: String): Int? {
        return get().mapParametres.getIntOrNull(key)
    }

    /**
     * Retourne la valeur Double d'un paramètre dont la clé est fournie en paramètre.
     *
     * */
    fun getParametreDouble(key: String): Double? {
        return get().mapParametres.getDoubleOrNull(key)
    }

    /**
     * Retourne la valeur String d'un paramètre dont la clé est fournie en paramètre.
     *
     * */
    fun getParametreString(key: String): String? {
        return get().mapParametres.getStringOrNull(key)
    }
}
