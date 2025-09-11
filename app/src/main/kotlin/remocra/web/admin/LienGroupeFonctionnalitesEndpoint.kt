package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.GroupeFonctionnalitesData
import remocra.db.GroupeFonctionnalitesRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.GroupeFonctionnalites
import remocra.usecase.admin.liengroupefonctionnalites.UpdateLienGroupeFonctionnalitesUseCase
import remocra.web.AbstractEndpoint

@Produces("application/json; charset=UTF-8")
@Path("/lien-groupe-fonctionnalites")
class LienGroupeFonctionnalitesEndpoint : AbstractEndpoint() {
    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var groupeFonctionnalitesRepository: GroupeFonctionnalitesRepository

    @Inject lateinit var updateLienDroitUseCase: UpdateLienGroupeFonctionnalitesUseCase

    @Path("/")
    @GET
    @RequireDroits([Droit.ADMIN_DROITS])
    fun list(): Response =
        Response.ok(
            object {
                val groupeFonctionnalitesList: Collection<GroupeFonctionnalites> = groupeFonctionnalitesRepository.getAllForAdmin().sortedBy { it.groupeFonctionnalitesLibelle }
                val typeDroitList: Collection<Droit> = Droit.entries.sortedBy { it }
            },
        ).build()

    @Path("/update")
    @PUT
    @RequireDroits([Droit.ADMIN_DROITS])
    fun put(element: Collection<GroupeFonctionnalitesData>): Response =
        updateLienDroitUseCase.execute(securityContext.userInfo, element).wrap()
}
