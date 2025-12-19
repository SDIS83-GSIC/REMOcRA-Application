package remocra.web.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
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
import remocra.data.CoucheData
import remocra.data.CoucheFormData
import remocra.data.CoucheFormDataWithImage
import remocra.data.DataTableau
import remocra.data.GroupeCoucheData
import remocra.data.Params
import remocra.db.CoucheRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.couches.CreateCoucheUseCase
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

    @Inject lateinit var objectMapper: ObjectMapper

    @Path("/")
    @GET
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun list(): Response =
        Response.ok(
            object {
                val groupeCoucheList = coucheRepository.getGroupeCoucheList()
                    .map {
                            groupeCouche ->
                        GroupeCoucheData(
                            groupeCoucheId = groupeCouche.groupeCoucheId,
                            groupeCoucheCode = groupeCouche.groupeCoucheCode,
                            groupeCoucheLibelle = groupeCouche.groupeCoucheLibelle,
                            groupeCoucheOrdre = groupeCouche.groupeCoucheOrdre,
                            coucheList = coucheRepository.getCoucheList(groupeCouche.groupeCoucheId).map { couche ->
                                CoucheData(
                                    coucheId = couche.coucheId,
                                    coucheCode = couche.coucheCode,
                                    coucheLibelle = couche.coucheLibelle,
                                    coucheOrdre = couche.coucheOrdre,
                                    coucheSource = couche.coucheSource,
                                    coucheProjection = couche.coucheProjection,
                                    coucheUrl = couche.coucheUrl,
                                    coucheNom = couche.coucheNom,
                                    coucheFormat = couche.coucheFormat,
                                    coucheCrossOrigin = couche.coucheCrossOrigin,
                                    couchePublic = couche.couchePublic,
                                    coucheActive = couche.coucheActive,
                                    coucheProxy = couche.coucheProxy ?: false,
                                    coucheIconeUrl = couche.coucheIcone?.let {
                                        UriBuilder.fromPath(AuthnConstants.API_PATH)
                                            .path(LayersEndpoint::class.java)
                                            .path(LayersEndpoint::getIcone.javaMethod)
                                            .build(couche.coucheId)
                                            .toString()
                                    },
                                    coucheLegendeUrl = couche.coucheLegende?.let {
                                        UriBuilder.fromPath(AuthnConstants.API_PATH)
                                            .path(LayersEndpoint::class.java)
                                            .path(LayersEndpoint::getLegende.javaMethod)
                                            .build(couche.coucheId)
                                            .toString()
                                    },
                                    groupeFonctionnalitesList = coucheRepository.getGroupeFonctionnalitesList(couche.coucheId)
                                        .map { groupeFonctionnalites -> groupeFonctionnalites.groupeFonctionnalitesId },
                                    moduleList = coucheRepository.getModuleList(couche.coucheId),
                                    coucheProtected = couche.coucheProtected,
                                    coucheTuilage = couche.coucheTuilage,
                                )
                            },
                            groupeCoucheProtected = groupeCouche.groupeCoucheProtected,
                        )
                    }
            },
        ).build()

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
}
