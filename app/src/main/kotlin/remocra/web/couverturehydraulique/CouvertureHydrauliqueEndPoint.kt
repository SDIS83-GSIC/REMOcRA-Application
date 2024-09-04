package remocra.web.couverturehydraulique

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
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/couverture-hydraulique")
class CouvertureHydrauliqueEndPoint : AbstractEndpoint() {

    @Inject lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    @POST
    @Path("/")
    @RequireDroits([Droit.ETUDE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getEtude(params: Params<CouvertureHydrauliqueRepository.Filter, CouvertureHydrauliqueRepository.Sort>): Response {
        return Response.ok(
            DataTableau(
                couvertureHydrauliqueRepository.getEtudes(
                    params.limit,
                    params.offset,
                    params.filterBy,
                    params.sortBy,
                ),
                couvertureHydrauliqueRepository.getCountEtudes(params.filterBy),
            ),
        ).build()
    }

    @GET
    @Path("/type-etudes")
    @RequireDroits([Droit.ETUDE_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun getTypeEtudes(): Response {
        return Response.ok(couvertureHydrauliqueRepository.getTypeEtudes()).build()
    }
}
