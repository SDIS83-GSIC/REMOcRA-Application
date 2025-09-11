package remocra.web.groupefonctionnalites

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.db.GroupeFonctionnalitesRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/groupe-fonctionnalites")
@Produces(MediaType.APPLICATION_JSON)
class GroupeFonctionnalitesEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var groupeFonctionnalitesRepository: GroupeFonctionnalitesRepository

    @GET
    @Path("/")
    @Public("L'affichage des groupe de fonctionnalités n'est pas lié à un droit (par exemple : les filtres)")
    fun getGroupeFonctionnalites() =
        Response.ok(groupeFonctionnalitesRepository.getAll()).build()

    @GET
    @Path("/profils")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_R])
    fun getGroupeFonctionnalitesWithProfils() =
        Response.ok(groupeFonctionnalitesRepository.getGroupeFonctionnalitesWithProfils()).build()
}
