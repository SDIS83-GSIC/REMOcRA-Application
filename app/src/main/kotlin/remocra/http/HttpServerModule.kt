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
                    qosMaxRequests = config.getString("qos.max-requests"),
                    qosWaitMS = config.getString("qos.wait-ms"),
                    qosSuspendMS = config.getString("qos.suspend-ms"),
                    staticDir = Path.of(config.getString("static-dir")),
                ),
            )
    }
}
