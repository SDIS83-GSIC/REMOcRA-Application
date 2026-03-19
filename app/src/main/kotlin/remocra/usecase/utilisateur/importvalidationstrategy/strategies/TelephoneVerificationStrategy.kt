package remocra.usecase.utilisateur.importvalidationstrategy.strategies

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.LigneImportUtilisateur
import remocra.usecase.utilisateur.LigneImportUserData
import remocra.usecase.utilisateur.importvalidationstrategy.VerificationUserStrategy

class TelephoneVerificationStrategy @Inject constructor() : VerificationUserStrategy {

    private fun isNumeric(phoneNumber: String): Boolean {
        return phoneNumber.matches("^\\d+$".toRegex())
    }

    private fun hasValidLength(phoneNumber: String): Boolean {
        return phoneNumber.length == 10
    }

    override fun validate(row: LigneImportUtilisateur, ligne: Int, userInfo: WrappedUserInfo, data: LigneImportUserData) {
        if (row.telephone.isNullOrEmpty()) return

        if (!isNumeric(row.telephone!!)) {
            data.addError(ligne, "Le numéro de téléphone doit contenir uniquement des chiffres.")
        }

        if (!hasValidLength(row.telephone!!)) {
            data.addError(ligne, "Le numéro de téléphone doit contenir exactement 10 chiffres.")
        }
    }
}
