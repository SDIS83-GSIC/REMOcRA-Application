package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.GroupeFonctionnalitesData
import remocra.data.Params
import remocra.db.GroupeFonctionnalitesRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.groupefonctionnalites.CreateGroupeFonctionnalitesUseCase
import remocra.usecase.admin.groupefonctionnalites.UpdateGroupeFonctionnalitesUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Produces("application/json; charset=UTF-8")
@Path("/groupe-fonctionnalites")
class GroupeFonctionnalitesEndpoint : AbstractEndpoint() {
    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var groupeFonctionnalitesRepository: GroupeFonctionnalitesRepository

    @Path("/")
    @POST
    @RequireDroits([Droit.ADMIN_GROUPE_UTILISATEUR])
    fun list(params: Params<GroupeFonctionnalitesRepository.Filter, GroupeFonctionnalitesRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                groupeFonctionnalitesRepository.getAll(params),
                groupeFonctionnalitesRepository.getCountAll(params),
            ),
        ).build()

    @Path("/{groupeFonctionnalitesId}")
    @GET
    @RequireDroits([Droit.ADMIN_GROUPE_UTILISATEUR])
    fun get(@PathParam("groupeFonctionnalitesId") groupeFonctionnalitesId: UUID): Response =
        Response.ok(groupeFonctionnalitesRepository.getById(groupeFonctionnalitesId)).build()

    @Inject lateinit var createGroupeFonctionnalitesUseCase: CreateGroupeFonctionnalitesUseCase

    @Path("/create")
    @POST
    @RequireDroits([Droit.ADMIN_GROUPE_UTILISATEUR])
    fun post(element: GroupeFonctionnalitesData): Response =
        createGroupeFonctionnalitesUseCase.execute(securityContext.userInfo, element).wrap()

    @Inject lateinit var updateGroupeFonctionnalitesUseCase: UpdateGroupeFonctionnalitesUseCase

    @Path("/update/{groupeFonctionnalitesId}")
    @PUT
    @RequireDroits([Droit.ADMIN_GROUPE_UTILISATEUR])
    fun put(@PathParam("groupeFonctionnalitesId") groupeFonctionnalitesId: UUID, element: GroupeFonctionnalitesData): Response =
        updateGroupeFonctionnalitesUseCase.execute(securityContext.userInfo, element.copy(groupeFonctionnalitesId = groupeFonctionnalitesId)).wrap()
}
