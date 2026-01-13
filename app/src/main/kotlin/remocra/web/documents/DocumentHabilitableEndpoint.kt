package remocra.web.documents

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
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
import org.slf4j.LoggerFactory
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.DocumentHabilitableData
import remocra.data.Params
import remocra.db.DocumentHabilitableRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.security.NoCsrf
import remocra.usecase.document.DocumentUtils
import remocra.usecase.document.documenthabilitable.CreateDocumentHabilitableUseCase
import remocra.usecase.document.documenthabilitable.DeleteDocumentHabilitableUseCase
import remocra.usecase.document.documenthabilitable.UpdateDocumentHabilitableUseCase
import remocra.utils.getTextPart
import remocra.utils.getTextPartOrNull
import remocra.utils.notFound
import remocra.web.AbstractEndpoint
import java.util.UUID
import kotlin.io.path.Path

@Path("/document-habilitable")
@Produces(MediaType.APPLICATION_JSON)
class DocumentHabilitableEndpoint : AbstractEndpoint() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Inject
    lateinit var documentHabilitableRepository: DocumentHabilitableRepository

    @Inject
    lateinit var createDocumentHabilitableUseCase: CreateDocumentHabilitableUseCase

    @Inject
    lateinit var deleteDocumentHabilitableUseCase: DeleteDocumentHabilitableUseCase

    @Inject
    lateinit var updateDocumentHabilitableUseCase: UpdateDocumentHabilitableUseCase

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var documentUtils: DocumentUtils

    @POST
    @Path("/")
    @RequireDroits([Droit.DOCUMENTS_R])
    fun getAll(params: Params<DocumentHabilitableRepository.Filter, DocumentHabilitableRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = documentHabilitableRepository.getAllForAdmin(params),
                count = documentHabilitableRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()

    @GET
    @Path("/{code-thematique}")
    @RequireDroits([Droit.DOCUMENTS_R])
    fun getThematique(
        @PathParam("code-thematique") codeThematique: String,
    ): Response =
        Response.ok(documentHabilitableRepository.getDocumentIdLibelleDateByCodeThematique(codeThematique)).build()

    /**
     * Télécharge le document.
     * @param documentId l'identifiant du document à télécharger
     * @return La réponse HTTP contenant le fichier à télécharger.
     */
    @GET
    @NoCsrf("On utilise une URL directe et donc on n'a pas les entêtes remplis, ce qui fait qu'on est obligé d'utiliser cette annotation")
    @Public("Le téléchargement des documents n'est pas dépendant d'un droit particulier")
    @Path("/telecharger/{documentHabilitableId}")
    @Produces(MediaType.TEXT_PLAIN)
    fun telechargerDocumentHabilitable(@PathParam("documentHabilitableId") documentHabilitableId: UUID): Response {
        val document = documentHabilitableRepository.getDocumentByDocumentHabilitable(documentHabilitableId)

        if (document == null) {
            logger.error("Le document habilitable $documentHabilitableId n'a pas été trouvé.")
            return notFound().build()
        }

        return documentUtils.checkFile(
            Path(document.documentRepertoire, document.documentNomFichier),
        )
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.DOCUMENTS_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun create(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val documentHabilitableData = DocumentHabilitableData(
            documentHabilitableId = UUID.randomUUID(),
            documentHabilitableLibelle = httpRequest.getTextPartOrNull("documentHabilitableLibelle"),
            documentHabilitableDescription = httpRequest.getTextPartOrNull("documentHabilitableDescription"),
            listeThematiqueId = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeThematiqueId")),
            listeGroupeFonctionnalitesId = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeGroupeFonctionnalitesId")),
            document = httpRequest.getPart("document"),
        )

        return createDocumentHabilitableUseCase.execute(
            securityContext.userInfo,
            documentHabilitableData,
        ).wrap()
    }

    @DELETE
    @Path("/delete/{documentHabilitableId}")
    @RequireDroits([Droit.DOCUMENTS_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun delete(
        @PathParam("documentHabilitableId")
        documentHabilitableId: UUID,
    ): Response =
        deleteDocumentHabilitableUseCase.execute(
            securityContext.userInfo,
            documentHabilitableRepository.getById(documentHabilitableId),
        ).wrap()

    @GET
    @Path("/get/{documentHabilitableId}")
    @RequireDroits([Droit.DOCUMENTS_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun get(
        @PathParam("documentHabilitableId")
        documentHabilitableId: UUID,
    ): Response =
        Response.ok(documentHabilitableRepository.getById(documentHabilitableId)).build()

    @PUT
    @Path("/update/{documentHabilitableId}")
    @RequireDroits([Droit.DOCUMENTS_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun update(
        @PathParam("documentHabilitableId")
        documentHabilitableId: UUID,
        @Context httpRequest: HttpServletRequest,
    ): Response =
        updateDocumentHabilitableUseCase.execute(
            securityContext.userInfo,
            DocumentHabilitableData(
                documentHabilitableId = documentHabilitableId,
                documentHabilitableLibelle = httpRequest.getTextPartOrNull("documentHabilitableLibelle"),
                documentHabilitableDescription = httpRequest.getTextPartOrNull("documentHabilitableDescription"),
                listeThematiqueId = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeThematiqueId")),
                listeGroupeFonctionnalitesId = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeGroupeFonctionnalitesId")),
                document = if (httpRequest.getPart("document").submittedFileName != null) httpRequest.getPart("document") else null,
            ),
        ).wrap()
}
