package remocra.web.module

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.core.UriInfo
import remocra.GlobalConstants
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.ListModuleWithImage
import remocra.data.ModuleAccueilData
import remocra.data.Params
import remocra.db.CourrierRepository
import remocra.db.ModuleRepository
import remocra.db.ThematiqueRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.security.NoCsrf
import remocra.usecase.courrier.CourrierUsecase
import remocra.usecase.module.ModuleAccueilUpsertUseCase
import remocra.usecase.module.ModuleDocumentCourrierUseCase
import remocra.usecase.module.ModuleUseCase
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.io.File
import java.util.UUID
import kotlin.reflect.jvm.javaMethod

@Path("/modules")
@Produces(MediaType.APPLICATION_JSON)
class ModuleEndPoint : AbstractEndpoint() {

    @Inject lateinit var moduleUseCase: ModuleUseCase

    @Inject lateinit var moduleAccueilUpsertUseCase: ModuleAccueilUpsertUseCase

    @Inject lateinit var moduleRepository: ModuleRepository

    @Inject lateinit var objectMapper: ObjectMapper

    @Inject lateinit var moduleDocumentCourrierUseCase: ModuleDocumentCourrierUseCase

    @Context lateinit var uriInfo: UriInfo

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var courrierUsecase: CourrierUsecase

    @GET
    @Path("/get-all-type-module")
    @Public("Les types de module ne sont pas liées à un droit.")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllTypeModule() =
        Response.ok(TypeModule.entries.sortedBy { it.name }).build()

    @GET
    @Path("/")
    @Public("Les modules de la pages d'accueil sont accessibles à tous, la page d'accueil affichera les modules en fonction des droits")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAll(): Response =
        Response.ok(
            moduleUseCase.execute(
                uriInfo.baseUriBuilder
                    .path(ModuleEndPoint::class.java)
                    .path(ModuleEndPoint::getUriImage.javaMethod),
                securityContext.userInfo,
            ),
        ).build()

    @GET
    @Path("/get-image")
    @Produces("image/png", "image/jpeg", "image/jpg")
    @NoCsrf("On utilise une URL directe et donc on n'a pas les entêtes remplis, ce qui fait qu'on est obligé d'utiliser cette annotation")
    @Public("Pas de droit particulier pour charger une image")
    fun getUriImage(@QueryParam("moduleId") moduleId: UUID): Response {
        val module = moduleRepository.getById(moduleId)
        return Response.ok(
            module.moduleImage?.let { File(GlobalConstants.DOSSIER_IMAGE_MODULE + it) },
        )
            .build()
    }

    @PUT
    @Path("/upsert")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    @Produces(MediaType.APPLICATION_JSON)
    fun upsert(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val liste = ListModuleWithImage(
            objectMapper.readValue<Collection<ModuleAccueilData>>(httpRequest.getTextPart("listeModule")),
            httpRequest.parts.filter { it.name.contains("image_") },
        )
        return moduleAccueilUpsertUseCase.execute(
            securityContext.userInfo,
            liste,
        ).wrap()
    }

    @POST
    @Path("/document/all")
    @Public("Les documents ne sont pas liés à un droit")
    fun getDocumentsForListWithThematique(
        @QueryParam("moduleId")
        moduleId: UUID,
        @QueryParam("moduleType")
        moduleType: String,
        params: Params<ThematiqueRepository.Filter, ThematiqueRepository.Sort>,
    ): Response =
        Response.ok(
            DataTableau(
                list = moduleDocumentCourrierUseCase.execute(
                    moduleId,
                    moduleType,
                    securityContext.userInfo,
                    params,
                ),
                count = moduleDocumentCourrierUseCase.count(
                    moduleId,
                    securityContext.userInfo,
                    params,
                ),
            ),
        ).build()

    @POST
    @Path("/courriers/all")
    @RequireDroits([Droit.COURRIER_ADMIN_R, Droit.COURRIER_ORGANISME_R, Droit.COURRIER_UTILISATEUR_R])
    fun getCourriersForListWithThematique(
        @QueryParam("moduleId")
        moduleId: UUID,
        params: Params<CourrierRepository.Filter, CourrierRepository.Sort>,
    ): Response =
        Response.ok(
            DataTableau(
                list = courrierUsecase.getCourrierCompletWithThematique(moduleId, securityContext.userInfo, params),
                count = courrierUsecase.countCourrierCompletWithThematique(moduleId, securityContext.userInfo, params),
            ),
        ).build()

    @GET
    @Path("/get-type-module-accueil")
    @Public("Les types de module ne sont pas liées à un droit.")
    @Produces(MediaType.APPLICATION_JSON)
    fun getTypeModuleAccueil() =
        Response.ok(TypeModule.entries.sortedBy { it.name }).build()
}
