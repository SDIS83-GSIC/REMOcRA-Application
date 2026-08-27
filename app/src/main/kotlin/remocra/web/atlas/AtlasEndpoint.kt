package remocra.web.atlas

import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.db.AtlasRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.AtlasAnnexe
import remocra.security.NoCsrf
import remocra.usecase.atlas.DownloadAtlasUseCase
import remocra.usecase.atlas.DownloadTemplateZipAtlasUseCase
import remocra.usecase.atlas.ImportAtlasZipUseCase
import remocra.usecase.atlas.UpdateAnnexesAtlasPagination
import remocra.utils.DateUtils
import remocra.web.AbstractEndpoint

@Path("/atlas")
@Produces(MediaType.APPLICATION_JSON)
class AtlasEndpoint : AbstractEndpoint() {

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var atlasRepository: AtlasRepository

    @Inject lateinit var dateUtils: DateUtils

    @Inject lateinit var importAtlasZipUseCase: ImportAtlasZipUseCase

    @Inject lateinit var downloadTemplateZipAtlasUseCase: DownloadTemplateZipAtlasUseCase

    @Inject lateinit var updateAnnexesAtlasPagination: UpdateAnnexesAtlasPagination

    @Inject lateinit var downloadAtlasUseCase: DownloadAtlasUseCase

    @GET
    @Path("/has-element")
    @RequireDroits([Droit.ATLAS_A, Droit.DFCI_EXPORTATLAS_C, Droit.ATLAS_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun hasElement(): Response {
        return Response.ok(
            atlasRepository.hasDocumentsOrAnnexes(),
        ).build()
    }

    @DELETE
    @Path("/delete")
    @RequireDroits([Droit.ATLAS_A, Droit.ATLAS_D])
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteAtlas(): Response {
        return Response.ok(
            atlasRepository.deleteAll(),
        ).build()
    }

    @POST
    @Path("/import-atlas-zip")
    @RequireDroits([Droit.ATLAS_C, Droit.ATLAS_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun importAtlasZip(
        @Context httpRequest: HttpServletRequest,
    ): Response =
        Response.ok().entity(
            importAtlasZipUseCase.importerAtlas(
                httpRequest.getPart("importAtlasZip").inputStream,
            ),
        ).build()

    @POST
    @Path("/download-atlas-zip-template")
    @RequireDroits([Droit.ATLAS_A])
    @Produces(MediaType.TEXT_PLAIN)
    @NoCsrf("On télécharge un fichier")
    fun downloadZipAtlas(): Response =
        Response.ok(
            downloadTemplateZipAtlasUseCase.execute(),
        )
            .header("Content-Disposition", "attachment; filename=\"atlasZipTemplate-${dateUtils.now()}.zip\"")
            .build()

    @GET
    @Path("/atlas-documents-annexes")
    @RequireDroits([Droit.ATLAS_A, Droit.ATLAS_C])
    fun getAnnexes(): Response {
        return Response.ok(
            atlasRepository.getAtlasAnnexes(),
        ).build()
    }

    @POST
    @Path("/update-pagination-atlas-annexes")
    @RequireDroits([Droit.ATLAS_A, Droit.ATLAS_C])
    @Consumes(MediaType.APPLICATION_JSON)
    fun updatePaginationAtlas(
        downloadRequest: List<AtlasAnnexe>,
    ): Response =
        Response.ok().entity(
            updateAnnexesAtlasPagination.execute(securityContext.userInfo, downloadRequest),
        ).build()

    @POST
    @Path("/download-atlas")
    @RequireDroits([Droit.DFCI_EXPORTATLAS_C])
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    fun downloadAtlas(): Response =
        Response.ok(
            downloadAtlasUseCase.execute(userInfo = securityContext.userInfo),
        ).header("Content-Disposition", "attachment; filename=\"atlas.zip\"").build()
}
