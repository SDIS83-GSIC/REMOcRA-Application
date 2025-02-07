package remocra.usecase.zoneintegration

import jakarta.inject.Inject
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

class DeleteZonesIntegrationUseCase : AbstractCUDUseCase<ZoneIntegrationData>(TypeOperation.DELETE) {

    @Inject lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_PARAM_APPLI)) {
            throw RemocraResponseException(ErrorType.ZONE_INTEGRATION_FORBIDDEN_DELETE)
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
        zoneIntegrationRepository.delete(element.zoneIntegrationId)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: ZoneIntegrationData) {
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
