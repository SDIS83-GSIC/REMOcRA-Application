package remocra.data

import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import java.time.ZonedDateTime
import java.util.UUID

data class PeiDiffData(
    val peiId: UUID,
    val numeroComplet: String,
    val momentModification: ZonedDateTime,
    // Organisme ou utilisateur, formaté en adéquation
    val auteurModification: String?,
    val auteur: AuteurTracabiliteData,
    val typeOperation: TypeOperation,
    val typeObjet: TypeObjet,
)
