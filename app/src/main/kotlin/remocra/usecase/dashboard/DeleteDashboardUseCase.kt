package remocra.usecase.dashboard

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DashboardConfigData
import remocra.data.enums.ErrorType
import remocra.db.DashboardRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteDashboardUseCase : AbstractCUDUseCase<DashboardConfigData>(TypeOperation.DELETE) {

    @Inject
    lateinit var dashboardRepository: DashboardRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DASHBOARD_A)) {
            throw RemocraResponseException(ErrorType.DASHBOARD_FORBIDDEN_CUD)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DashboardConfigData) {}

    override fun execute(userInfo: WrappedUserInfo, element: DashboardConfigData): DashboardConfigData {
        dashboardRepository.deleteProfil(element.dashboardId)
        dashboardRepository.deleteConfig(element.dashboardId)
        dashboardRepository.deleteDashboard(element.dashboardId)
        return element
    }

    override fun postEvent(element: DashboardConfigData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.dashboardId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DASHBOARD,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
