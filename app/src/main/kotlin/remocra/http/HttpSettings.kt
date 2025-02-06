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
    val qosMaxRequests: Int,
    val qosMaxSuspendedRequests: Int,
    val qosMaxSuspend: Duration,
    val staticDir: Path,
    val multipartTempDir: String,
    val multipartMaxFileSize: Long,
    val multipartMaxRequestSize: Long,
    val multipartFileSizeThreshold: Int,
)
