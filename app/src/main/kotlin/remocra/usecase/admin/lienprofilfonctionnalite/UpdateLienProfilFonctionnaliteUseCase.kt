package remocra.usecase.admin.lienprofilfonctionnalite

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.LienProfilFonctionnaliteUpdateData
import remocra.data.enums.ErrorType
import remocra.db.LienProfilFonctionnaliteRepository
import remocra.db.ProfilOrganismeRepository
import remocra.db.ProfilUtilisateurRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateLienProfilFonctionnaliteUseCase @Inject constructor(
    private val lienProfilFonctionnaliteRepository: LienProfilFonctionnaliteRepository,
    private val profilOrganismeRepository: ProfilOrganismeRepository,
    private val profilUtilisateurRepository: ProfilUtilisateurRepository,
) :
    AbstractCUDUseCase<LienProfilFonctionnaliteUpdateData>(
        TypeOperation.UPDATE,
    ) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_GROUPE_UTILISATEUR)) {
            throw RemocraResponseException(ErrorType.PROFIL_DROIT_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: LienProfilFonctionnaliteUpdateData, userInfo: WrappedUserInfo) { }

    override fun execute(userInfo: WrappedUserInfo, element: LienProfilFonctionnaliteUpdateData): LienProfilFonctionnaliteUpdateData {
        lienProfilFonctionnaliteRepository.update(element.newValue, element.profilOrganismeId, element.profilUtilisateurId)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: LienProfilFonctionnaliteUpdateData) {
        if (profilOrganismeRepository.get(element.profilOrganismeId).profilOrganismeTypeOrganismeId !=
            profilUtilisateurRepository.get(element.profilUtilisateurId).profilUtilisateurTypeOrganismeId
        ) {
            throw RemocraResponseException(ErrorType.LIEN_PROFIL_FONCTIONNALITE_WRONG_TYPE)
        }
    }
}
