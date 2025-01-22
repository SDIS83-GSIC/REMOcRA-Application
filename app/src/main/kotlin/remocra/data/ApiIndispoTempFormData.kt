package remocra.data

import java.time.ZonedDateTime

data class ApiIndispoTempFormData(
    val motif: String,
    val observation: String? = null,
    val dateDebut: ZonedDateTime,
    val mailAvantIndisponibilite: Boolean = true,
    val mailApresIndisponibilite: Boolean = true,
    val basculeAutoDisponible: Boolean = true,
    val basculeAutoIndisponible: Boolean = true,
    val notificationDebut: ZonedDateTime? = null,
    val notificationFin: ZonedDateTime? = null,
    val notificationResteIndispo: ZonedDateTime? = null,
    val dateFin: ZonedDateTime? = null,
    val listeNumeroPei: List<String>,
)
