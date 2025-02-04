package remocra.web.geoserver

import com.typesafe.config.Config
import okhttp3.HttpUrl
import remocra.RemocraModule
import remocra.web.registerResource

class GeoserverModule(private val settings: GeoserverSettings) : RemocraModule() {
    override fun configure() {
        bind(GeoserverSettings::class.java).toInstance(settings)
        this.binder().registerResource<LayersEndpoint>()
        this.binder().registerResource<GeoserverEndpoint>()
        this.binder().registerResource<CartoEndpoint>()
    }

    companion object {
        fun create(config: Config): GeoserverModule {
            return GeoserverModule(GeoserverSettings(HttpUrl.get(config.getString("url"))))
        }
    }

    data class GeoserverSettings(val url: HttpUrl)
}
