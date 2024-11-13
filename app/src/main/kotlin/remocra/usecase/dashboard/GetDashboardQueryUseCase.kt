package remocra.usecase.dashboard

import com.google.inject.Inject
import remocra.data.DashboardQueryRequestData
import remocra.db.DashboardRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.RequestUtils
import java.util.UUID

class GetDashboardQueryUseCase : AbstractUseCase() {

    @Inject
    lateinit var dashboardRepository: DashboardRepository

    @Inject
    lateinit var requestUtils: RequestUtils

    fun getQuery(sqlQuery: DashboardQueryRequestData): RequestUtils.FieldData? {
        requestUtils.validateReadOnlyQuery(sqlQuery.query) // Vérifie que la requête SQL soit valide
        val parseQuery = requestUtils.parseSQLQuery(sqlQuery.query) // Déconstruit la requête SQL brute
        val queryGetGeometryCol = requestUtils.generateSQLQuery(parseQuery) // Génère requête qui retourne le type des colonnes
        val dataGeometry = dashboardRepository.getQuery(queryGetGeometryCol) // Retourne le type des colonnes
        val rewriteQuery = requestUtils.rewriteQueryWithGeoJSON(sqlQuery.query, dataGeometry) // Réécrit la requête en castant les colonnes de type geométrie
        val dataSqlQuery = dashboardRepository.getQuery(rewriteQuery)
        return requestUtils.mapQueryToFieldData(dataSqlQuery, sqlQuery)
    }

    fun getDataQuery(queryId: UUID): RequestUtils.FieldData? {
        val requestSql = dashboardRepository.getRequest(queryId)
        return requestSql?.let { getQuery(it) }
    }
}
