package remocra.data

import java.time.ZonedDateTime

data class ApiVisiteData(val visiteId: String, val moment: ZonedDateTime, val visiteTypeVisite: String, val anomalies: Collection<String>?)
