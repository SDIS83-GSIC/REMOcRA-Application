package remocra.usecase.utilisateur.importvalidationstrategy.strategies

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.LigneImportUtilisateur
import remocra.db.OrganismeRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.utilisateur.LigneImportUserData
import remocra.usecase.utilisateur.importvalidationstrategy.VerificationUserStrategy
import kotlin.collections.contains

class OrganismeVerificationStrategy @Inject constructor(
    private val organismeRepository: OrganismeRepository,
) : VerificationUserStrategy {

    override fun validate(row: LigneImportUtilisateur, ligne: Int, userInfo: WrappedUserInfo, data: LigneImportUserData) {
        if (row.organisme.isNullOrEmpty()) {
            data.addError(ligne, "Le champ ORGANISME doit être rempli.")
        } else {
            row.organismeId = organismeRepository.getByCode(row.organisme!!)?.organismeId

            if (row.organismeId == null) {
                data.addError(ligne, "L'organisme n'est pas connu dans l'application.")
            } else if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A) &&
                !userInfo.isSuperAdmin &&
                !userInfo.affiliatedOrganismeIds.orEmpty().contains(row.organismeId)
            ) {
                data.addError(
                    ligne,
                    "Vous n'avez pas les droits de gestion des utilisateurs pour l'organisme.",
                )
            }
        }
    }
}
