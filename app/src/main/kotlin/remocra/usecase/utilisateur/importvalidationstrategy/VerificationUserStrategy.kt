package remocra.usecase.utilisateur.importvalidationstrategy

import remocra.auth.WrappedUserInfo
import remocra.data.LigneImportUtilisateur
import remocra.usecase.utilisateur.LigneImportUserData

interface VerificationUserStrategy {
    fun validate(row: LigneImportUtilisateur, ligne: Int, userInfo: WrappedUserInfo, data: LigneImportUserData)
}
