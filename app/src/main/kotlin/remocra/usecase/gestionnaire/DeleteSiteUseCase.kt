package remocra.usecase.gestionnaire

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.SiteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.SiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteSiteUseCase : AbstractCUDUseCase<SiteData>(TypeOperation.DELETE) {

    @Inject lateinit var siteRepository: SiteRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.GEST_SITE_A)) {
            throw RemocraResponseException(ErrorType.SITE_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: SiteData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.siteId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.SITE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: SiteData): SiteData {
        siteRepository.deleteSite(element.siteId)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: SiteData) {
        if (siteRepository.siteUsedInPei(siteId = element.siteId)) {
            throw RemocraResponseException(ErrorType.SITE_USED)
        }
    }
}
