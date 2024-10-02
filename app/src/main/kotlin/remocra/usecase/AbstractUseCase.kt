package remocra.usecase

abstract class AbstractUseCase {
    sealed class Result {
        data class Created(val entity: Any? = null) : Result()
        data class Success(val entity: Any? = null) : Result()
        data class NotFound(val message: String?) : Result()
        data class Forbidden(val message: String?) : Result()
        data class Error(val message: String?) : Result()
    }
}
