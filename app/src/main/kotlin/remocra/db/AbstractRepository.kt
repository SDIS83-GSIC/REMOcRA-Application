package remocra.db

import com.google.inject.Inject
import remocra.utils.DateUtils

abstract class AbstractRepository {
    @Inject
    lateinit var repositoryUtils: RepositoriesUtils

    @Inject
    lateinit var dateUtils: DateUtils
}
