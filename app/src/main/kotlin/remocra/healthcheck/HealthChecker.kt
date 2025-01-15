package remocra.healthcheck

abstract class HealthChecker protected constructor(
    val critical: Boolean = true,
) {

    abstract fun check(): Health

    sealed class Health(val isHealthy: Boolean, val data: Any?) {
        class Success(data: Any?) : Health(true, data)
        class Failure(data: Any?) : Health(false, data)
        object Timeout : Health(false, null)
    }
}
