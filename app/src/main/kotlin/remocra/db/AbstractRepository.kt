package remocra.db

import com.google.inject.Inject
import remocra.app.AppSettings
import remocra.utils.DateUtils
import java.text.Normalizer

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

/**
 * Equivalent à la primitive "unaccent" de postgres, enfin en théorie, à vérifier au fur et à mesure. Objectif : permettre de faire un sortBy après récupération des données, dans le même ordre que ce que fournirait PG.
 */
public fun String.unaccent(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace("\\p{Mn}+".toRegex(), "")
