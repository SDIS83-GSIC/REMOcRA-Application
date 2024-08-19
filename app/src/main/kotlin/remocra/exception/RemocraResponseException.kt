package remocra.exception

import remocra.data.enums.ErrorType

/**
 * Exception à déclencher lors de toute erreur métier au sein d'un UseCase. Le [ErrorType] permettra à l'API de présenter le code de l'erreur en plus de son libellé
 */
class RemocraResponseException(val errorType: ErrorType) : Exception(errorType.toString())
