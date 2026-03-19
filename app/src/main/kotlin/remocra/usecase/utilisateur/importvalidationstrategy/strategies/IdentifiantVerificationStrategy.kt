package remocra.usecase.utilisateur.importvalidationstrategy.strategies

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.LigneImportUtilisateur
import remocra.db.UtilisateurRepository
import remocra.usecase.utilisateur.LigneImportUserData
import remocra.usecase.utilisateur.importvalidationstrategy.VerificationUserStrategy

class IdentifiantVerificationStrategy @Inject constructor(
    private val utilisateurRepository: UtilisateurRepository,
) : VerificationUserStrategy {

    override fun validate(row: LigneImportUtilisateur, ligne: Int, userInfo: WrappedUserInfo, data: LigneImportUserData) {
        if (row.identifiant.isNullOrEmpty()) {
            data.addError(ligne, "Le champ IDENTIFIANT doit être rempli.")
        } else if (row.identifiant!!.trim().length < 3) {
            data.addError(ligne, "Le champ IDENTIFIANT doit comporter au moins 3 caractères.")
        } else if (utilisateurRepository.checkExistsUsername(row.identifiant!!, null)) {
            data.addError(ligne, "L'identifiant est déjà utilisé par un autre utilisateur.")
        }
    }
}
