package remocra.usecase.dashboard

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.DashboardConfigData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DashboardRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Dashboard
import remocra.db.jooq.remocra.tables.pojos.DashboardConfig
import remocra.db.jooq.remocra.tables.pojos.LDashboardProfil
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateDashboardUseCase : AbstractCUDUseCase<DashboardConfigData>(TypeOperation.UPDATE) {

    @Inject
    lateinit var dashboardRepository: DashboardRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.DASHBOARD_A)) {
            throw RemocraResponseException(ErrorType.DASHBOARD_FORBIDDEN_CUD)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: DashboardConfigData) {}

    override fun execute(userInfo: UserInfo?, element: DashboardConfigData): DashboardConfigData {
        dashboardRepository.updateDashboard(
            Dashboard(
                dashboardId = element.dashboardId,
                dashboardTitle = element.dashboardTitle,
            ),
        )

        dashboardRepository.deleteProfil(element.dashboardId)

        element.dashboardProfilsId?.forEach { profil ->
            dashboardRepository.insertProfil(
                LDashboardProfil(
                    dashboardId = element.dashboardId,
                    profilUtilisateurId = profil,
                ),
            )
        }

        dashboardRepository.deleteConfig(element.dashboardId)

        element.dashboardComponents.forEach { component ->
            dashboardRepository.insertConfig(
                DashboardConfig(
                    dashboardConfigDashboardId = element.dashboardId,
                    dashboardConfigDashboardComponentId = component.componentId,
                    dashboardConfigDashboardComponentPositionConfig = component.componentConfig,
                ),
            )
        }
        return element
    }

    override fun postEvent(element: DashboardConfigData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.dashboardId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DASHBOARD,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }
}
