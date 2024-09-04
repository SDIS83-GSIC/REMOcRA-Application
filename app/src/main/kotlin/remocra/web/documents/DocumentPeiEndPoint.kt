package remocra.web.documents

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.UriInfo
import remocra.auth.RequireDroits
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecases.document.DocumentPeiUseCase
import java.io.File
import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.pathString

@Path("/documents/pei")
@Produces(MediaType.APPLICATION_JSON)
class DocumentPeiEndPoint {

    @Inject lateinit var documentPeiUseCase: DocumentPeiUseCase

    @Context lateinit var uriInfo: UriInfo

    @GET
    @Path("/{peiId}")
    @RequireDroits([Droit.PEI_R, Droit.PEI_U, Droit.PEI_C])
    fun getDocumentByPeiId(
        @PathParam("peiId")
        peiId: UUID,
    ) = Response.ok(documentPeiUseCase.execute(peiId)).build()

    /**
     * Télécharge le document.
     * @param documentId l'identifiant du document à télécharger
     * @return La réponse HTTP contenant le fichier à télécharger.
     */
    @GET
    @Path("/telecharger/{documentId}")
    @RequireDroits([Droit.DOCUMENTS_R])
    @Produces(MediaType.TEXT_PLAIN)
    fun telechargerRessource(@PathParam("documentId") documentId: UUID): Response {
        val document = documentPeiUseCase.telecharger(documentId)
        return Response.ok(File(Paths.get(document!!.documentRepertoire, document.documentNomFichier).pathString).readBytes())
            .header("Content-Disposition", "attachment; filename=\"${document.documentNomFichier}\"")
            .build()
    }
}
