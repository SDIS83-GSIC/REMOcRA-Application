package remocra.keycloak

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.typesafe.config.Config
import remocra.healthcheck.HealthModule
import java.net.URI

class KeycloakModule(private val healthcheckUrl: URI) : AbstractModule() {

    override fun configure() {
        HealthModule.addHealthCheck(binder(), "keycloak").to(KeycloakHealthChecker::class.java)
    }

    @Provides
    @HealthcheckUrl
    fun provideHealthcheckUrl(): URI {
        return healthcheckUrl
    }

    companion object {
        fun create(config: Config): KeycloakModule {
            return KeycloakModule(
                URI(config.getString("base-uri")).resolve("health"),
            )
        }
    }
}
