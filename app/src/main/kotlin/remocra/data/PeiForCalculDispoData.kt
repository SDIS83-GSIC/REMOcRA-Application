package remocra.data

import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.Reservoir
import java.util.UUID

data class PeiForCalculDispoData(
    val peiId: UUID,
    val peiNatureId: UUID,

    val diametreId: UUID?,
    val reservoirId: UUID?,
    val debit: Int?,
    val pression: Double?,
    val pressionDynamique: Double?,

    val penaCapacite: Int?,
    val penaCapaciteIllimitee: Boolean?,
    val penaCapaciteIncertaine: Boolean?,

    var nature: Nature? = null,
    var diametre: Diametre? = null,
    var reservoir: Reservoir? = null,
)
