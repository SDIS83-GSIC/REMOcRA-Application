package remocra.data

import remocra.db.jooq.remocra.enums.TypeVisite
import java.time.ZonedDateTime
import java.util.UUID

data class ApiVisiteSpecifiqueData(
    val moment: ZonedDateTime,
    var visiteId: UUID,
    var typeVisite: TypeVisite,
    var agent1: String?,
    var agent2: String?,
    var debit: Int,
    var pression: Double,
    var pressionDyn: Double,
    var ctrlDebitPression: Boolean,
    var anomaliesConstatees: Collection<String>?,
    var anomaliesControlees: Collection<String>?,
    var observations: String? = null,
)
