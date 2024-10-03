package remocra.web

import jakarta.ws.rs.core.Response
import remocra.usecase.AbstractUseCase
import remocra.utils.badRequest
import remocra.utils.created
import remocra.utils.forbidden
import remocra.utils.notFound
import remocra.utils.text

abstract class AbstractEndpoint {

    /** Wrappe un AbstractCUDUseCase.Result dans une response gérant les différents cas de retour */
    fun AbstractUseCase.Result.wrap(): Response {
        return when (this) {
            is AbstractUseCase.Result.Created -> created().entity(this.entity).build()
            is AbstractUseCase.Result.Success -> Response.ok().entity(this.entity).build()
            is AbstractUseCase.Result.NotFound -> notFound().text(this.message).build()
            is AbstractUseCase.Result.Forbidden -> forbidden().text(this.message).build()
            is AbstractUseCase.Result.Error -> Response.serverError().text(this.message).build()
            is AbstractUseCase.Result.BadRequest -> badRequest().text(this.message).build()
        }
    }
}
