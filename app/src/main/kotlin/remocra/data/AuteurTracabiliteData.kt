package remocra.data

import remocra.data.enums.TypeSourceModification
import java.util.UUID

data class AuteurTracabiliteData(
    val idAuteur: UUID,
    val nom: String?,
    val prenom: String?,
    val email: String,
    val typeSourceModification: TypeSourceModification,
)
