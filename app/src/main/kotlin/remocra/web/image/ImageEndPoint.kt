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
// Liste des clés à chercher dans les paramètres (ici, les chemins d'images)
        val listImageHeader = listOf("BANNIERE_CHEMIN", "LOGO_CHEMIN")

// Filtrage des paramètres pour ne garder que ceux présents dans listImageHeader
        val mapParam = parametresProvider.get().mapParametres.filter { it.key in listImageHeader }

        val codeUri = mutableMapOf<String, String>() // Liste mutable pour stocker les objets CodeUri

// Si aucun paramètre correspondant n'est trouvé, on lance une exception
        if (mapParam.isEmpty()) {
            throw IllegalArgumentException("Aucun paramètre n'a été trouvé : $listImageHeader")
        }

// Pour chaque paramètre filtré, on crée une map key => parametreCode value => uri
        mapParam.forEach { param ->
            codeUri[param.value.parametreCode] = uriInfo.baseUriBuilder // Construction de l'URI en utilisant le paramètre
                .path(ImageEndPoint::class.java)
                .path(ImageEndPoint::getUri.javaMethod)
                .queryParam("path", param.value.parametreValeur) // Ajout du paramètre dans la query string
                .build()
                .toString()
        }

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
