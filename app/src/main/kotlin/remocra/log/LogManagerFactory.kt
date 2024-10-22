package remocra.log

import com.google.inject.Inject
import com.google.inject.Provider
import remocra.db.LogLineRepository
import remocra.utils.DateUtils
import java.util.UUID

interface LogManagerFactory {
    /**
     * Crée un LogManager avec un idJob aléatoire ; cet ID sera unique sur la durée de vie de
     * l'objet, et permettra d'aggréger les lignes de log d'un même "traitement". Il convient donc
     * d'appeler la méthode [create] dans la méthode de plus haut niveau.
     */
    fun create(): LogManager

    /**
     * Crée un LogManager avec un idJob bien défini, avec pour unique but d'en modifier les propriétés.<br />
     * Ne devrait servir que pour la modification a posteriori d'un job (notification)
     */
    fun create(idJob: UUID): LogManager
}

class LogManagerFactoryImpl
@Inject
constructor(
    private val logLineRepositoryProvider: Provider<LogLineRepository>,
    private val dateUtils: DateUtils,
) : LogManagerFactory {
    override fun create(): LogManager {
        return create(UUID.randomUUID())
    }

    override fun create(idJob: UUID): LogManager {
        return LogManager(
            logLineRepository = logLineRepositoryProvider.get(),
            idJob = idJob,
            dateUtils = dateUtils,
        )
    }
}
