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
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.core.UriBuilder
import remocra.auth.AuthnConstants
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.CoucheData
import remocra.data.CoucheFormData
import remocra.data.CoucheImageData
import remocra.data.CoucheStyleInput
import remocra.data.DataTableau
import remocra.data.GroupeCoucheData
import remocra.data.GroupeFonctionnalite
import remocra.data.Params
import remocra.data.SimplifiedCoucheData
import remocra.data.StyleGroupeCoucheData
import remocra.db.CoucheRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.couches.DeleteCoucheStyleUseCase
import remocra.usecase.admin.couches.GetCoucheStyleUseCase
import remocra.usecase.admin.couches.StyleCoucheUseCase
import remocra.usecase.admin.couches.UpdateCoucheStyleUseCase
import remocra.usecase.admin.couches.UpsertCoucheUseCase
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

    @Inject lateinit var updateCouche: UpsertCoucheUseCase

    @Inject lateinit var objectMapper: ObjectMapper

    @Inject lateinit var styleCoucheUseCase: StyleCoucheUseCase

    @Inject lateinit var getCoucheStyleUseCase: GetCoucheStyleUseCase

    @Inject lateinit var updateStyleCoucheUseCase: UpdateCoucheStyleUseCase

    @Inject lateinit var deleteStyleCoucheUseCase: DeleteCoucheStyleUseCase

    @Path("/get-all-styles")
    @GET
    @Public("Les styles peuvent être accessiblent ")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllStyles(): Response = Response.ok(getCoucheStyleUseCase.getAllStyles(securityContext.userInfo)).build()

    // créer un style après le formulaire
    @POST
    @Path("/add-style")
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun addCoucheStyle(coucheStyleInput: CoucheStyleInput): Response =
        styleCoucheUseCase.execute(securityContext.userInfo, coucheStyleInput).wrap()

    @Path("/get-couches-params")
    @POST
    @RequireDroits([Droit.CARTO_METADATA_A])
    fun getCouchesParams(params: Params<CoucheRepository.FilterLayerStyle, CoucheRepository.SortLayer>): Response =
        Response.ok(
            DataTableau(
                getCoucheStyleUseCase.getCouchesParams(params),
                getCoucheStyleUseCase.getCountStyles(params.filterBy),
            ),
        ).build()

    // permet de récupérer un style pour la modification de celui-ci
    @GET
    @Path("/get-style/{styleId}")
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun getStyleById(
        @PathParam("styleId")
        styleId: UUID,
    ): Response {
        return Response.ok(getCoucheStyleUseCase.getStyleById(styleId)).build()
    }

    @Path("/get-available-layers")
    @GET
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun getAvailableLayers(
        @QueryParam("coucheStyleId") coucheStyleId: UUID?,
    ): Response =
        Response.ok(
            object {
                val list = coucheRepository.getGroupeCoucheList().map { groupeCouche ->
                    StyleGroupeCoucheData(
                        groupeCoucheId = groupeCouche.groupeCoucheId,
                        groupeCoucheLibelle = groupeCouche.groupeCoucheLibelle,
                        groupeCoucheCode = groupeCouche.groupeCoucheCode,
                        coucheList = coucheRepository.getAvailableLayers(groupeCouche.groupeCoucheId).map { couche ->
                            SimplifiedCoucheData(
                                coucheId = couche.coucheId,
                                coucheLibelle = couche.coucheLibelle,
                                coucheCode = couche.coucheCode,
                                coucheNom = couche.coucheNom,
                                groupeFonctionnaliteList = coucheRepository.getAvailableGroupeFonctionnaliteList(couche.coucheId, coucheStyleId)
                                    .map { groupeFonctionnalite ->
                                        GroupeFonctionnalite(
                                            groupeFonctionnaliteId = groupeFonctionnalite.groupeFonctionnalitesId,
                                            groupeFonctionnaliteCode = groupeFonctionnalite.groupeFonctionnalitesCode,
                                            groupeFonctionnaliteLibelle = groupeFonctionnalite.groupeFonctionnalitesLibelle,
                                        )
                                    },
                            )
                        },
                    )
                }
            },
        ).build()

    @POST
    @Path("/{styleId}/update")
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun updateStyle(
        coucheStyleInput: CoucheStyleInput,
    ): Response {
        return updateStyleCoucheUseCase.execute(
            userInfo = securityContext.userInfo,
            element = coucheStyleInput,
        ).wrap()
    }

    @Path("/delete/{styleId}")
    @DELETE
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteStyle(
        @PathParam("styleId")
        styleId: UUID,
    ): Response =
        deleteStyleCoucheUseCase.execute(
            securityContext.userInfo,
            styleId,
        ).wrap()

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
