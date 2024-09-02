package remocra.exception

import jakarta.ws.rs.core.Response.Status
import remocra.data.enums.ErrorType

/**
 * Exception à déclencher lors de toute erreur métier au sein d'un UseCase. Le [ErrorType] permettra à l'API de présenter le code de l'erreur en plus de son libellé
 */
class RemocraResponseException(override val message: String, val status: Status) : Exception(message) {
    constructor(errorType: ErrorType) : this(errorType.toString(), errorType.status)

    // TODO gérer ça avec un test unitaire
    constructor(code: Int, libelle: String, status: Status = Status.BAD_REQUEST) : this("$code - $libelle", status) {
        if (ErrorType.entries.map { it.code }.contains(code)) {
            throw IllegalArgumentException("Le code de l'errorType est déjà utilisé !")
        }
    }
}
