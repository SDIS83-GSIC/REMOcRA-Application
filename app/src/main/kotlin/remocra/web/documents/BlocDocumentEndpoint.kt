package remocra.web.documents

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
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
import remocra.data.BlocDocumentData
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.BlocDocumentRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.security.NoCsrf
import remocra.usecase.document.DocumentUtils
import remocra.usecase.document.blocdocument.CreateBlocDocumentUseCase
import remocra.usecase.document.blocdocument.DeleteBlocDocumentUseCase
import remocra.utils.getTextPart
import remocra.utils.notFound
import remocra.web.AbstractEndpoint
import java.io.File
import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.pathString

@Path("/bloc-document")
@Produces(MediaType.APPLICATION_JSON)
class BlocDocumentEndpoint : AbstractEndpoint() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Inject
    lateinit var blocDocumentRepository: BlocDocumentRepository

    @Inject
    lateinit var createBlocDocumentUseCase: CreateBlocDocumentUseCase

    @Inject
    lateinit var deleteBlocDocumentUseCase: DeleteBlocDocumentUseCase

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var documentUtils: DocumentUtils

    @POST
    @Path("/")
    @RequireDroits([Droit.DOCUMENTS_R])
    fun getAll(params: Params<BlocDocumentRepository.Filter, BlocDocumentRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = blocDocumentRepository.getAllForAdmin(params),
                count = blocDocumentRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()

    /**
     * Télécharge le document.
     * @param documentId l'identifiant du document à télécharger
     * @return La réponse HTTP contenant le fichier à télécharger.
     */
    @GET
    @NoCsrf("On utilise une URL directe et donc on n'a pas les entêtes remplis, ce qui fait qu'on est obligé d'utiliser cette annotation")
    @Public("Le téléchargement des documents n'est pas dépendant d'un droit particulier")
    @Path("/telecharger/{blocDocumentId}")
    @Produces(MediaType.TEXT_PLAIN)
    fun telechargerBlocDocument(@PathParam("blocDocumentId") blocDocumentId: UUID): Response {
        val document = blocDocumentRepository.getDocumentByBlocDocument(blocDocumentId)

        if (document == null) {
            logger.error("Le bloc document $blocDocumentId n'a pas été trouvé.")
            return notFound().build()
        }

        return documentUtils.checkFile(
            File(Paths.get(document.documentRepertoire, document.documentNomFichier).pathString),
        )
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.DOCUMENTS_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun create(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        val blocDocumentData = BlocDocumentData(
            blocDocumentId = UUID.randomUUID(),
            blocDocumentLibelle = httpRequest.getTextPart("blocDocumentLibelle"),
            blocDocumentDecription = httpRequest.getTextPart("blocDocumentDecription"),
            listeThematiqueId = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeThematiqueId")),
            listeProfilDroitId = objectMapper.readValue<List<UUID>>(httpRequest.getTextPart("listeProfilDroitId")),
            document = httpRequest.getPart("document"),
        )

        return createBlocDocumentUseCase.execute(
            securityContext.userInfo,
            blocDocumentData,
        ).wrap()
    }

    @DELETE
    @Path("/delete/{blocDocumentId}")
    @RequireDroits([Droit.DOCUMENTS_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun delete(
        @PathParam("blocDocumentId")
        blocDocumentId: UUID,
    ): Response =
        deleteBlocDocumentUseCase.execute(
            securityContext.userInfo,
            blocDocumentRepository.getById(blocDocumentId),
        ).wrap()
}
