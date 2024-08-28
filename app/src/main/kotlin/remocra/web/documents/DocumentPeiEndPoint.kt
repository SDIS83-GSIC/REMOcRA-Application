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
import java.util.UUID

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
}
