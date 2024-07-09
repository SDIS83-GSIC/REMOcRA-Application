package remocra.app

import com.google.inject.Provider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import remocra.data.ParametresData
import remocra.db.ParametreRepository
import remocra.db.TaskRepository
import remocra.db.jooq.remocra.enums.TypeParametre
import remocra.db.jooq.remocra.tables.pojos.Parametre
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
        val value = get().mapParametres[key] ?: throw IllegalArgumentException("La clé $key n'existe pas dans les paramètres")

        return value
    }

    fun getParametreValue(key: String): Any? {
        return getParametre(key).parametreValeur
    }

    /**
     * Retourne la valeur booléenne d'un paramètre dont la clé est fournie en paramètre.
     *
     * */
    fun getParametreBoolean(key: String): Boolean? {
        val param = getParametre(key)
        if (TypeParametre.BOOLEAN != param.parametreType) {
            throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
        }

        return param.parametreValeur?.toBooleanStrictOrNull()
    }

    /**
     * Retourne la valeur Int d'un paramètre dont la clé est fournie en paramètre.
     *
     * */
    fun getParametreInt(key: String): Int? {
        val param = getParametre(key)
        if (TypeParametre.INTEGER != param.parametreType) {
            throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
        }

        return param.parametreValeur?.toIntOrNull()
    }

    /**
     * Retourne la valeur Double d'un paramètre dont la clé est fournie en paramètre.
     *
     * */
    fun getParametreDouble(key: String): Double? {
        val param = getParametre(key)
        if (TypeParametre.DOUBLE != param.parametreType) {
            throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
        }

        return param.parametreValeur?.toDoubleOrNull()
    }

    /**
     * Retourne la valeur String d'un paramètre dont la clé est fournie en paramètre.
     *
     * */
    fun getParametreString(key: String): String? {
        val param = getParametre(key)
        if (TypeParametre.STRING != param.parametreType) {
            throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
        }

        return param.parametreValeur
    }
}
