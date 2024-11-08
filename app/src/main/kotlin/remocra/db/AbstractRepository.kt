package remocra.db

import com.google.inject.Inject

abstract class AbstractRepository {
    @Inject
    lateinit var repositoryUtils: RepositoriesUtils
}
