package remocra.usecase.dashboard

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DashboardData
import remocra.data.DashboardQueryRequestData
import remocra.data.QueryIds
import remocra.db.DashboardRepository
import remocra.db.jooq.remocra.tables.pojos.DashboardQuery
import remocra.usecase.AbstractUseCase
import remocra.utils.RequestUtils
import java.util.UUID

class GetDashboardQueryUseCase
@Inject
constructor(
    private val dashboardRepository: DashboardRepository,
    private val requestUtils: RequestUtils,
) :
    AbstractUseCase() {

    /**
     * Valide que la requête est formée convenablement, en remplaçant les placeholders statiques.
     */
    fun validateQuery(sqlQuery: DashboardQueryRequestData, saveQuery: Boolean): RequestUtils.FieldData? {
        val replacedDashboardQueryRequestData = sqlQuery.copy(
            query = requestUtils.replaceGlobalParameters(
                sqlQuery.query,
                zoneCompetenceId = sqlQuery.zoneCompetenceId,
                organismeId = sqlQuery.organismeId,
                utilisateurId = sqlQuery.utilisateurId,
            ),
        )

        requestUtils.validateReadOnlyQuery(replacedDashboardQueryRequestData.query)

        if (saveQuery && replacedDashboardQueryRequestData.queryId != null) {
            val queryWithoutRuntimeParams = requestUtils.replaceGlobalParameters(
                sqlQuery.query,
                zoneCompetenceId = null,
                organismeId = null,
                utilisateurId = null,
            )
            requestUtils.validateReadOnlyQuery(queryWithoutRuntimeParams)
            // Exécute aussi la version template pour remonter les erreurs SQL runtime avant sauvegarde.
            dashboardRepository.getQuery(queryWithoutRuntimeParams)

            dashboardRepository.updateQuery(
                DashboardQuery(
                    dashboardQueryId = replacedDashboardQueryRequestData.queryId,
                    dashboardQueryTitle = replacedDashboardQueryRequestData.queryTitle,
                    // On stocke la version originale, avec les placeholders non remplacés.
                    dashboardQueryQuery = sqlQuery.query,
                ),
            )
        }

        val dataSqlQuery = dashboardRepository.getQuery(replacedDashboardQueryRequestData.query)
        return requestUtils.mapQueryToFieldData(dataSqlQuery, sqlQuery)
    }

    fun getDataQuery(queryId: UUID, userInfo: WrappedUserInfo): RequestUtils.FieldData? {
        val requestSql = dashboardRepository.getRequest(queryId)?.let {
            // On remplace les placeholders habituels si l'utilisateur est connecté
            DashboardQueryRequestData(
                queryId = it.queryId,
                query = requestUtils.replaceGlobalParameters(userInfo = userInfo, requeteSql = it.query),
                queryTitle = it.queryTitle,
                null,
                null,
                null,
            )
        }

        return requestSql?.let { validateQuery(it, false) }
    }

    fun getDataQuerys(queryIds: QueryIds, userInfo: WrappedUserInfo): MutableList<RequestUtils.FieldData?> {
        val results: MutableList<RequestUtils.FieldData?> = mutableListOf() // Liste pour stocker les résultats

        for (queryId in queryIds.dashboardQueryIds) {
            val result = getDataQuery(queryId, userInfo)
            if (result != null) {
                results.add(result)
            }
        }
        return results
    }

    fun getDashboardsUser(userInfo: WrappedUserInfo): DashboardData {
        val dashboard = dashboardRepository.getDashboardUser(userInfo)
        val components = dashboard?.let { dashboardRepository.getDashboardConfig(it.dashboardId) }
        return DashboardData(
            dashboardId = dashboard?.dashboardId,
            dashboardTitle = dashboard?.dashboardTitle,
            dashboardComponents = components,
        )
    }
}
