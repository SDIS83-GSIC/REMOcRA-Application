package remocra.app

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.typesafe.config.Config
import jakarta.inject.Singleton
import remocra.data.NomenclaturesData
import remocra.data.ParametresData
import remocra.data.enums.Environment
import java.time.Clock
import java.time.ZoneId

class AppModule(private val settings: AppSettings) : AbstractModule() {
    @Provides @Singleton
    fun provideClock() = Clock.system(ZoneId.systemDefault())

    override fun configure() {
        bind(ParametresData::class.java).toProvider(ParametresProvider::class.java)
        bind(NomenclaturesData::class.java).toProvider(NomenclaturesProvider::class.java)
        bind(AppSettings::class.java).toInstance(settings)
    }

    companion object {
        fun create(config: Config) =
            AppModule(
                AppSettings(environment = config.getEnum(Environment::class.java, "environment")),
            )
    }
}
