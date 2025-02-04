package remocra.http

import com.typesafe.config.Config
import remocra.RemocraModule
import remocra.getStringOrNull
import java.io.File
import java.nio.file.Path

class HttpServerModule(private val settings: HttpSettings) : RemocraModule() {

    override fun configure() {
        bind(HttpSettings::class.java).toInstance(settings)
    }

    companion object {
        fun create(config: Config) =
            HttpServerModule(
                HttpSettings(
                    port = config.getInt("port"),
                    gracefulStopTime = config.getInt("graceful-stop-time"),
                    idleTimeout = config.getInt("idle-timeout"),
                    sessionCookieName = config.getString("session-cookie-name"),
                    sessionMaxIdleTime = config.getDuration("session-max-idle-time"),
                    sessionStoreDir = config.getStringOrNull("session-store-dir")?.let { File(it) },
                    tempDirPrefix = config.getString("temp-dir-prefix"),
                    qosMaxRequests = config.getInt("qos.max-requests"),
                    qosMaxSuspendedRequests = config.getInt("qos.max-suspended-requests"),
                    qosMaxSuspend = config.getDuration("qos.max-suspend"),
                    staticDir = Path.of(config.getString("static-dir")),
                ),
            )
    }
}
