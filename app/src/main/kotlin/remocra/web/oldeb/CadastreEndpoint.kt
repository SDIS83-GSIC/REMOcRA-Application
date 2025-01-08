package remocra.web.oldeb

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.db.CadastreRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.utils.forbidden
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/cadastre")
@Produces(MediaType.APPLICATION_JSON)
class CadastreEndpoint : AbstractEndpoint() {

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var cadastreRepository: CadastreRepository

    @GET
    @Path("/commune/{communeId}/section")
    @RequireDroits([Droit.OLDEB_R])
    fun section(@PathParam("communeId") communeId: UUID): Response =
        if (securityContext.userInfo!!.zoneCompetence == null && !securityContext.userInfo!!.isSuperAdmin) {
            forbidden().build()
        } else {
            Response.ok(cadastreRepository.getSectionByCommuneId(communeId, securityContext.userInfo!!.zoneCompetence!!.zoneIntegrationId)).build()
        }

    @GET
    @Path("/section/{sectionId}/parcelle")
    @RequireDroits([Droit.OLDEB_R])
    fun parcelle(@PathParam("sectionId") sectionId: UUID): Response = Response.ok(cadastreRepository.getParcelleBySectionId(sectionId)).build()

    @GET
    @Path("/section/{sectionId}/parcelle-old/")
    @RequireDroits([Droit.OLDEB_R])
    fun parcelleWithOld(@PathParam("sectionId") sectionId: UUID): Response = Response.ok(cadastreRepository.getParcelleWithOldBySectionId(sectionId)).build()
}
