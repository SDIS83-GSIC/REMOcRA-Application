package remocra.web.courrier

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
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
import jakarta.ws.rs.core.UriInfo
import org.slf4j.LoggerFactory
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.DocumentsData
import remocra.data.ModeleCourrierData
import remocra.data.Params
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.db.CourrierRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.security.NoCsrf
import remocra.usecase.courrier.CourrierGenerator
import remocra.usecase.courrier.GetCourriersWithParametresUseCase
import remocra.usecase.document.DocumentUtils
import remocra.usecase.modelecourrier.CreateModeleCourrierUseCase
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.io.File
import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.pathString
import kotlin.reflect.jvm.javaMethod

@Path("/courriers")
@Produces(MediaType.APPLICATION_JSON)
class CourrierEndPoint : AbstractEndpoint() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Inject lateinit var courrierGenerator: CourrierGenerator

    @Inject lateinit var getCourriersWithParametresUseCase: GetCourriersWithParametresUseCase

    @Inject lateinit var courrierRepository: CourrierRepository

    @Inject lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject lateinit var createModeleCourrierUseCase: CreateModeleCourrierUseCase

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
                courrierGenerator.execute(
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
    @Path("/modeles")
    @RequireDroits([Droit.ADMIN_DROITS])
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
    @RequireDroits([Droit.ADMIN_DROITS])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun create(@Context httpRequest: HttpServletRequest): Response {
        val modeleCourrier = objectMapper.readValue<ModeleCourrierData>(httpRequest.getTextPart("modeleCourrier"))
        val modeleCourrierId = modeleCourrier.modeleCourrierId ?: UUID.randomUUID()
        return createModeleCourrierUseCase.execute(
            securityContext.userInfo,
            modeleCourrier.copy(
                modeleCourrierId = modeleCourrierId,
                documents = DocumentsData.DocumentsModeleCourrier(
                    objectId = modeleCourrierId,
                    listDocument = objectMapper.readValue<List<DocumentsData.DocumentModeleCourrierData>>(httpRequest.getTextPart("documents")),
                    listeDocsToRemove = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeDocsToRemove")),
                    listDocumentParts = httpRequest.parts.filter { it.name.contains("document_") },
                ),
            ),
        ).wrap()
    }

    @GET
    @Path("/parametres")
    @RequireDroits([Droit.COURRIER_C])
    @Produces(MediaType.APPLICATION_JSON)
    fun getParametreByCourrier(): Response {
        return Response.ok(getCourriersWithParametresUseCase.execute(securityContext.userInfo)).build()
    }

    @GET
    @Path("/get-courrier")
    @RequireDroits([Droit.COURRIER_C])
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    fun getUriCourrier(@QueryParam("courrierPath") courrierPath: String?): Response {
        return Response.ok(
            courrierPath?.let { File(it) },
        )
            .build()
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
            File(Paths.get(document.documentRepertoire, document.documentNomFichier).pathString),
        )
    }
}
