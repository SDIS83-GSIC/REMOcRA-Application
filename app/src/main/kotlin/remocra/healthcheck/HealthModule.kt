package remocra.healthcheck

import com.google.inject.AbstractModule
import com.google.inject.Binder
import com.google.inject.binder.LinkedBindingBuilder
import com.typesafe.config.Config
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMapBinder
import java.time.Duration

class HealthModule(private val settings: HealthSettings) : AbstractModule() {
    override fun configure() {
        bind(HealthSettings::class.java).toInstance(settings)
    }

    companion object {
        fun create(config: Config): HealthModule {
            return HealthModule(HealthSettings(config.getDuration("timeout")))
        }

        fun addHealthCheck(binder: Binder, name: String): LinkedBindingBuilder<HealthChecker> {
            return KotlinMapBinder.newMapBinder<String, HealthChecker>(binder).addBinding(name)
        }
    }

    data class HealthSettings(val checkTimeout: Duration)
}
