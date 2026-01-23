package remocra.usecase.zoneintegration

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.ZoneIntegrationData
import remocra.data.enums.ErrorType
import remocra.db.ZoneIntegrationRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateZoneIntegrationUseCase : AbstractCUDUseCase<ZoneIntegrationData>(TypeOperation.UPDATE) {

    @Inject lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_ZONE_COMPETENCE)) {
            throw RemocraResponseException(ErrorType.ZONE_INTEGRATION_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: ZoneIntegrationData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.zoneIntegrationId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ZONE_INTEGRATION,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: ZoneIntegrationData): ZoneIntegrationData {
        zoneIntegrationRepository.updateZoneIntegration(
            element.zoneIntegrationId,
            element.zoneIntegrationCode,
            element.zoneIntegrationLibelle,
            element.zoneIntegrationActif,
        )

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ZoneIntegrationData) {
    }
}
