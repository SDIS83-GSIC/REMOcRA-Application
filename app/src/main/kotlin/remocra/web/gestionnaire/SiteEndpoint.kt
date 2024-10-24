package remocra.web.gestionnaire

import com.google.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.SiteRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/site")
@Produces(MediaType.APPLICATION_JSON)
class SiteEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var siteRepository: SiteRepository

    @POST
    @Path("/")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getAll(params: Params<SiteRepository.Filter, SiteRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = siteRepository.getAllForAdmin(params),
                count = siteRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()
}
