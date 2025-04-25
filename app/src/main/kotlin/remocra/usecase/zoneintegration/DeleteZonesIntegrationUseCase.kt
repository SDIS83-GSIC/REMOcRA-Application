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

class DeleteZonesIntegrationUseCase : AbstractCUDUseCase<ZoneIntegrationData>(TypeOperation.DELETE) {

    @Inject lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_PARAM_APPLI)) {
            throw RemocraResponseException(ErrorType.ZONE_INTEGRATION_FORBIDDEN_DELETE)
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
        zoneIntegrationRepository.delete(element.zoneIntegrationId)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ZoneIntegrationData) {
        // Une zone d'intégration est soit une zone de compétence, soit une zone spéciale
        // Si la première condition est vraie, la seconde sera forcément fausse
        val referencedIn = when {
            zoneIntegrationRepository.existsInOrganisme(element.zoneIntegrationId) -> "Organisme - Zone de compétence"
            zoneIntegrationRepository.existsInPei(element.zoneIntegrationId) -> "PEI - Zone spéciale"
            else -> null
        }

        referencedIn?.let {
            throw RemocraResponseException(ErrorType.ZONE_INTEGRATION_STILL_IN_USE, it)
        }
    }
}
