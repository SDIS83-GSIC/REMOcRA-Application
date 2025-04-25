package remocra.web.dashboard

import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DashboardConfigData
import remocra.data.DashboardQueryData
import remocra.data.DashboardQueryRequestData
import remocra.data.QueryIds
import remocra.db.DashboardRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.dashboard.CreateDashboardUseCase
import remocra.usecase.dashboard.CreateQueryUseCase
import remocra.usecase.dashboard.DeleteDashboardUseCase
import remocra.usecase.dashboard.DeleteQueryUseCase
import remocra.usecase.dashboard.GetDashboardQueryUseCase
import remocra.usecase.dashboard.UpdateDashboardQueryUseCase
import remocra.usecase.dashboard.UpdateDashboardUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
class DashboardEndPoint : AbstractEndpoint() {

    @Inject lateinit var dashboardRepository: DashboardRepository

    @Inject lateinit var getDashboardQueryUseCase: GetDashboardQueryUseCase

    @Inject lateinit var createDashboardQueryUseCase: CreateQueryUseCase

    @Inject lateinit var updateDashboardQueryUseCase: UpdateDashboardQueryUseCase

    @Inject lateinit var deleteDashboardQueryUseCase: DeleteQueryUseCase

    @Inject lateinit var createDashboardDashboardUseCase: CreateDashboardUseCase

    @Inject lateinit var updateDashboardUseCase: UpdateDashboardUseCase

    @Inject lateinit var deleteDashboardUseCase: DeleteDashboardUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @POST
    @Path("/validate-query")
    @RequireDroits([Droit.DASHBOARD_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun validateQuery(queryDashboard: DashboardQueryRequestData): Response = Response.ok().entity(getDashboardQueryUseCase.validateQuery(queryDashboard, true)).build()

    @POST
    @Path("/create-query")
    @RequireDroits([Droit.DASHBOARD_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun create(
        dashboardData: DashboardQueryData,
    ): Response = createDashboardQueryUseCase.execute(securityContext.userInfo, dashboardData).wrap()

    @PUT
    @Path("/update-query")
    @RequireDroits([Droit.DASHBOARD_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun update(
        dashboardData: DashboardQueryData,
    ): Response = updateDashboardQueryUseCase.execute(securityContext.userInfo, dashboardData).wrap()

    @GET
    @Path("/get-list-query")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getQueryList(): Response = Response.ok(dashboardRepository.getQueryList()).build()

    @GET
    @Path("/get-list-dashboard")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getDashboardList(): Response = Response.ok(dashboardRepository.getDashboardList()).build()

    @GET
    @Path("/get-dashboard-user")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getDashboardUser(): Response = Response.ok(getDashboardQueryUseCase.getDashboardsUser(securityContext.userInfo)).build()

    @GET
    @Path("/get-dashboard-profil-available")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getDashboardAvailableProfilList(): Response = Response.ok(dashboardRepository.getAvailableProfil()).build()

    @GET
    @Path("/get-dashboard-list-profil/{id}")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getDashboardProfilList(@PathParam("id") dashboardId: UUID): Response = Response.ok(dashboardRepository.getDashboardProfil(dashboardId)).build()

    @POST
    @Path("/get-list-data-query")
    @RequireDroits([Droit.DASHBOARD_A])
    fun getDataQueryList(queryIds: QueryIds): Response = Response.ok(getDashboardQueryUseCase.getDataQuerys(queryIds, securityContext.userInfo)).build()

    @GET
    @Path("/get-query-list-all")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getQueryListAllComponents(): Response = Response.ok(dashboardRepository.getQueryListAllComponents()).build()

    @GET
    @Path("/get-components/{id}")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getComponentsByQuery(@PathParam("id") queryId: UUID): Response = Response.ok(dashboardRepository.getComponentsByQuery(queryId)).build()

    @DELETE
    @Path("/delete-query/{id}")
    @RequireDroits([Droit.DASHBOARD_A])
    fun delete(@PathParam("id") id: UUID): Response = deleteDashboardQueryUseCase.execute(securityContext.userInfo, id).wrap()

    @GET
    @Path("/get-component-config/{id}")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getComponentConfig(@PathParam("id") componentId: UUID): Response = Response.ok(dashboardRepository.getComponentsConfig(componentId)).build()

    @GET
    @Path("/get-dashboard-config/{id}")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getDashboardConfig(@PathParam("id") dashboardId: UUID): Response = Response.ok(dashboardRepository.getDashboardConfig(dashboardId)).build()

    @GET
    @Path("/get-data-query/{id}")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getDataQuery(@PathParam("id") queryId: UUID): Response = Response.ok(getDashboardQueryUseCase.getDataQuery(queryId, securityContext.userInfo)).build()

    @POST
    @Path("/create-dashboard")
    @RequireDroits([Droit.DASHBOARD_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun createDashboard(
        dashboardConfig: DashboardConfigData,
    ): Response = createDashboardDashboardUseCase.execute(securityContext.userInfo, dashboardConfig).wrap()

    @PUT
    @Path("/update-dashboard")
    @RequireDroits([Droit.DASHBOARD_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun updateDashboard(
        dashboardConfig: DashboardConfigData,
    ): Response = updateDashboardUseCase.execute(securityContext.userInfo, dashboardConfig).wrap()

    @DELETE
    @Path("/delete-dashboard/{id}")
    @RequireDroits([Droit.DASHBOARD_A])
    fun deleteDashboard(@PathParam("id") id: UUID): Response = deleteDashboardUseCase.execute(securityContext.userInfo, id).wrap()
}
