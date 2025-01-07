package remocra.web.tracabilite

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.data.enums.TypeSourceModification
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.web.AbstractEndpoint

@Path("/tracabilite")
@Produces("application/json; charset=UTF-8")
class TracabiliteEndpoint : AbstractEndpoint() {
    @GET
    @Path("refs")
    @Public("référentiels de données de recherche")
    fun getFormReferences(): Response {
        val typeOperations = enumValues<TypeOperation>().map { it.name }
        val typeObjets = enumValues<TypeObjet>().map { it.name }
        val typeUtilisateurs = enumValues<TypeSourceModification>().map { it.name }
        return Response.ok(
            mapOf(
                "typeOperations" to typeOperations,
                "typeObjets" to typeObjets,
                "typeUtilisateurs" to typeUtilisateurs,
            ),
        ).build()
    }
}
