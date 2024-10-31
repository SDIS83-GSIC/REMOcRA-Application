package remocra.usecase.admin.lienprofilfonctionnalite

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.LienProfilFonctionnaliteRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.LProfilUtilisateurOrganismeDroit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteLienProfilFonctionnaliteUseCase @Inject constructor(private val lienProfilFonctionnaliteRepository: LienProfilFonctionnaliteRepository) :
    AbstractCUDUseCase<LProfilUtilisateurOrganismeDroit>(
        TypeOperation.DELETE,
    ) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.PROFIL_DROIT_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: LProfilUtilisateurOrganismeDroit, userInfo: UserInfo) { }

    override fun execute(userInfo: UserInfo?, element: LProfilUtilisateurOrganismeDroit): LProfilUtilisateurOrganismeDroit {
        lienProfilFonctionnaliteRepository.delete(element)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: LProfilUtilisateurOrganismeDroit) { }
}
