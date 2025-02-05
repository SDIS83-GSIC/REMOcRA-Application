package remocra.geoserver

import com.typesafe.config.Config
import okhttp3.HttpUrl
import remocra.RemocraModule
import remocra.healthcheck.HealthModule

class GeoserverModule(private val settings: GeoserverSettings) : RemocraModule() {
    override fun configure() {
        bind(GeoserverSettings::class.java).toInstance(settings)
        HealthModule.addHealthCheck(binder(), "geoserver").to(GeoserverHealthChecker::class.java)
    }

    companion object {
        fun create(config: Config): GeoserverModule = GeoserverModule(
            GeoserverSettings(
                url = HttpUrl.get(config.getString("url")),
            ),
        )
    }

    data class GeoserverSettings(val url: HttpUrl)
}
