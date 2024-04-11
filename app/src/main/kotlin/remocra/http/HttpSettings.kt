package remocra.http

import java.io.File
import java.nio.file.Path
import java.time.Duration

data class HttpSettings(
    val port: Int,
    val gracefulStopTime: Int,
    val idleTimeout: Int,
    val sessionCookieName: String,
    val sessionMaxIdleTime: Duration,
    val sessionStoreDir: File?,
    val tempDirPrefix: String,
    val qosMaxRequests: String,
    val qosWaitMS: String,
    val qosSuspendMS: String,
    val staticDir: Path,
)
