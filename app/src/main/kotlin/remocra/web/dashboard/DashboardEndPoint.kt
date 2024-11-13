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
import remocra.db.DashboardRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.dashboard.CreateDashboardUseCase
import remocra.usecase.dashboard.CreateQueryUseCase
import remocra.usecase.dashboard.DeleteQueryUseCase
import remocra.usecase.dashboard.GetDashboardQueryUseCase
import remocra.usecase.dashboard.UpdateDashboardQueryUseCase
import remocra.web.AbstractEndpoint
import java.util.*

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
class DashboardEndPoint : AbstractEndpoint() {

    @Inject lateinit var dashboardRepository: DashboardRepository

    @Inject lateinit var getDashboardQueryUseCase: GetDashboardQueryUseCase

    @Inject lateinit var createDashboardQueryUseCase: CreateQueryUseCase

    @Inject lateinit var updateDashboardQueryUseCase: UpdateDashboardQueryUseCase

    @Inject lateinit var deleteDashboardQueryUseCase: DeleteQueryUseCase

    @Inject lateinit var createDashboardDashboardUseCase: CreateDashboardUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @POST
    @Path("/validate-query")
    @RequireDroits([Droit.DASHBOARD_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun getDashboardQuery(queryDasboard: DashboardQueryRequestData): Response = Response.ok().entity(getDashboardQueryUseCase.getQuery(queryDasboard)).build()

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
    @Path("/get-data-query/{id}")
    @RequireDroits([Droit.DASHBOARD_R])
    fun getDataQuery(@PathParam("id") queryId: UUID): Response = Response.ok(getDashboardQueryUseCase.getDataQuery(queryId)).build()

    @POST
    @Path("/create-dashboard")
    @RequireDroits([Droit.DASHBOARD_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun createDashboard(
        dashboardConfig: DashboardConfigData,
    ): Response = createDashboardDashboardUseCase.execute(securityContext.userInfo, dashboardConfig).wrap()
}
