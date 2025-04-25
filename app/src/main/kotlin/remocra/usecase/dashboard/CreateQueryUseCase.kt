package remocra.usecase.dashboard

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DashboardQueryData
import remocra.data.enums.ErrorType
import remocra.db.DashboardRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.DashboardComponent
import remocra.db.jooq.remocra.tables.pojos.DashboardQuery
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.utils.RequestUtils

class CreateQueryUseCase : AbstractCUDUseCase<DashboardQueryData>(TypeOperation.INSERT) {

    @Inject
    lateinit var dashboardRepository: DashboardRepository

    @Inject
    lateinit var requestUtils: RequestUtils

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DASHBOARD_A)) {
            throw RemocraResponseException(ErrorType.DASHBOARD_FORBIDDEN_CUD)
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: DashboardQueryData): DashboardQueryData {
        dashboardRepository.insertQuery(
            DashboardQuery(
                dashboardQueryId = element.queryId,
                dashboardQueryTitle = element.queryTitle,
                dashboardQueryQuery = element.queryQuery,
            ),
        )

        element.queryComponents.forEach { component ->
            dashboardRepository.insertComponent(
                DashboardComponent(
                    dashboardComponentId = component.componentId,
                    dashboardComponentDahsboardQueryId = element.queryId,
                    dashboardComponentKey = component.componentKey,
                    dashboardComponentConfig = component.componentConfig,
                    dashboardComponentTitle = component.componentTitle,
                ),
            )
        }
        return element
    }

    override fun postEvent(element: DashboardQueryData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.queryId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DASHBOARD,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DashboardQueryData) {
        requestUtils.validateReadOnlyQuery(element.queryQuery)
    }
}
