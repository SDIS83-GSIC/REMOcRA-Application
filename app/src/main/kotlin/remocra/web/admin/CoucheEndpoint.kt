package remocra.web.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
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
import jakarta.ws.rs.core.UriBuilder
import remocra.auth.AuthnConstants
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.CoucheFormData
import remocra.data.CoucheFormDataWithImage
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.CoucheRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.couches.CreateCoucheUseCase
import remocra.usecase.admin.couches.DeleteCoucheUseCase
import remocra.usecase.admin.couches.UpdateCoucheUseCase
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import remocra.web.carto.LayersEndpoint
import java.util.UUID
import kotlin.reflect.jvm.javaMethod

@Produces("application/json; charset=UTF-8")
@Path("/admin/couche")
class CoucheEndpoint : AbstractEndpoint() {
    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var createCoucheUseCase: CreateCoucheUseCase

    @Inject lateinit var updateCoucheUseCase: UpdateCoucheUseCase

    @Inject lateinit var deleteCoucheUseCase: DeleteCoucheUseCase

    @Inject lateinit var objectMapper: ObjectMapper

    @Path("/{coucheId}")
    @GET
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun get(@PathParam("coucheId") coucheId: UUID): Response {
        val coucheData = coucheRepository.getCouche(coucheId)
        return Response.ok(
            coucheData.copy(
                coucheIconeUrl = coucheData.coucheIcone?.let {
                    UriBuilder.fromPath(AuthnConstants.API_PATH)
                        .path(LayersEndpoint::class.java)
                        .path(LayersEndpoint::getIcone.javaMethod)
                        .build(coucheId)
                        .toString()
                },
                coucheLegendeUrl = coucheData.coucheLegende?.let {
                    UriBuilder.fromPath(AuthnConstants.API_PATH)
                        .path(LayersEndpoint::class.java)
                        .path(LayersEndpoint::getLegende.javaMethod)
                        .build(coucheId)
                        .toString()
                },
            ),

        ).build()
    }

    @POST
    @Path("/groupe-couche/{groupeCoucheId}")
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun list(
        @PathParam("groupeCoucheId") groupeCoucheId: UUID,
        params: Params<CoucheRepository.FilterCouche, CoucheRepository.Sort>,
    ): Response =
        Response.ok(
            DataTableau(
                list = coucheRepository.getAllCoucheForAdmin(groupeCoucheId, params),
                count = coucheRepository.countAllCoucheForAdmin(groupeCoucheId, params.filterBy),
            ),
        ).build()

    @POST
    @Path("/groupe-couche/{groupeCoucheId}/create")
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun create(
        @Context httpRequest: HttpServletRequest,
    ): Response =
        createCoucheUseCase.execute(
            securityContext.userInfo,
            CoucheFormDataWithImage(
                coucheFormData = objectMapper.readValue<CoucheFormData>(httpRequest.getTextPart("couche")),
                icone = httpRequest.getPart("icone"),
                legende = httpRequest.getPart("legende"),
            ),
        ).wrap()

    @PUT
    @Path("/groupe-couche/{groupeCoucheId}/update/{coucheId}")
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun update(
        @PathParam("coucheId") coucheId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response =
        updateCoucheUseCase.execute(
            securityContext.userInfo,
            CoucheFormDataWithImage(
                coucheFormData = objectMapper.readValue<CoucheFormData>(httpRequest.getTextPart("couche")).copy(coucheId = coucheId),
                icone = httpRequest.getPart("icone"),
                legende = httpRequest.getPart("legende"),
            ),
        ).wrap()

    @DELETE
    @Path("/groupe-couche/{groupeCoucheId}/delete/{coucheId}")
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun delete(@PathParam("coucheId") coucheId: UUID): Response =
        deleteCoucheUseCase.execute(
            securityContext.userInfo,
            CoucheFormDataWithImage(
                coucheFormData = coucheRepository.getCouche(coucheId),
                icone = null,
                legende = null,
            ),
        ).wrap()
}
