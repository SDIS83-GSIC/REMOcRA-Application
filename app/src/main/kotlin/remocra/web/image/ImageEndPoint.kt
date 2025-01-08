package remocra.web.image

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.UriInfo
import remocra.GlobalConstants
import remocra.app.ParametresProvider
import remocra.auth.Public
import remocra.security.NoCsrf
import java.io.File
import kotlin.reflect.jvm.javaMethod

@Path("/image")
class ImageEndPoint {

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Context
    lateinit var uriInfo: UriInfo

    @GET
    @Path("/get-image-header")
    @Produces(MediaType.APPLICATION_JSON)
    @Public("Les images ne sont pas lié aux droits")
    fun getImageHeader(): Response {
        val codeUri = mutableMapOf<String, String>() // Liste mutable pour stocker les objets CodeUri
        codeUri["BANNIERE_CHEMIN"] = uriInfo.baseUriBuilder // Construction de l'URI en utilisant le paramètre
            .path(ImageEndPoint::class.java)
            .path(ImageEndPoint::getUri.javaMethod)
            .queryParam("path", GlobalConstants.BANNIERE_FULL_PATH) // Ajout du paramètre dans la query string
            .build()
            .toString()

        codeUri["LOGO_CHEMIN"] = uriInfo.baseUriBuilder // Construction de l'URI en utilisant le paramètre
            .path(ImageEndPoint::class.java)
            .path(ImageEndPoint::getUri.javaMethod)
            .queryParam("path", GlobalConstants.LOGO_FULL_PATH) // Ajout du paramètre dans la query string
            .build()
            .toString()

        return Response.ok().entity(codeUri).build()
    }

    @GET
    @Path("/get-uri")
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    @Public("Ce Endpoint est appelé par un autre Endpoint")
    @NoCsrf("Ce Endpoint est appelé par un autre Endpoint")
    fun getUri(@QueryParam("path") path: String?): Response {
        return Response.ok(
            path?.let { File(it) },
        )
            .build()
    }
}
