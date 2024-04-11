package remocra.http

import com.google.inject.AbstractModule
import com.typesafe.config.Config
import java.io.File
import java.nio.file.Path

class HttpServerModule(private val settings: HttpSettings) : AbstractModule() {

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
                    sessionStoreDir =
                    if (config.hasPath("session-store-dir")) {
                        File(config.getString("session-store-dir"))
                    } else {
                        null
                    },
                    tempDirPrefix = config.getString("temp-dir-prefix"),
                    qosMaxRequests = config.getString("qos.max-requests"),
                    qosWaitMS = config.getString("qos.wait-ms"),
                    qosSuspendMS = config.getString("qos.suspend-ms"),
                    staticDir = Path.of(config.getString("static-dir")),
                ),
            )
    }
}
