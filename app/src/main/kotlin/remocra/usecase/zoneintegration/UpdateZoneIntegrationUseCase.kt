package remocra.usecase.zoneintegration

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.ZoneIntegrationData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.ZoneIntegrationRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateZoneIntegrationUseCase : AbstractCUDUseCase<ZoneIntegrationData>(TypeOperation.UPDATE) {

    @Inject lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_PARAM_APPLI)) {
            throw RemocraResponseException(ErrorType.ZONE_INTEGRATION_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: ZoneIntegrationData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.zoneIntegrationId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ZONE_INTEGRATION,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: ZoneIntegrationData): ZoneIntegrationData {
        zoneIntegrationRepository.updateZoneIntegration(
            element.zoneIntegrationId,
            element.zoneIntegrationCode,
            element.zoneIntegrationLibelle,
            element.zoneIntegrationActif,
        )

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: ZoneIntegrationData) {
    }
}
