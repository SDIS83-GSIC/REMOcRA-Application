package remocra.web.admin

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.CoucheMetadata
import remocra.data.DataTableau
import remocra.data.GroupeFonctionnalite
import remocra.data.Params
import remocra.data.SimplifiedCoucheData
import remocra.data.StyleGroupeCoucheData
import remocra.db.CoucheMetadataRepository
import remocra.db.CoucheRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.couches.CreateCoucheMetadataUseCase
import remocra.usecase.admin.couches.DeleteCoucheMetadataUseCase
import remocra.usecase.admin.couches.GetCoucheMetadataUseCase
import remocra.usecase.admin.couches.UpdateCoucheMetadataUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Produces("application/json; charset=UTF-8")
@Path("/admin/couche-metadata")
class CoucheMetadataEndpoint : AbstractEndpoint() {
    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var objectMapper: ObjectMapper

    @Inject lateinit var createCoucheMetadataUseCase: CreateCoucheMetadataUseCase

    @Inject lateinit var getCoucheMetadataUseCase: GetCoucheMetadataUseCase

    @Inject lateinit var updateCoucheMetadataUseCase: UpdateCoucheMetadataUseCase

    @Inject lateinit var deleteCoucheMetadataUseCase: DeleteCoucheMetadataUseCase

    @Inject lateinit var coucheMetadataRepository: CoucheMetadataRepository

    @Path("/get-all-metadata")
    @GET
    @Public("Les metadata sont publiques")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllCoucheMetadata(): Response = Response.ok(getCoucheMetadataUseCase.getAllCoucheMetadata(securityContext.userInfo)).build()

    @POST
    @Path("/add-couche-metadata")
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun addCoucheCoucheMetadata(coucheMetadataInput: CoucheMetadata): Response =
        createCoucheMetadataUseCase.execute(securityContext.userInfo, coucheMetadataInput).wrap()

    @Path("/get-couches-metadata-table")
    @POST
    @RequireDroits([Droit.CARTO_METADATA_A])
    fun getCouchesParams(params: Params<CoucheMetadataRepository.FilterCoucheMetadata, CoucheMetadataRepository.SortCouche>): Response =
        Response.ok(
            DataTableau(
                getCoucheMetadataUseCase.getCouchesMetadataForTableau(params),
                coucheMetadataRepository.getCountCoucheMetadata(params.filterBy),
            ),
        ).build()

    @GET
    @Path("/get-couche-metadata/{coucheMetadataId}")
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun getCoucheMetadataById(
        @PathParam("coucheMetadataId")
        coucheMetadataId: UUID,
    ): Response {
        return Response.ok(coucheMetadataRepository.getCoucheMetadataByIdWithLibelle(coucheMetadataId)).build()
    }

    @POST
    @Path("/{coucheMetadataId}/update")
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun updateCoucheMetadata(
        coucheMetadataInput: CoucheMetadata,
    ): Response {
        return updateCoucheMetadataUseCase.execute(
            userInfo = securityContext.userInfo,
            element = coucheMetadataInput,
        ).wrap()
    }

    @Path("/delete/{coucheMetadataId}")
    @DELETE
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteCoucheMetadata(
        @PathParam("coucheMetadataId")
        coucheMetadataId: UUID,
    ): Response =
        deleteCoucheMetadataUseCase.execute(
            securityContext.userInfo,
            coucheMetadataRepository.getCoucheMetadataById(coucheMetadataId),
        ).wrap()

    @Path("/get-available-layers")
    @GET
    @RequireDroits([Droit.CARTO_METADATA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun getAvailableLayers(
        @QueryParam("coucheMetadataId") coucheMetadataId: UUID?,
    ): Response =
        Response.ok(
            coucheRepository.getGroupeCoucheList().map { groupeCouche ->
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
                            groupeFonctionnaliteList = coucheMetadataRepository.getAvailableGroupeFonctionnaliteList(couche.coucheId, coucheMetadataId)
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
            },
        ).build()
}
