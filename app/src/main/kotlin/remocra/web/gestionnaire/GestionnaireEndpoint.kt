package remocra.web.gestionnaire

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.GestionnaireRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/gestionnaire")
@Produces(MediaType.APPLICATION_JSON)
class GestionnaireEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var gestionnaireRepository: GestionnaireRepository

    @GET
    @Path("/get")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getAll(): Response =
        Response.ok(gestionnaireRepository.getAll()).build()

    @POST
    @Path("/")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getAll(params: Params<GestionnaireRepository.Filter, GestionnaireRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = gestionnaireRepository.getAllForAdmin(params),
                count = gestionnaireRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()
}
