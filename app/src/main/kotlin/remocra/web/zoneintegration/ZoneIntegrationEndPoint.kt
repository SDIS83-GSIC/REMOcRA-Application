package remocra.web.zoneintegration

import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
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
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.CoordonneeInput
import remocra.data.DataTableau
import remocra.data.ImportGeometriesCodeLibelleData
import remocra.data.Params
import remocra.data.ZoneIntegrationData
import remocra.db.ZoneIntegrationRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.security.NoCsrf
import remocra.usecase.zoneintegration.CheckZoneIntegration
import remocra.usecase.zoneintegration.ImportZonesIntegrationUseCase
import remocra.usecase.zoneintegration.UpdateZoneIntegrationUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/zone-integration")
@Produces(MediaType.APPLICATION_JSON)
class ZoneIntegrationEndPoint : AbstractEndpoint() {
    @Inject
    lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    @Inject
    lateinit var checkZoneIntegration: CheckZoneIntegration

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var importZonesIntegrationUseCase: ImportZonesIntegrationUseCase

    @Inject
    lateinit var updateZoneIntegrationUseCase: UpdateZoneIntegrationUseCase

    @GET
    @Path("/get-active")
    @NoCsrf("")
    @Public("Les zones d'intégration ne sont pas liées à un droit")
    fun getActive(): Response {
        return Response.ok(zoneIntegrationRepository.getAll()).build()
    }

    /**
     * Vérifie si la géométrie est contenue dans la zone de l'utilisateur
     */
    @POST
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    @Public("La vérification doit être accessible pour n'importe quel utilisateur")
    fun check(input: CoordonneeInput): Response =
        checkZoneIntegration.checkZoneIntegration(securityContext.userInfo, input).wrap()

    @PUT
    @Path("/import/")
    // TODO droit ?
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun importData(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        return Response.ok(
            importZonesIntegrationUseCase.execute(
                securityContext.userInfo,
                ImportGeometriesCodeLibelleData(
                    httpRequest.getPart("fileGeometries").inputStream,
                ),
            ),
        ).build()
    }

    @GET
    @Path("/{zoneIntegrationId}")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    fun getById(@PathParam("zoneIntegrationId") zoneIntegrationId: UUID): Response =
        Response.ok(zoneIntegrationRepository.getById(zoneIntegrationId)).build()

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    fun getAll(params: Params<ZoneIntegrationRepository.Filter, ZoneIntegrationRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = zoneIntegrationRepository.getAllForAdmin(params),
                count = zoneIntegrationRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()

    @PUT
    @Path("/update/{zoneIntegrationId}")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    @Produces(MediaType.APPLICATION_JSON)
    fun update(@PathParam("zoneIntegrationId") zoneIntegrationId: UUID, zoneIntegrationInput: ZoneIntegrationInput): Response =
        updateZoneIntegrationUseCase.execute(
            securityContext.userInfo,
            ZoneIntegrationData(
                zoneIntegrationId = zoneIntegrationId,
                zoneIntegrationCode = zoneIntegrationInput.zoneIntegrationCode,
                zoneIntegrationActif = zoneIntegrationInput.zoneIntegrationActif,
                zoneIntegrationLibelle = zoneIntegrationInput.zoneIntegrationLibelle,
            ),
        ).wrap()

    class ZoneIntegrationInput {
        @FormParam("zoneIntegrationCode")
        lateinit var zoneIntegrationCode: String

        @FormParam("zoneIntegrationLibelle")
        lateinit var zoneIntegrationLibelle: String

        @FormParam("zoneIntegrationActif")
        var zoneIntegrationActif: Boolean = true
    }
}
