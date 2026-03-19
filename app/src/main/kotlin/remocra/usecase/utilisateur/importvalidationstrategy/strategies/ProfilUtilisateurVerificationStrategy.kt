package remocra.usecase.utilisateur.importvalidationstrategy.strategies

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.LigneImportUtilisateur
import remocra.db.ProfilUtilisateurRepository
import remocra.usecase.utilisateur.LigneImportUserData
import remocra.usecase.utilisateur.importvalidationstrategy.VerificationUserStrategy

class ProfilUtilisateurVerificationStrategy @Inject constructor(
    private val profilUtilisateurRepository: ProfilUtilisateurRepository,
) : VerificationUserStrategy {

    override fun validate(row: LigneImportUtilisateur, ligne: Int, userInfo: WrappedUserInfo, data: LigneImportUserData) {
        if (row.profil_utilisateur.isNullOrEmpty()) {
            data.addError(ligne, "Le champ PROFIL UTILISATEUR doit être rempli.")
        } else {
            row.profilUtilisateurId = profilUtilisateurRepository.getByCode(row.profil_utilisateur!!)?.profilUtilisateurId

            if (row.profilUtilisateurId == null) {
                data.addError(ligne, "Le profil utilisateur n'est pas connu dans l'application.")
            }
        }
    }
}
