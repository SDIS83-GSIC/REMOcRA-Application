package remocra.usecase.dashboard

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DashboardRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class DeleteDashboardUseCase : AbstractCUDUseCase<UUID>(TypeOperation.DELETE) {

    @Inject
    lateinit var dashboardRepository: DashboardRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.DASHBOARD_A)) {
            throw RemocraResponseException(ErrorType.DASHBOARD_FORBIDDEN_CUD)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: UUID) {}

    override fun execute(userInfo: UserInfo?, element: UUID): UUID {
        dashboardRepository.deleteProfil(element)
        dashboardRepository.deleteConfig(element)
        dashboardRepository.deleteDashboard(element)
        return element
    }

    override fun postEvent(element: UUID, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DASHBOARD,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }
}
