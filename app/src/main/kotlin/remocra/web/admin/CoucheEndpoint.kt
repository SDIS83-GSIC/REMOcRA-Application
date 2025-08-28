package remocra.web.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
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
import remocra.data.CoucheImageData
import remocra.data.GroupeCoucheData
import remocra.db.CoucheRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.couches.UpsertCoucheUseCase
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import remocra.web.carto.LayersEndpoint
import kotlin.reflect.jvm.javaMethod

@Produces("application/json; charset=UTF-8")
@Path("/admin/couche")
class CoucheEndpoint : AbstractEndpoint() {
    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var updateCouche: UpsertCoucheUseCase

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
                                    profilDroitList = coucheRepository.getProfilDroitList(couche.coucheId)
                                        .map { profilDroit -> profilDroit.profilDroitId },
                                    moduleList = coucheRepository.getModuleList(couche.coucheId),
                                    coucheProtected = couche.coucheProtected ?: false,
                                )
                            },
                            groupeCoucheProtected = groupeCouche.groupeCoucheProtected ?: false,
                        )
                    }
            },
        ).build()

    @Path("/")
    @PUT
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun put(
        @Context httpRequest: HttpServletRequest,
    ): Response =
        updateCouche.execute(
            securityContext.userInfo,
            CoucheFormData(
                data = objectMapper.readValue<List<GroupeCoucheData>>(httpRequest.getTextPart("data")),
                iconeList = httpRequest.parts.filter { it.name.startsWith("icone_") }.map {
                    val code = it.name.substringAfter("icone_")
                    CoucheImageData(
                        code = code,
                        data = it,
                    )
                },
                legendeList = httpRequest.parts.filter { it.name.startsWith("legende_") }.map {
                    val code = it.name.substringAfter("legende_")
                    CoucheImageData(
                        code = code,
                        data = it,
                    )
                },
            ),
        ).wrap()
}
