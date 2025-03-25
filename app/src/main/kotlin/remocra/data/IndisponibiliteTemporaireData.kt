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

    val indisponibiliteTemporaireNotificationDebut: ZonedDateTime? = null,

    val indisponibiliteTemporaireNotificationFin: ZonedDateTime? = null,

    val indisponibiliteTemporaireNotificationResteIndispo: ZonedDateTime? = null,

    val indisponibiliteTemporaireBasculeDebut: Boolean = false,

    val indisponibiliteTemporaireBasculeFin: Boolean = false,

    val indisponibiliteTemporaireDateFin: ZonedDateTime? = null,

    val indisponibiliteTemporaireListePeiId: Collection<UUID>,

)
