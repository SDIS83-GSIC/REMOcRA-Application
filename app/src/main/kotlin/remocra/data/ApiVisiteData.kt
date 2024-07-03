package remocra.data

import java.time.ZonedDateTime

data class ApiVisiteData(val visiteId: String, val moment: ZonedDateTime, val typeVisite: String, val anomalies: Collection<String>)
