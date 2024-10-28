package remocra.web.lieudit

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.db.LieuDitRepository

@Path("/lieu-dit")
@Produces(MediaType.APPLICATION_JSON)
class LieuDitEndPoint {

    @Inject
    lateinit var lieuDitRepository: LieuDitRepository

    @GET
    @Path("/get")
    @Public("Les lieux-dits ne sont pas liés à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getLieuDitForSelect(): Response {
        return Response.ok(
            lieuDitRepository.getLieuDitWithCommune(),
        )
            .build()
    }
}
