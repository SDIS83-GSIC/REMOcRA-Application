package remocra.web.documents

import com.google.inject.Inject
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.slf4j.LoggerFactory
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.db.CourrierRepository
import remocra.db.DocumentRepository
import remocra.security.NoCsrf
import remocra.usecase.document.DocumentUtils
import remocra.utils.notFound
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

    @Inject lateinit var courrierRepository: CourrierRepository

    @Context lateinit var securityContext: SecurityContext

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

        val response = documentUtils.checkFile(
            File(Paths.get(document.documentRepertoire, document.documentNomFichier).pathString),
        )

        val courierId: UUID? = documentRepository.getCourrierIdByDocumentId(documentId)
        // si c'est un courrier, on passe par le EndPoint "Courrier" pour gérer les accusés de la même façon partout
        if (courierId != null) {
            // Il faut être connecté pour télécharger un courrier et set l'accusé réception
            val idUtilisateur = securityContext.userInfo?.utilisateurId ?: throw ForbiddenException()
            if (response.status == Response.Status.OK.statusCode) {
                courrierRepository.setAccuse(courierId, idUtilisateur)
            }
        }
        return response
    }
}
