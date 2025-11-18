package remocra.web.rapportpersonnalise

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
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.GenererRapportPersonnaliseData
import remocra.data.Params
import remocra.data.RapportPersonnaliseData
import remocra.data.RapportPersonnaliseParametreData
import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.security.NoCsrf
import remocra.usecase.rapportpersonnalise.BuildFormRapportPersonnaliseUseCase
import remocra.usecase.rapportpersonnalise.CreateRapportPersonnaliseUseCase
import remocra.usecase.rapportpersonnalise.DeleteRapportPersonnaliseUseCase
import remocra.usecase.rapportpersonnalise.ExportConfRapportPersonnaliseUseCase
import remocra.usecase.rapportpersonnalise.ExportDataCarteRapportPersonnaliseUseCase
import remocra.usecase.rapportpersonnalise.ExportDataRapportPersonnaliseUseCase
import remocra.usecase.rapportpersonnalise.GenereRapportPersonnaliseUseCase
import remocra.usecase.rapportpersonnalise.ImportConfRapportPersonnaliseUseCase
import remocra.usecase.rapportpersonnalise.UpdateRapportPersonnaliseUseCase
import remocra.utils.DateUtils
import remocra.web.AbstractEndpoint
import java.nio.charset.StandardCharsets
import java.util.UUID

