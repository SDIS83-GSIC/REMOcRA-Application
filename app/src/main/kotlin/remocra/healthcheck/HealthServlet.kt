package remocra.healthcheck

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap
import com.google.common.net.MediaType
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import remocra.healthcheck.HealthChecker.Health
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class HealthServlet
@Inject
constructor(
    private var settings: HealthModule.HealthSettings,
    private val healthchecks: Map<String, HealthChecker>,
    private val objectMapper: ObjectMapper,
) : HttpServlet() {
    private val logger = LoggerFactory.getLogger(HealthServlet::class.java)

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        // Lance tous les checks en parallèle
        val executor = Executors.newCachedThreadPool()
        val checks = LinkedHashMap<String, Future<Health>>(healthchecks.size)
        healthchecks.forEach { (key, value) ->
            checks[key] = executor.submit<Health> { value.check() }
        }

        // Attend qu'ils se terminent, mais pas trop longtemps
        executor.shutdown()
        try {
            executor.awaitTermination(settings.checkTimeout.toNanos(), TimeUnit.NANOSECONDS)
        } catch (ignore: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        executor.shutdownNow()
        val res = ImmutableMap.builder<String, Any>()
        var ok = true
        healthchecks.forEach { (key, value) ->
            var health: Health
            try {
                health = checks[key]!!.get()
            } catch (e: CancellationException) {
                health = Health.Timeout
            } catch (e: InterruptedException) {
                health = Health.Failure(e.message)
            } catch (e: ExecutionException) {
                logger.error("Health check {} failed", key, e.cause)
                health = Health.Failure(e.message)
            }
            res.put(key, health.data ?: health.isHealthy)
            if (value.critical) {
                ok = ok and health.isHealthy
            }
        }
        resp.status =
            if (ok) HttpServletResponse.SC_OK else HttpServletResponse.SC_SERVICE_UNAVAILABLE
        resp.contentType = MediaType.JSON_UTF_8.toString()
        objectMapper.writeValue(resp.writer, res.build())
    }

    companion object {
        const val PATH = "/health"
    }
}
