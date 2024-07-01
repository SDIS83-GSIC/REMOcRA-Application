package remocra.data

import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.pojos.Nature
import java.util.UUID

data class PeiForCalculDispoData(
    val peiId: UUID,
    val peiNatureId: UUID,

    val diametreId: UUID?,
    val debit: Int?,
    val pression: Double?,
    val pressionDynamique: Double?,

    val penaCapacite: Int?,
    val penaCapaciteIllimitee: Boolean?,

    var nature: Nature? = null,
    var diametre: Diametre? = null,
)
