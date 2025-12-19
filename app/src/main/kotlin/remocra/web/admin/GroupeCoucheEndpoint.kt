package remocra.web.admin

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
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
import remocra.data.Params
import remocra.data.couche.GroupeCoucheData
import remocra.db.GroupeCoucheRepository
import remocra.db.GroupeCoucheRepository.FilterGroupeCouche
import remocra.db.GroupeCoucheRepository.Sort
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.couches.groupecouche.CreateGroupeCoucheUseCase
import remocra.usecase.admin.couches.groupecouche.DeleteGroupeCoucheUseCase
import remocra.usecase.admin.couches.groupecouche.UpdateGroupeCoucheUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Produces("application/json; charset=UTF-8")
@Path("/admin/groupe-couche")
class GroupeCoucheEndpoint : AbstractEndpoint() {
    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var groupeCoucheRepository: GroupeCoucheRepository

    @Inject
    lateinit var createGroupeCoucheUseCase: CreateGroupeCoucheUseCase

    @Inject
    lateinit var updateGroupeCoucheUseCase: UpdateGroupeCoucheUseCase

    @Inject
    lateinit var deleteGroupeCoucheUseCase: DeleteGroupeCoucheUseCase

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Path("/")
    @POST
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun list(
        params: Params<FilterGroupeCouche, Sort>,
    ): Response =
        Response.ok(
            DataTableau(
                groupeCoucheRepository.getAllForAdmin(params),
                groupeCoucheRepository.countForAdmin(params.filterBy),
            ),
        ).build()

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun create(groupeCoucheData: GroupeCoucheData): Response {
        return createGroupeCoucheUseCase.execute(
            securityContext.userInfo,
            groupeCoucheData,
        ).wrap()
    }

    @PUT
    @Path("/{groupeCoucheId}")
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun update(groupeCoucheData: GroupeCoucheData): Response {
        return updateGroupeCoucheUseCase.execute(
            securityContext.userInfo,
            groupeCoucheData,
        ).wrap()
    }

    @DELETE
    @Path("/{groupeCoucheId}")
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun update(@PathParam("groupeCoucheId") groupeCoucheId: UUID): Response {
        return deleteGroupeCoucheUseCase.execute(
            securityContext.userInfo,
            groupeCoucheRepository.getById(groupeCoucheId),
        ).wrap()
    }

    @GET
    @Path("/{groupeCoucheId}")
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun getGroupeCoucheById(@PathParam("groupeCoucheId") groupeCoucheId: UUID): Response {
        // Implementation goes here
        return Response.ok(groupeCoucheRepository.getById(groupeCoucheId)).build()
    }
}
