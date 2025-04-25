package remocra.usecase.admin.lienprofilfonctionnalite

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.LienProfilFonctionnaliteRepository
import remocra.db.ProfilOrganismeRepository
import remocra.db.ProfilUtilisateurRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.LProfilUtilisateurOrganismeDroit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateLienProfilFonctionnaliteUseCase @Inject constructor(
    private val lienProfilFonctionnaliteRepository: LienProfilFonctionnaliteRepository,
    private val profilOrganismeRepository: ProfilOrganismeRepository,
    private val profilUtilisateurRepository: ProfilUtilisateurRepository,
) :
    AbstractCUDUseCase<LProfilUtilisateurOrganismeDroit>(
        TypeOperation.INSERT,
    ) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_GROUPE_UTILISATEUR)) {
            throw RemocraResponseException(ErrorType.PROFIL_DROIT_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: LProfilUtilisateurOrganismeDroit, userInfo: WrappedUserInfo) { }

    override fun execute(userInfo: WrappedUserInfo, element: LProfilUtilisateurOrganismeDroit): LProfilUtilisateurOrganismeDroit {
        lienProfilFonctionnaliteRepository.insert(element)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: LProfilUtilisateurOrganismeDroit) {
        if (lienProfilFonctionnaliteRepository.get(element.profilOrganismeId, element.profilUtilisateurId) != null) {
            throw RemocraResponseException(ErrorType.LIEN_PROFIL_FONCTIONNALITE_EXISTS)
        }

        if (profilOrganismeRepository.get(element.profilOrganismeId).profilOrganismeTypeOrganismeId !=
            profilUtilisateurRepository.get(element.profilUtilisateurId).profilUtilisateurTypeOrganismeId
        ) {
            throw RemocraResponseException(ErrorType.LIEN_PROFIL_FONCTIONNALITE_WRONG_TYPE)
        }
    }
}
