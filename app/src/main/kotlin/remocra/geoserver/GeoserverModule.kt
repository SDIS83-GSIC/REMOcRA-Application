package remocra.geoserver

import com.typesafe.config.Config
import remocra.RemocraModule
import remocra.web.registerResource

class GeoserverModule(private val settings: GeoserverSettings) : RemocraModule() {
    override fun configure() {
        bind(GeoserverSettings::class.java).toInstance(settings)
        this.binder().registerResource<LayersEndpoint>()
    }

    companion object {
        fun create(config: Config): GeoserverModule {
            return GeoserverModule(GeoserverSettings(config.getString("url")))
        }
    }

    data class GeoserverSettings(val url: String)
}
