package remocra.web.courrier

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
import jakarta.ws.rs.core.UriInfo
import org.slf4j.LoggerFactory
import remocra.GlobalConstants
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.ModeleCourrierData
import remocra.data.Params
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.db.CourrierRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.security.NoCsrf
import remocra.usecase.courrier.BuildFormCourrierUseCase
import remocra.usecase.courrier.CourrierGeneratorUseCase
import remocra.usecase.courrier.CreateCourrierUseCase
import remocra.usecase.document.DocumentUtils
import remocra.usecase.modelecourrier.CreateModeleCourrierUseCase
import remocra.usecase.modelecourrier.DeleteModeleCourrierUseCase
import remocra.usecase.modelecourrier.UpdateModeleCourrierUseCase
import remocra.utils.getTextPart
import remocra.utils.notFound
import remocra.web.AbstractEndpoint
import java.nio.file.Paths
import java.util.UUID
import kotlin.reflect.jvm.javaMethod

@Path("/courriers")
@Produces(MediaType.APPLICATION_JSON)
class CourrierEndPoint : AbstractEndpoint() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Inject lateinit var courrierGeneratorUseCase: CourrierGeneratorUseCase

    @Inject lateinit var buildFormCourrierUseCase: BuildFormCourrierUseCase

    @Inject lateinit var courrierRepository: CourrierRepository

    @Inject lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject lateinit var createModeleCourrierUseCase: CreateModeleCourrierUseCase

    @Inject lateinit var updateModeleCourrierUseCase: UpdateModeleCourrierUseCase

    @Inject lateinit var deleteModeleCourrierUseCase: DeleteModeleCourrierUseCase

    @Inject lateinit var createCourrierUseCase: CreateCourrierUseCase

    @Inject lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Context lateinit var securityContext: SecurityContext

    @Context lateinit var uriInfo: UriInfo

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.COURRIER_C])
    fun genererCourrier(
        parametreCourrierInput: ParametreCourrierInput,
    ): Response {
        return Response.ok()
            .entity(
                courrierGeneratorUseCase.execute(
                    parametreCourrierInput,
                    securityContext.userInfo,
                    uriInfo.baseUriBuilder
                        .path(CourrierEndPoint::class.java)
                        .path(CourrierEndPoint::getUriCourrier.javaMethod),
                ),
            )
            .build()
    }

    @POST
    @Path("/destinataires")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.COURRIER_C])
    fun getAllDestinataires(
        params: Params<CourrierRepository.FilterDestinataire, CourrierRepository.SortDestinataire>,
    ): Response {
        return Response.ok()
            .entity(
                DataTableau(
                    list = courrierRepository.getAllDestinataires(params.filterBy, params.sortBy, params.limit, params.offset),
                    count = courrierRepository.countDestinataire(),
                ),
            )
            .build()
    }

    @POST
    @Path("/modeles")
    @RequireDroits([Droit.ADMIN_COURRIER])
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllModeleCourrier(params: Params<ModeleCourrierRepository.Filter, ModeleCourrierRepository.Sort>): Response {
        return Response.ok(
            DataTableau(
                list = modeleCourrierRepository.getAllForAdmin(params),
                count = modeleCourrierRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()
    }

    @POST
    @Path("modeles/create")
    @RequireDroits([Droit.ADMIN_COURRIER])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun create(@Context httpRequest: HttpServletRequest): Response {
        val modeleCourrier = objectMapper.readValue<ModeleCourrierData>(httpRequest.getTextPart("modeleCourrier"))
        val modeleCourrierId = modeleCourrier.modeleCourrierId ?: UUID.randomUUID()
        return createModeleCourrierUseCase.execute(
            securityContext.userInfo,
            modeleCourrier.copy(
                modeleCourrierId = modeleCourrierId,
                part = httpRequest.parts.find { it.name == "part" },
            ),
        ).wrap()
    }

    @PUT
    @Path("modeles/update/{modeleCourrierId}")
    @RequireDroits([Droit.ADMIN_COURRIER])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun update(
        @PathParam("modeleCourrierId")
        modeleCourrierId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val modeleCourrier = objectMapper.readValue<ModeleCourrierData>(httpRequest.getTextPart("modeleCourrier"))
        return updateModeleCourrierUseCase.execute(
            securityContext.userInfo,
            modeleCourrier.copy(
                part = httpRequest.parts.find { it.name == "part" },
            ),
        ).wrap()
    }

    @GET
    @Path("/modele-courrier/get/{modeleCourrierId}")
    @RequireDroits([Droit.ADMIN_COURRIER])
    @Produces(MediaType.APPLICATION_JSON)
    fun getModeleCourrier(
        @PathParam("modeleCourrierId")
        modeleCourrierId: UUID,
    ): Response {
        return Response.ok(modeleCourrierRepository.getModeleCourrier(modeleCourrierId)).build()
    }

    @DELETE
    @Path("/modele-courrier/delete/{modeleCourrierId}")
    @RequireDroits([Droit.ADMIN_COURRIER])
    @Produces(MediaType.APPLICATION_JSON)
    fun delete(
        @PathParam("modeleCourrierId")
        modeleCourrierId: UUID,
    ): Response {
        return deleteModeleCourrierUseCase.execute(
            securityContext.userInfo,
            modeleCourrierRepository.getModeleCourrier(modeleCourrierId),
        ).wrap()
    }

    @GET
    @Path("/modeles-courriers/list")
    @RequireDroits([Droit.COURRIER_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun getCourrierInfo(
        @QueryParam("typeModule")
        typeModule: TypeModule,
    ): Response {
        return Response.ok(
            modeleCourrierRepository.getListeModeleCourrier(
                securityContext.userInfo.utilisateurId!!,
                securityContext.userInfo.isSuperAdmin,
                typeModule,
            ),
        ).build()
    }

    @GET
    @Path("/parametres/{modeleCourrierId}")
    @RequireDroits([Droit.COURRIER_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun getParametreByCourrier(
        @PathParam("modeleCourrierId") modeleCourrierId: UUID,
    ): Response {
        return Response.ok(buildFormCourrierUseCase.execute(securityContext.userInfo, modeleCourrierId)).build()
    }

    @GET
    @Path("/get-courrier")
    @RequireDroits([Droit.COURRIER_C])
    @NoCsrf("Téléchargement d'un fichier")
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    fun getUriCourrier(@QueryParam("courrierPath") courrierPath: String?): Response {
        val path = courrierPath?.let {
            GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE.resolve(it)
        }?.takeIf {
            it.startsWith(GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE)
        }
        return path?.let { documentUtils.checkFile(it) } ?: notFound().build()
    }

    /**
     * Télécharge le courrier.
     * @param courrierId l'identifiant du courrier à télécharger
     * @return La réponse HTTP contenant le fichier à télécharger.
     */
    @GET
    @NoCsrf("On utilise une URL directe et donc on n'a pas les entêtes remplis, ce qui fait qu'on est obligé d'utiliser cette annotation")
    @Public("Le téléchargement des courriers n'est pas dépendant d'un droit particulier")
    @Path("/telecharger/{courrierId}")
    @Produces(MediaType.TEXT_PLAIN)
    fun telechargerCourrier(@PathParam("courrierId") courrierId: UUID): Response {
        val document = courrierRepository.getDocumentByCourrier(courrierId)

        return documentUtils.checkFile(
            Paths.get(document.documentRepertoire, document.documentNomFichier),
        )
    }
}
