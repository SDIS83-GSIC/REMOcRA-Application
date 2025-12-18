package remocra.web.admin

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.GroupeCoucheRepository
import remocra.db.GroupeCoucheRepository.FilterGroupeCouche
import remocra.db.GroupeCoucheRepository.Sort
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Produces("application/json; charset=UTF-8")
@Path("/admin/groupe-couche")
class GroupeCoucheEndpoint : AbstractEndpoint() {
    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var groupeCoucheRepository: GroupeCoucheRepository

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Path("/")
    @POST
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    fun list(
        params: Params<FilterGroupeCouche, Sort>,
    ): Response =
        Response.ok(
            DataTableau(
                groupeCoucheRepository.getAllForAdmin(params),
                groupeCoucheRepository.countForAdmin(params.filterBy),
            ),
        ).build()
}
