package remocra.web.documents

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import remocra.auth.Public
import remocra.db.DocumentRepository
import remocra.security.NoCsrf
import remocra.usecase.document.DocumentUtils
import remocra.web.notFound
import java.io.File
import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.pathString

@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
class DocumentEndPoint {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Inject lateinit var documentRepository: DocumentRepository

    @Inject lateinit var documentUtils: DocumentUtils

    /**
     * Télécharge le document.
     * @param documentId l'identifiant du document à télécharger
     * @return La réponse HTTP contenant le fichier à télécharger.
     */
    @GET
    @NoCsrf("On utilise une URL directe et donc on n'a pas les entêtes remplis, ce qui fait qu'on est obligé d'utiliser cette annotation")
    @Public("Le téléchargement des documents n'est pas dépendant d'un droit particulier")
    @Path("/telecharger/{documentId}")
    @Produces(MediaType.TEXT_PLAIN)
    fun telechargerRessource(@PathParam("documentId") documentId: UUID): Response {
        val document = documentRepository.getById(documentId)

        if (document == null) {
            logger.error("Le document $documentId n'a pas été trouvé.")
            return notFound().build()
        }

        return documentUtils.checkFile(
            File(Paths.get(document.documentRepertoire, document.documentNomFichier).pathString),
        )
    }
}
