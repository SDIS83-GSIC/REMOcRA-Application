package remocra.usecase.utilisateur.importvalidationstrategy

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.LigneImportUtilisateur
import remocra.usecase.AbstractUseCase
import remocra.usecase.utilisateur.LigneImportUserData
import remocra.usecase.utilisateur.importvalidationstrategy.strategies.IdentifiantVerificationStrategy
import remocra.usecase.utilisateur.importvalidationstrategy.strategies.MailVerificationStrategy
import remocra.usecase.utilisateur.importvalidationstrategy.strategies.OrganismeVerificationStrategy
import remocra.usecase.utilisateur.importvalidationstrategy.strategies.ProfilUtilisateurVerificationStrategy
import remocra.usecase.utilisateur.importvalidationstrategy.strategies.TelephoneVerificationStrategy

class VerificationUserDataUseCase @Inject constructor(
    private val identifiantVerificationStrategy: IdentifiantVerificationStrategy,
    private val mailVerificationStrategy: MailVerificationStrategy,
    private val organismeVerificationStrategy: OrganismeVerificationStrategy,
    private val profilUtilisateurVerificationStrategy: ProfilUtilisateurVerificationStrategy,
    private val telephoneVerificationStrategy: TelephoneVerificationStrategy,
) : AbstractUseCase() {
    private val strategies: List<VerificationUserStrategy> =
        listOf(
            mailVerificationStrategy,
            identifiantVerificationStrategy,
            organismeVerificationStrategy,
            profilUtilisateurVerificationStrategy,
            telephoneVerificationStrategy,
        )

    fun execute(row: LigneImportUtilisateur, ligne: Int, userInfo: WrappedUserInfo, data: LigneImportUserData) {
        strategies.forEach { strategy ->
            strategy.validate(row, ligne, userInfo, data)
        }
    }
}
