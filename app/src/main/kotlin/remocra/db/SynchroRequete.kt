package remocra.db

import remocra.data.IdCodeVersion

/**
 * Interface permettant d'avoir les différentes requêtes pour réaliser la synchronisation.
 */
interface SynchroRequete<T : IdCodeVersion> {

    /**
     * Récupère tous les nouveaux éléments présents dans l'entrepotsig
     */
    fun getAllNewElementsFromSig(): List<T>

    /**
     * Récupère tous les anciens éléments présents dans l'entrepotsig en comparant avec REMOcRA
     */
    fun getAllOldElementsFromSig(): Map<String, T>

    /**
     * Récupère tous les anciens éléments présents dans REMOcRA en comparant avec l'entrepotsig
     */
    fun getAllOldElementsFromRemocra(): List<T>
}
