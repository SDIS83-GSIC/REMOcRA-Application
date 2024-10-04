package remocra.web.parametres

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.app.ParametresProvider
import remocra.auth.Public
import remocra.web.AbstractEndpoint

@Path("/parametres")
class ParametreEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var parametresProvider: ParametresProvider

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Public("Les paramètres sont publics pour les utilisateurs authentifiés")
    fun getParametres(
        @QueryParam("listeParametreCode")
        listeParametreCode: Set<String>,
    ): Response {
        val mapParam = parametresProvider.get().mapParametres.filter { listeParametreCode.contains(it.key) }
        if (mapParam.isEmpty()) {
            throw IllegalArgumentException("Aucun paramètre n'a été trouvé : $listeParametreCode")
        }

        return Response.ok(mapParam).build()
    }
}
