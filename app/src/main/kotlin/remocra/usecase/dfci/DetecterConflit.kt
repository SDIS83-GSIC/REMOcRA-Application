package remocra.usecase.dfci

import remocra.auth.WrappedUserInfo
import remocra.data.IdCodeVersion

/**
 * Interface pour les UseCases permettant de détecter les conflits dans le module DFCI
 */
interface DetecterConflit<T : IdCodeVersion> {

    /**
     * Détecte les conflits entre l'élément du SIG et celui de REMOcRA
     */
    fun detecterConflit(remocraElem: T, sigElem: T, userInfo: WrappedUserInfo): Boolean
}
