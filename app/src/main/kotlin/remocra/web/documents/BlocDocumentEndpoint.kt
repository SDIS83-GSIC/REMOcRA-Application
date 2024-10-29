package remocra.web.documents

import com.google.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.BlocDocumentRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/bloc-document")
@Produces(MediaType.APPLICATION_JSON)
class BlocDocumentEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var blocDocumentRepository: BlocDocumentRepository

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
}
