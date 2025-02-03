package remocra.data.oldeb

import java.time.ZonedDateTime

data class OldebData(
    val oldebId: String,
    val oldebAdresse: String?,
    val oldebTypeAvis: String?,
    val oldebCommune: String,
    val oldebDateDerniereVisite: ZonedDateTime?,
    val oldebTypeDebroussaillement: String?,
    val oldebTypeZoneUrbanisme: String?,
    val oldebParcelle: String,
    val oldebSection: String,
)
