package remocra.data

import java.util.UUID

/**
 * Interface permettant de récupérer l'id, le code, et la version d'un objet
 */
interface IdCodeVersion {
    val id: UUID
    val code: String
    val version: Int
}
