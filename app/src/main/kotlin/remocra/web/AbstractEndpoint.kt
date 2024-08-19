package remocra.web

import jakarta.ws.rs.core.Response

abstract class AbstractEndpoint {

    sealed class Result {
        data class Success(val entity: Any? = null) : Result()
        data class NotFound(val message: String?) : Result()
        data class Forbidden(val message: String?) : Result()
        data class Error(val message: String?) : Result()
    }

    /** Wrappe un AbstractCUDUseCase.Result dans une response gérant les différents cas de retour */
    fun Result.wrap(): Response {
        return when (this) {
            is Result.Success -> Response.ok().entity(this.entity).build()
            is Result.NotFound -> notFound().text(this.message).build()
            is Result.Forbidden -> forbidden().text(this.message).build()
            is Result.Error -> Response.serverError().text(this.message).build()
        }
    }
}