@Path("/rapport-personnalise")
class RapportPersonnaliseEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    lateinit var createRapportPersonnaliseUseCase: CreateRapportPersonnaliseUseCase

    @Inject
    lateinit var updateRapportPersonnaliseUseCase: UpdateRapportPersonnaliseUseCase

    @Inject
    lateinit var deleteRapportPersonnaliseUseCase: DeleteRapportPersonnaliseUseCase

    @Inject
    lateinit var buildFormRapportPersonnaliseUseCase: BuildFormRapportPersonnaliseUseCase

    @Inject
    lateinit var genereRapportPersonnaliseUseCase: GenereRapportPersonnaliseUseCase

    @Inject
    lateinit var exportDataRapportPersonnaliseUseCase: ExportDataRapportPersonnaliseUseCase

    @Inject
    lateinit var dateUtils: DateUtils

    @Inject
    lateinit var exportDataCarteRapportPersonnaliseUseCase: ExportDataCarteRapportPersonnaliseUseCase

    @Inject
    lateinit var exportConfRapportPersonnaliseUseCase: ExportConfRapportPersonnaliseUseCase

    @Inject
    lateinit var importConfRapportPersonnaliseUseCase: ImportConfRapportPersonnaliseUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    @Produces(MediaType.APPLICATION_JSON)
    fun getAll(params: Params<RapportPersonnaliseRepository.Filter, RapportPersonnaliseRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = rapportPersonnaliseRepository.getAllForAdmin(params),
                count = rapportPersonnaliseRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()

    @Path("/create")
    @POST
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    @Produces(MediaType.APPLICATION_JSON)
    fun create(element: RapportPersonnaliseInput): Response =
        createRapportPersonnaliseUseCase.execute(
            securityContext.userInfo,
            RapportPersonnaliseData(
                rapportPersonnaliseId = UUID.randomUUID(),
                rapportPersonnaliseActif = element.rapportPersonnaliseActif,
                rapportPersonnaliseCode = element.rapportPersonnaliseCode,
                rapportPersonnaliseLibelle = element.rapportPersonnaliseLibelle,
                rapportPersonnaliseChampGeometrie = element.rapportPersonnaliseChampGeometrie,
                rapportPersonnaliseDescription = element.rapportPersonnaliseDescription,
                rapportPersonnaliseSourceSql = element.rapportPersonnaliseSourceSql,
                rapportPersonnaliseModule = element.rapportPersonnaliseModule,
                rapportPersonnaliseProtected = false,
                listeGroupeFonctionnalitesId = element.listeGroupeFonctionnalitesId,
                listeRapportPersonnaliseParametre = element.listeRapportPersonnaliseParametre,
            ),
        ).wrap()

    @Path("/update/{rapportPersonnaliseId}")
    @PUT
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    @Produces(MediaType.APPLICATION_JSON)
    fun update(
        @PathParam("rapportPersonnaliseId")
        rapportPersonnaliseId: UUID,

        element: RapportPersonnaliseInput,
    ): Response =
        updateRapportPersonnaliseUseCase.execute(
            securityContext.userInfo,
            RapportPersonnaliseData(
                rapportPersonnaliseId = rapportPersonnaliseId,
                rapportPersonnaliseActif = element.rapportPersonnaliseActif,
                rapportPersonnaliseCode = element.rapportPersonnaliseCode,
                rapportPersonnaliseLibelle = element.rapportPersonnaliseLibelle,
                rapportPersonnaliseChampGeometrie = element.rapportPersonnaliseChampGeometrie,
                rapportPersonnaliseDescription = element.rapportPersonnaliseDescription,
                rapportPersonnaliseSourceSql = element.rapportPersonnaliseSourceSql,
                rapportPersonnaliseModule = element.rapportPersonnaliseModule,
                rapportPersonnaliseProtected = element.rapportPersonnaliseProtected,
                listeGroupeFonctionnalitesId = element.listeGroupeFonctionnalitesId,
                listeRapportPersonnaliseParametre = element.listeRapportPersonnaliseParametre,
            ),
        ).wrap()

    @Path("/delete/{rapportPersonnaliseId}")
    @DELETE
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    @Produces(MediaType.APPLICATION_JSON)
    fun delete(
        @PathParam("rapportPersonnaliseId")
        rapportPersonnaliseId: UUID,
    ): Response =
        deleteRapportPersonnaliseUseCase.execute(
            securityContext.userInfo,
            rapportPersonnaliseRepository.getRapportPersonnalise(rapportPersonnaliseId),
        ).wrap()

    class RapportPersonnaliseInput {
        val rapportPersonnaliseActif: Boolean = false
        lateinit var rapportPersonnaliseCode: String
        lateinit var rapportPersonnaliseLibelle: String
        val rapportPersonnaliseChampGeometrie: String? = null
        val rapportPersonnaliseDescription: String? = null
        lateinit var rapportPersonnaliseSourceSql: String
        lateinit var rapportPersonnaliseModule: TypeModuleRapportCourrier
        val rapportPersonnaliseProtected: Boolean = false
        var listeGroupeFonctionnalitesId: Collection<UUID> = listOf()
        val listeRapportPersonnaliseParametre: Collection<RapportPersonnaliseParametreData> = listOf()
    }

    @GET
    @Path("/get/{rapportPersonnaliseId}")
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    @Produces(MediaType.APPLICATION_JSON)
    fun getRapportPersonnalise(
        @PathParam("rapportPersonnaliseId")
        rapportPersonnaliseId: UUID,
    ) =
        Response.ok(rapportPersonnaliseRepository.getRapportPersonnalise(rapportPersonnaliseId)).build()

    @GET
    @Path("/parametres/{rapportPersonnaliseId}")
    @RequireDroits([Droit.RAPPORT_PERSONNALISE_E])
    @Produces(MediaType.APPLICATION_JSON)
    fun getRapportPersonnaliseWithParametre(
        @PathParam("rapportPersonnaliseId") rapportPersonnaliseId: UUID,
    ) =
        Response.ok(buildFormRapportPersonnaliseUseCase.execute(securityContext.userInfo, rapportPersonnaliseId)).build()

    @GET
    @Path("/list")
    @RequireDroits([Droit.RAPPORT_PERSONNALISE_E])
    @Produces(MediaType.APPLICATION_JSON)
    fun getRapportPersonnalise() =
        Response.ok(rapportPersonnaliseRepository.getListeRapportPersonnalise(securityContext.userInfo.utilisateurId!!, securityContext.userInfo.isSuperAdmin)).build()

    @PUT
    @Path("/generer")
    @RequireDroits([Droit.RAPPORT_PERSONNALISE_E])
    @Produces(MediaType.APPLICATION_JSON)
    fun genererRapportPersonnalise(element: GenererRapportPersonnaliseData) =
        Response.ok(genereRapportPersonnaliseUseCase.execute(securityContext.userInfo, element)).build()

    @POST
    @Path("/export-data")
    @RequireDroits([Droit.RAPPORT_PERSONNALISE_E])
    @Produces(MediaType.TEXT_PLAIN + "; charset=ISO-8859-1")
    fun exportData(
        element: GenererRapportPersonnaliseData,
    ): Response =
        Response.ok(exportDataRapportPersonnaliseUseCase.execute(securityContext.userInfo, element).toString(StandardCharsets.ISO_8859_1))
            .header("Content-Disposition", "attachment; filename=\"rapport-personnalise-${dateUtils.now()}.csv\"")
            .build()

    @POST
    @Path("/export-shp")
    @RequireDroits([Droit.RAPPORT_PERSONNALISE_E])
    @Produces(MediaType.TEXT_PLAIN)
    fun exportShp(
        element: GenererRapportPersonnaliseData,
    ): Response =
        Response.ok(exportDataCarteRapportPersonnaliseUseCase.execute(element))
            .header("Content-Disposition", "attachment; filename=\"rapport-personnalise-${dateUtils.now()}.zip")
            .build()

    @GET
    @Path("/export/{rapportPersonnaliseId}")
    @NoCsrf("Téléchargement d'un fichier")
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    @Produces(MediaType.APPLICATION_JSON)
    fun exportRapportPersonnalise(
        @PathParam("rapportPersonnaliseId")
        rapportPersonnaliseId: UUID,
    ): Response {
        return Response.ok(exportConfRapportPersonnaliseUseCase.execute(rapportPersonnaliseId))
            .header("Content-Disposition", "attachment; filename=\"rapport-personnalise.zip\"")
            .build()
    }

    @PUT
    @Path("/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    fun import(@Context httpRequest: HttpServletRequest): Response {
        importConfRapportPersonnaliseUseCase.execute(
            securityContext.userInfo,
            httpRequest.getPart("zipFile").inputStream,
        )
        return Response.ok().build()
    }
}
