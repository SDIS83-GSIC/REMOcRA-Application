package remocra.web.appsettings

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Response
import remocra.app.AppSettings
import remocra.auth.Public

@Path("/app-settings")
@Produces("application/json; charset=UTF-8")
class AppSettingsEndPoint {

    @Inject lateinit var appSettings: AppSettings

    @GET
    @Path("/epsg")
    @Public("Le paramètre EPSG n'est pas lié à un droit")
    fun getEpsg() =
        Response.ok(appSettings.epsg).build()

    @GET
    @Path("/environment")
    @Public("L'environment n'est pas lié à un droit")
    fun getEnvironment(): Response =
        Response.ok().entity(appSettings.environment).build()

    @GET
    @Path("/version")
    @Public("Le paramètre version n'est pas lié à un droit")
    fun getVersion() =
        Response.ok(object { val version: String = appSettings.version }).build()
}
