package remocra.web.module

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.UriInfo
import remocra.auth.Public
import remocra.usecases.module.ModuleUseCase
import java.io.File

@Path("/modules")
class ModuleEndPoint {

    @Inject lateinit var moduleUseCase: ModuleUseCase

    @Context lateinit var uriInfo: UriInfo

    @GET
    @Path("/")
    @Public("Les modules de la pages d'accueil sont accessibles à tous, la page d'accueil affichera les modules en fonction des droits")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAll(): Response =
        Response.ok(moduleUseCase.execute(uriInfo)).build()

    @GET
    @Path("/get-image")
    @Produces("image/png", "image/jpeg", "image/jpg")
    fun getUriImage(@QueryParam("imagePath") imagePath: String?): Response {
        return Response.ok(
            imagePath?.let { File(it) },
        )
            .build()
    }
}
