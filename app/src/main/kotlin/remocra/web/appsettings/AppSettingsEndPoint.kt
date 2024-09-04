package remocra.web.appsettings

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import remocra.app.AppSettings
import remocra.auth.Public

@Path("/app-settings")
class AppSettingsEndPoint {

    @Inject lateinit var appSettings: AppSettings

    @GET
    @Path("/srid")
    @Public("Le srid n'est pas lié à un droit")
    fun getSrid() =
        Response.ok(appSettings.sridInt).build()
}
