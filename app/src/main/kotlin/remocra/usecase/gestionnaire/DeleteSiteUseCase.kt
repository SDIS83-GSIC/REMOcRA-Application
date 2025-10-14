package remocra.usecase.gestionnaire

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.SiteData
import remocra.data.enums.ErrorType
import remocra.db.SiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteSiteUseCase : AbstractCUDUseCase<SiteData>(TypeOperation.DELETE) {

    @Inject lateinit var siteRepository: SiteRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.GEST_SITE_A)) {
            throw RemocraResponseException(ErrorType.SITE_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: SiteData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.siteId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.SITE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: SiteData): SiteData {
        siteRepository.deleteSite(element.siteId)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: SiteData) {
        if (siteRepository.siteUsedInPei(siteId = element.siteId)) {
            throw RemocraResponseException(ErrorType.SITE_USED)
        }
    }
}
