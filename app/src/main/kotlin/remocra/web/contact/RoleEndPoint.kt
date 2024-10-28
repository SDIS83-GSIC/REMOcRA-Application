package remocra.web.contact

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.db.RoleRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/role")
@Produces(MediaType.APPLICATION_JSON)
class RoleEndPoint : AbstractEndpoint() {

    @Inject
    lateinit var roleRepository: RoleRepository

    @GET
    @Path("/")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getRole(): Response {
        return Response.ok(roleRepository.getAll()).build()
    }
}
