package remocra.data

import remocra.db.jooq.remocra.enums.TypePei
import java.util.UUID

data class NatureWithDiametres(
    val natureId: UUID,
    val natureActif: Boolean,
    val natureCode: String,
    val natureLibelle: String,
    val natureTypePei: TypePei,
    val natureProtected: Boolean?,
    val diametreIds: Collection<UUID>,
)
