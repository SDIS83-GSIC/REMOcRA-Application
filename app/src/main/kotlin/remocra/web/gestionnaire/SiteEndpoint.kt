package remocra.web.gestionnaire

import com.google.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.FormParam
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
import remocra.data.DataTableau
import remocra.data.ImportSitesData
import remocra.data.Params
import remocra.data.SiteData
import remocra.db.SiteRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.gestionnaire.DeleteSiteUseCase
import remocra.usecase.gestionnaire.UpdateSiteUseCase
import remocra.usecase.site.ImportSitesUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/site")
@Produces(MediaType.APPLICATION_JSON)
class SiteEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var siteRepository: SiteRepository

    @Inject
    lateinit var updateSiteUseCase: UpdateSiteUseCase

    @Inject
    lateinit var deleteSiteUseCase: DeleteSiteUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var importSitesUseCase: ImportSitesUseCase

    @POST
    @Path("/")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getAll(params: Params<SiteRepository.Filter, SiteRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = siteRepository.getAllForAdmin(params),
                count = siteRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()

    @GET
    @Path("/{siteId}")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getById(@PathParam("siteId") siteId: UUID): Response =
        Response.ok(siteRepository.getById(siteId)).build()

    @PUT
    @Path("/update/{siteId}")
    @RequireDroits([Droit.GEST_SITE_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun update(@PathParam("siteId") siteId: UUID, siteInput: SiteInput): Response =
        updateSiteUseCase.execute(
            securityContext.userInfo,
            SiteData(
                siteId = siteId,
                siteGestionnaireId = siteInput.siteGestionnaireId,
                siteCode = siteInput.siteCode,
                siteActif = siteInput.siteActif,
                siteLibelle = siteInput.siteLibelle,
            ),
        ).wrap()

    class SiteInput {
        @FormParam("siteGestionnaireId")
        var siteGestionnaireId: UUID? = null

        @FormParam("siteCode")
        lateinit var siteCode: String

        @FormParam("siteLibelle")
        lateinit var siteLibelle: String

        @FormParam("siteActif")
        var siteActif: Boolean = true
    }

    @DELETE
    @Path("/delete/{siteId}")
    @RequireDroits([Droit.GEST_SITE_A])
    fun delete(@PathParam("siteId") siteId: UUID): Response {
        val site = siteRepository.getById(siteId)
        return deleteSiteUseCase.execute(
            securityContext.userInfo,
            SiteData(
                siteId = siteId,
                siteCode = site.siteCode,
                siteActif = site.siteActif,
                siteLibelle = site.siteLibelle,
                siteGestionnaireId = site.siteGestionnaireId,
            ),
        ).wrap()
    }

    @GET
    @Path("gestionnaire/{gestionnaireId}")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getAllSiteByGestionnaire(
        @PathParam("gestionnaireId")
        gestionnaireId: UUID,
    ): Response {
        return Response.ok(siteRepository.getAllSiteByGestionnaire(gestionnaireId)).build()
    }

    @PUT
    @Path("/import/")
    @RequireDroits([Droit.GEST_SITE_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun importData(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        return Response.ok(
            importSitesUseCase.execute(
                securityContext.userInfo,
                ImportSitesData(
                    httpRequest.getPart("fileSites").inputStream,
                ),
            ),
        ).build()
    }
}
