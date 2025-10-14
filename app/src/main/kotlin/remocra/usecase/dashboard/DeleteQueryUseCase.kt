package remocra.usecase.dashboard
import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DashboardQueryData
import remocra.data.enums.ErrorType
import remocra.db.DashboardRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteQueryUseCase : AbstractCUDUseCase<DashboardQueryData>(TypeOperation.DELETE) {
    @Inject
    lateinit var dashboardRepository: DashboardRepository
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DASHBOARD_A)) {
            throw RemocraResponseException(ErrorType.DASHBOARD_FORBIDDEN_CUD)
        }
    }
    override fun checkContraintes(userInfo: WrappedUserInfo, element: DashboardQueryData) {
        // Vérifier que la requête n'est pas utilisée dans un dashboard
        if (dashboardRepository.existsComponentsConfigByQuery(element.queryId)) {
            throw RemocraResponseException(ErrorType.DASHBOARD_QUERY_IN_USE)
        }
    }
    override fun execute(userInfo: WrappedUserInfo, element: DashboardQueryData): DashboardQueryData {
        dashboardRepository.deleteComponentsByQueryIds(element.queryId)
        dashboardRepository.deleteQueryById(element.queryId)
        return element
    }
    override fun postEvent(element: DashboardQueryData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.queryId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DASHBOARD_QUERY,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
