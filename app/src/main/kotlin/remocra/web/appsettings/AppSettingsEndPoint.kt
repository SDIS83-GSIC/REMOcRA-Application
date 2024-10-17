package remocra.web.appsettings

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.app.AppSettings
import remocra.auth.Public

@Path("/app-settings")
class AppSettingsEndPoint {

    @Inject lateinit var appSettings: AppSettings

    @GET
    @Path("/srid")
    @Public("Le srid n'est pas lié à un droit")
    fun getSrid(): Response =
        Response.ok(appSettings.sridInt).build()

    @GET
    @Path("/environment")
    @Produces(MediaType.APPLICATION_JSON)
    @Public("L'environment n'est pas lié à un droit")
    fun getEnvironement(): Response =
        Response.ok().entity(appSettings.environment).build()
}
