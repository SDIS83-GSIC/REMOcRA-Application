package remocra.data

import java.time.ZonedDateTime
import java.util.UUID

data class ApiIndispoTemporaireData(
    val indisponibiliteTemporaireId: UUID,
    val indisponibiliteTemporaireDateDebut: ZonedDateTime,
    val indisponibiliteTemporaireDateFin: ZonedDateTime?,
    val indisponibiliteTemporaireMotif: String,
    val indisponibiliteTemporaireObservation: String?,
    val indisponibiliteTemporaireBasculeAutoIndisponible: Boolean,
    val indisponibiliteTemporaireBasculeAutoDisponible: Boolean,
    val indisponibiliteTemporaireMailAvantIndisponibilite: Boolean,
    val indisponibiliteTemporaireMailApresIndisponibilite: Boolean,
    val listeNumeroPei: String?,
    val organismeApiMaj: String?,
)
