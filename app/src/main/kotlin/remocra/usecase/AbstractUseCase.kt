package remocra.usecase

import jakarta.inject.Inject
import remocra.utils.DateUtils

abstract class AbstractUseCase {
    sealed class Result {
        data class Created(val entity: Any? = null) : Result()
        data class Success(val entity: Any? = null) : Result()
        data class NotFound(val message: String?) : Result()
        data class Forbidden(val message: String?) : Result()
        data class Error(val message: String?) : Result()
        data class BadRequest(val message: String?) : Result()
        data class Conflict(val message: String?) : Result()
    }

    @Inject lateinit var dateUtils: DateUtils
}
