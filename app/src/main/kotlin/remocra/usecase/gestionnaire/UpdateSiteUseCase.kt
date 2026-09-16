package remocra.usecase.gestionnaire

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.SiteData
import remocra.data.enums.ErrorType
import remocra.db.SiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.pei.PeiUseCase
import remocra.usecase.pei.UpdatePeiUseCase

class UpdateSiteUseCase
@Inject
constructor(
    private val siteRepository: SiteRepository,
    private val peiUseCase: PeiUseCase,
    private val updatePeiUseCase: UpdatePeiUseCase,
) :
    AbstractCUDUseCase<SiteData>(TypeOperation.UPDATE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.GEST_SITE_A)) {
            throw RemocraResponseException(ErrorType.SITE_FORBIDDEN_UPDATE)
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
        val initialSiteData = siteRepository.getById(element.siteId)

        siteRepository.updateSite(
            element.siteId,
            element.siteGestionnaireId,
            element.siteCode,
            element.siteLibelle,
            element.siteActif,
        )

        if (element.siteGestionnaireId != initialSiteData.siteGestionnaireId) {
            siteRepository.getPeiId(element.siteId).forEach { peiId ->
                peiUseCase.getInfoPei(peiId).let { peiData ->
                    val peiNeedsUpdate = when (peiData) {
                        is PibiData -> peiData.copy(peiGestionnaireIdInitial = null, peiGestionnaireId = element.siteGestionnaireId)
                        is PenaData -> peiData.copy(peiGestionnaireIdInitial = null, peiGestionnaireId = element.siteGestionnaireId)
                        else -> throw RemocraResponseException(ErrorType.SITE_ERROR_ON_UPDATE, "Type de PEI non supporté : ${peiData.peiTypePei}")
                    }
                    val result = updatePeiUseCase.execute(userInfo, peiNeedsUpdate, transactionManager)
                    if (result !is Result.Success) {
                        throw RemocraResponseException(ErrorType.SITE_ERROR_ON_UPDATE, "$peiId : ${if (result is Result.Error) result.message else ""}")
                    }
                }
            }
        }

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: SiteData) {
        // no op
    }
}
