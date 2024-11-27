package remocra.web.rapportpersonnalise

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/rapport-personnalise")
@Produces(MediaType.APPLICATION_JSON)
class RapportPersonnaliseEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    fun getAll(params: Params<RapportPersonnaliseRepository.Filter, RapportPersonnaliseRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = rapportPersonnaliseRepository.getAllForAdmin(params),
                count = rapportPersonnaliseRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()

    @GET
    @Path("/get-type-module")
    @RequireDroits([Droit.ADMIN_RAPPORTS_PERSO])
    fun getTypeModule() =
        Response.ok(TypeModuleRapportCourrier.entries).build()
}
