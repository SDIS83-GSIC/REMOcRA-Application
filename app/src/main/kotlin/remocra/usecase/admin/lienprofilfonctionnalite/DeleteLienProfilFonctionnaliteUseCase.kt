package remocra.usecase.admin.lienprofilfonctionnalite

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.LienProfilFonctionnaliteRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.LProfilUtilisateurOrganismeGroupeFonctionnalites
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteLienProfilFonctionnaliteUseCase @Inject constructor(private val lienProfilFonctionnaliteRepository: LienProfilFonctionnaliteRepository) :
    AbstractCUDUseCase<LProfilUtilisateurOrganismeGroupeFonctionnalites>(
        TypeOperation.DELETE,
    ) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_GROUPE_UTILISATEUR)) {
            throw RemocraResponseException(ErrorType.GROUPE_FONCTIONNALITES_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: LProfilUtilisateurOrganismeGroupeFonctionnalites, userInfo: WrappedUserInfo) { }

    override fun execute(userInfo: WrappedUserInfo, element: LProfilUtilisateurOrganismeGroupeFonctionnalites): LProfilUtilisateurOrganismeGroupeFonctionnalites {
        lienProfilFonctionnaliteRepository.delete(element)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: LProfilUtilisateurOrganismeGroupeFonctionnalites) { }
}
