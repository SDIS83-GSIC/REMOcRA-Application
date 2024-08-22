package remocra.sentry

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.multibindings.ProvidesIntoOptional
import com.google.inject.multibindings.ProvidesIntoOptional.Type.ACTUAL
import com.typesafe.config.Config
import io.sentry.ExternalOptions
import io.sentry.SentryOptions
import io.sentry.SystemOutLogger
import io.sentry.config.PropertiesProvider
import org.apache.logging.log4j.Level

class SentryModule
private constructor(
    @get:SentryJava
    @get:ProvidesIntoOptional(ACTUAL)
    val sentryOptions: SentryOptions?,
    private val sentryAppenderConfig: SentryAppenderConfig,
) : AbstractModule() {

    companion object {
        private const val DSN = "dsn"

        fun create(config: Config): SentryModule {
            // Configuration du client java
            val javaSentryOptions =
                getJavaSentryOptions(config.getConfig("java"))

            // Configuration pour l'appender log4j2
            val sentryAppenderConfig =
                SentryAppenderConfig(
                    Level.toLevel(config.getString("log4j2.minimum-breadcrumb-level")),
                    Level.toLevel(config.getString("log4j2.minimum-event-level")),
                )

            return SentryModule(javaSentryOptions, sentryAppenderConfig)
        }

        private fun getJsConfigMap(javascript: Config): Map<String, String>? =
            if (!javascript.hasPath(DSN)) {
                null
            } else {
                javascript.toMap()
            }

        private fun Config.toMap(): MutableMap<String, String> =
            HashMap<String, String>().also {
                entrySet().forEach { (k, v) -> it[k] = v.unwrapped().toString() }
            }

        private fun getJavaSentryOptions(java: Config): SentryOptions? {
            if (!java.hasPath(DSN)) {
                return null
            }
            val externalOption = ExternalOptions.from(ConfigPropertiesProvider(java), SystemOutLogger())
            val sentryOptions = SentryOptions()
            sentryOptions.merge(externalOption)
            return sentryOptions
        }
    }

    @Provides @Singleton
    fun provideSentryAppenderConfig() = sentryAppenderConfig
}

class ConfigPropertiesProvider(private val config: Config) : PropertiesProvider {
    override fun getProperty(property: String): String? {
        return if (config.hasPath(property)) config.getString(property) else null
    }

    override fun getMap(property: String): Map<String, String> {
        @Suppress("UNCHECKED_CAST")
        return if (config.hasPath(property)) {
            config.getValue(property).unwrapped() as Map<String, String>
        } else java.util.Map.of()
    }

    override fun getList(property: String): List<String> {
        return if (config.hasPath(property)) config.getStringList(property) else emptyList()
    }

    override fun getProperty(property: String, defaultValue: String): String {
        return if (config.hasPath(property)) config.getString(property) else defaultValue
    }

    override fun getBooleanProperty(property: String): Boolean? {
        return if (config.hasPath(property)) config.getBoolean(property) else null
    }

    override fun getDoubleProperty(property: String): Double? {
        return if (config.hasPath(property)) config.getDouble(property) else null
    }
}

data class SentryAppenderConfig(
    val minimumBreadcrumbLevel: Level,
    val minimumEventLevel: Level,
)
