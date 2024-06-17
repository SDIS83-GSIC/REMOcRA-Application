package remocra.db

import java.util.UUID

/**
 * Interface décrivant le comportement d'un repository de nomenclature
 * T correspond au POJO manipulé
 */
interface NomenclatureRepository<T> {

    fun getMapById(): Map<UUID, T>
}
