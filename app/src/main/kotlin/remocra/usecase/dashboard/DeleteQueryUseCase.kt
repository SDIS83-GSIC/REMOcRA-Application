package remocra.usecase.dashboard
import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.DashboardRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class DeleteQueryUseCase : AbstractCUDUseCase<UUID>(TypeOperation.DELETE) {
    @Inject
    lateinit var dashboardRepository: DashboardRepository
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DASHBOARD_A)) {
            throw RemocraResponseException(ErrorType.DASHBOARD_FORBIDDEN_CUD)
        }
    }
    override fun checkContraintes(userInfo: WrappedUserInfo, element: UUID) {
        // Vérifier que la requête n'est pas utilisée dans un dashboard
        if (dashboardRepository.getComponentsByQuery(element).isNotEmpty()) {
            throw RemocraResponseException(ErrorType.DASHBOARD_QUERY_IN_USE)
        }
    }
    override fun execute(userInfo: WrappedUserInfo, element: UUID): UUID {
        dashboardRepository.deleteComponentsByQueryIds(element)
        dashboardRepository.deleteQueryById(element)
        return element
    }
    override fun postEvent(element: UUID, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DASHBOARD,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
