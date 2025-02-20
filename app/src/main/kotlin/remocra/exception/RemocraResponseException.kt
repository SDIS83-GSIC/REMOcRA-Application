package remocra.exception

import jakarta.ws.rs.core.Response.Status
import remocra.GlobalConstants
import remocra.data.enums.ErrorType

/**
 * Exception à déclencher lors de toute erreur métier au sein d'un UseCase. Le [ErrorType] permettra à l'API de présenter le code de l'erreur en plus de son libellé
 */
class RemocraResponseException(override val message: String, val status: Status) :
    RuntimeException(message) {
    constructor(errorType: ErrorType, replacement: String? = null) : this(
        replacement?.let { errorType.toString().replace(GlobalConstants.PLACEHOLDER_ERROR_TYPE, it) } ?: errorType.toString(),
        errorType.status,
    )
}
