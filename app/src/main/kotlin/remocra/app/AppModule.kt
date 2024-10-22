package remocra.app

import com.google.inject.Provides
import com.typesafe.config.Config
import jakarta.inject.Singleton
import remocra.RemocraModule
import remocra.data.DataCache
import remocra.data.ParametresData
import remocra.data.enums.CodeSdis
import remocra.data.enums.Environment
import remocra.utils.DateUtils
import java.time.Clock
import java.time.ZoneId

class AppModule(private val settings: AppSettings) : RemocraModule() {
    @Provides @Singleton
    fun provideClock() = Clock.system(ZoneId.systemDefault())

    @Provides @Singleton
    fun provideDateUtils() = DateUtils(provideClock())

    override fun configure() {
        bind(ParametresData::class.java).toProvider(ParametresProvider::class.java)
        bind(DataCache::class.java).toProvider(DataCacheProvider::class.java)
        bind(AppSettings::class.java).toInstance(settings)
    }

    companion object {
        fun create(config: Config) =
            AppModule(
                AppSettings(
                    environment = config.getEnum(Environment::class.java, "environment"),
                    codeSdis = config.getEnum(CodeSdis::class.java, "codeSdis"),
                    sridString = config.getString("srid"),
                    sridInt = config.getString("srid").split(":")[1].toInt(),
                ),
            )
    }
}
