package remocra.usecase.utilisateur.importvalidationstrategy.strategies

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.LigneImportUtilisateur
import remocra.usecase.utilisateur.LigneImportUserData
import remocra.usecase.utilisateur.importvalidationstrategy.VerificationUserStrategy

class MailVerificationStrategy @Inject constructor() : VerificationUserStrategy {

    private fun isValidEmail(email: String): Boolean {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex())
    }

    override fun validate(row: LigneImportUtilisateur, ligne: Int, userInfo: WrappedUserInfo, data: LigneImportUserData) {
        if (row.mail.isNullOrEmpty()) {
            data.addError(ligne, "Le champ MAIL doit être rempli.")
        }

        // Vérification du format de l'email
        if (!isValidEmail(row.mail!!)) {
            data.addError(ligne, "Format de mail invalide.")
        }
    }
}
