package remocra.data

import java.time.ZonedDateTime
import java.util.UUID

data class IndisponibiliteTemporaireData(

    val indisponibiliteTemporaireId: UUID,

    val indisponibiliteTemporaireMotif: String,

    val indisponibiliteTemporaireObservation: String? = null,

    val indisponibiliteTemporaireDateDebut: ZonedDateTime,

    val indisponibiliteTemporaireMailAvantIndisponibilite: Boolean = false,

    val indisponibiliteTemporaireMailApresIndisponibilite: Boolean = false,

    val indisponibiliteTemporaireBasculeAutoDisponible: Boolean = false,

    val indisponibiliteTemporaireBasculeAutoIndisponible: Boolean = false,

    val indisponibiliteTemporaireDateFin: ZonedDateTime?,

    val indisponibiliteTemporaireListePeiId: Collection<UUID>,

)
