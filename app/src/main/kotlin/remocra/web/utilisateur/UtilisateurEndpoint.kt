package remocra.web.utilisateur

import jakarta.inject.Inject
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
import jakarta.ws.rs.core.UriInfo
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.LigneImportUtilisateur
import remocra.data.Params
import remocra.data.UtilisateurData
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.utilisateur.CreateUtilisateurUseCase
import remocra.usecase.utilisateur.DeleteUtilisateurUseCase
import remocra.usecase.utilisateur.DownloadTemplateImportUserUseCase
import remocra.usecase.utilisateur.ImportUtilisateurUseCase
import remocra.usecase.utilisateur.UpdateUtilisateurUseCase
import remocra.utils.DateUtils
import remocra.web.AbstractEndpoint
import java.nio.charset.StandardCharsets
import java.util.UUID

@Path("/utilisateur")
@Produces(MediaType.APPLICATION_JSON)
class UtilisateurEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var utilisateurRepository: UtilisateurRepository

    @Inject
    lateinit var createUtilisateurUseCase: CreateUtilisateurUseCase

    @Inject
    lateinit var deleteUtilisateurUseCase: DeleteUtilisateurUseCase

    @Inject
    lateinit var updateUtilisateurUseCase: UpdateUtilisateurUseCase

    @Inject
    lateinit var importUtilisateurUseCase: ImportUtilisateurUseCase

    @Inject
    lateinit var downloadTemplateImportUserUseCase: DownloadTemplateImportUserUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @Context
    lateinit var uriInfo: UriInfo

    @Inject
    lateinit var dateUtils: DateUtils

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_R, Droit.ADMIN_UTILISATEURS_ORGA_R, Droit.ADMIN_UTILISATEURS_A])
    fun getAll(params: Params<UtilisateurRepository.Filter, UtilisateurRepository.Sort>): Response {
        val user = securityContext.userInfo
        return Response.ok(
            DataTableau(
                list = utilisateurRepository.getAllForAdmin(user, params),
                count = utilisateurRepository.countAllForAdmin(user, params.filterBy),
            ),
        ).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A, Droit.ADMIN_UTILISATEURS_ORGA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun create(utilisateurInput: UtilisateurInput): Response =
        createUtilisateurUseCase.execute(
            securityContext.userInfo,
            UtilisateurData(
                utilisateurId = UUID.randomUUID(),
                utilisateurActif = utilisateurInput.utilisateurActif,
                utilisateurEmail = utilisateurInput.utilisateurEmail,
                utilisateurNom = utilisateurInput.utilisateurNom,
                utilisateurPrenom = utilisateurInput.utilisateurPrenom,
                utilisateurUsername = utilisateurInput.utilisateurUsername,
                utilisateurTelephone = utilisateurInput.utilisateurTelephone,
                utilisateurCanBeNotified = utilisateurInput.utilisateurCanBeNotified,
                utilisateurProfilUtilisateurId = utilisateurInput.utilisateurProfilUtilisateurId,
                utilisateurOrganismeId = utilisateurInput.utilisateurOrganismeId,
                utilisateurIsSuperAdmin = utilisateurInput.utilisateurIsSuperAdmin,
            ),
        ).wrap()

    class UtilisateurInput {
        @FormParam("utilisateurEmail")
        lateinit var utilisateurEmail: String

        @FormParam("utilisateurNom")
        val utilisateurNom: String? = null

        @FormParam("utilisateurPrenom")
        val utilisateurPrenom: String? = null

        @FormParam("utilisateurUsername")
        lateinit var utilisateurUsername: String

        @FormParam("utilisateurTelephone")
        val utilisateurTelephone: String? = null

        @FormParam("utilisateurOrganismeId")
        var utilisateurOrganismeId: UUID? = null

        @FormParam("utilisateurProfilUtilisateurId")
        var utilisateurProfilUtilisateurId: UUID? = null

        @FormParam("utilisateurCanBeNotified")
        var utilisateurCanBeNotified: Boolean = true

        @FormParam("utilisateurActif")
        var utilisateurActif: Boolean = true

        @FormParam("utilisateurIsSuperAdmin")
        var utilisateurIsSuperAdmin: Boolean = false
    }

    @DELETE
    @Path("/delete/{utilisateurId}")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A, Droit.ADMIN_UTILISATEURS_ORGA_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun delete(
        @PathParam("utilisateurId")
        utilisateurId: UUID,
    ): Response =
        deleteUtilisateurUseCase.execute(
            securityContext.userInfo,
            utilisateurRepository.getById(utilisateurId),
        ).wrap()

    @GET
    @Path("/get/{utilisateurId}")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_R, Droit.ADMIN_UTILISATEURS_A, Droit.ADMIN_UTILISATEURS_ORGA_R, Droit.ADMIN_UTILISATEURS_ORGA_A])
    fun get(@PathParam("utilisateurId") utilisateurId: UUID): Response {
        return Response.ok(utilisateurRepository.getById(utilisateurId)).build()
    }

    @PUT
    @Path("/update/{utilisateurId}")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A, Droit.ADMIN_UTILISATEURS_ORGA_A])
    fun update(
        @PathParam("utilisateurId")
        utilisateurId: UUID,
        utilisateurInput: UtilisateurInput,
    ) =
        updateUtilisateurUseCase.execute(
            userInfo = securityContext.userInfo,
            element = UtilisateurData(
                utilisateurId = utilisateurId,
                utilisateurActif = utilisateurInput.utilisateurActif,
                utilisateurEmail = utilisateurInput.utilisateurEmail,
                utilisateurNom = utilisateurInput.utilisateurNom,
                utilisateurPrenom = utilisateurInput.utilisateurPrenom,
                utilisateurUsername = utilisateurInput.utilisateurUsername,
                utilisateurTelephone = utilisateurInput.utilisateurTelephone,
                utilisateurCanBeNotified = utilisateurInput.utilisateurCanBeNotified,
                utilisateurProfilUtilisateurId = utilisateurInput.utilisateurProfilUtilisateurId,
                utilisateurOrganismeId = utilisateurInput.utilisateurOrganismeId,
                utilisateurIsSuperAdmin = utilisateurInput.utilisateurIsSuperAdmin,
            ),
        ).wrap()

    @GET
    @RequireDroits([Droit.DASHBOARD_A])
    @Path("/list")
    fun getForList() = utilisateurRepository.getForList()

    @POST
    @Path("/import")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A, Droit.ADMIN_UTILISATEURS_ORGA_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun importUtilisateur(
        @Context httpRequest: HttpServletRequest,
    ): Response =
        Response.ok().entity(
            importUtilisateurUseCase.verifierDonnees(
                securityContext.userInfo,
                httpRequest.getPart("document").inputStream,
            ),
        ).build()

    @POST
    @Path("/importer-utilisateur")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A, Droit.ADMIN_UTILISATEURS_ORGA_A])
    @Consumes(MediaType.APPLICATION_JSON)
    fun importUtilisateurEnregistrement(importData: List<LigneImportUtilisateur>): Response =
        Response.ok(
            importUtilisateurUseCase.importUtilisateurEnregistrement(
                securityContext.userInfo,
                importData,
            ),
        ).build()

    @POST
    @Path("/download-user-template")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_A, Droit.ADMIN_UTILISATEURS_ORGA_A])
    @Produces(MediaType.TEXT_PLAIN + "; charset=ISO-8859-1")
    fun exportData(): Response =
        Response.ok(downloadTemplateImportUserUseCase.execute().toString(StandardCharsets.ISO_8859_1))
            .header("Content-Disposition", "attachment; filename=\"template-${dateUtils.now()}.csv\"")
            .build()
}
