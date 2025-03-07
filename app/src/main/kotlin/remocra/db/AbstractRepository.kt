package remocra.db

import com.google.inject.Inject
import remocra.app.AppSettings
import remocra.utils.DateUtils

abstract class AbstractRepository {
    @Inject
    lateinit var repositoryUtils: RepositoriesUtils

    @Inject
    lateinit var dateUtils: DateUtils

    @Inject
    lateinit var appSettings: AppSettings

    protected val SRID: Int
        get() = appSettings.srid
}
