package remocra.utils

import remocra.data.enums.ErrorType
import remocra.exception.RemocraResponseException
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * Classe utilitaire pour tous les objets "date" On passe par une représentation en ZoneDateTime
 * pour gérer la différence de fuseau, en BDD on est sur de l'UTC mais sur le front et les appels,
 * on veut l'heure en GMT+1
 */
class DateUtils(val clock: Clock) {

    companion object {

        /** Pattern attendu pour les chaînes représentatives d'une date (moment)  */
        const val PATTERN_MINUTE_SECONDE: String = "yyyy-MM-dd HH:mm:ss"

        /** Pattern attendu pour les chaînes représentatives d'une date (moment) SANS secondes */
        const val PATTERN_MINUTE: String = "yyyy-MM-dd HH:mm"
        const val PATTERN_DATE_ONLY: String = "yyyy-MM-dd"

        const val PATTERN_NATUREL: String = "dd/MM/yyyy HH:mm:ss"
        const val PATTERN_NATUREL_DATE_ONLY: String = "dd/MM/yyyy"
    }

    /**
     * Retourne un moment (ZoneDateTime) à partir d'une chaîne de date
     *
     * @param dateString String
     * @return ZonedDateTime
     * @throws DateTimeParseException si la string ne peut pas être parsée
     */
    @Throws(DateTimeParseException::class)
    fun getMoment(dateString: String): ZonedDateTime {
        return ZonedDateTime.parse(
            dateString,
            DateTimeFormatter.ofPattern(PATTERN_MINUTE, Locale.getDefault()).withZone(clock.zone),
        )
    }

    /**
     * Retourne un moment (ZoneDateTime) à partir d'une date
     *
     * @param localDate LocalDate
     * @return ZonedDateTime
     * @throws DateTimeParseException si le format PATTERN_MINUTE n'est pas respecté
     */
    @Throws(DateTimeParseException::class)
    fun getMoment(localDate: LocalDate): ZonedDateTime {
        return localDate.atStartOfDay(clock.zone)
    }

    /**
     * Retourne un moment (ZoneDateTime) à partir d'une chaîne de date
     *
     * @param dateString String
     * @return ZonedDateTime
     * @throws DateTimeParseException si le format PATTERN_MINUTE n'est pas respecté
     */
    @Throws(DateTimeParseException::class)
    fun getInstant(localDateTime: LocalDateTime): ZonedDateTime {
        return localDateTime.atZone(clock.zone)
    }

    /**
     * Retourne une ZonedDateTime à l'instant now()
     * @return ZonedDateTime
     *
     */
    fun now(): ZonedDateTime {
        return ZonedDateTime.now(clock)
    }

    /**
     * Retourne un Instant à l'instant now()
     * @return Instant
     *
     */
    fun nowInstant(): Instant {
        return Instant.now(clock)
    }

    /**
     * Retourne un moment (ZoneDateTime) à partir d'une chaîne de date. L'encapsulation dans un ResponseException permet de retourner plus simplement au Endpoint appelenat
     *
     * @param momentString String
     * @return ZonedDateTime
     * @throws RemocraResponseException si le format PATTERN_MINUTE n'est pas respecté, pour encapsulation directe
     */
    @Throws(RemocraResponseException::class)
    fun getMomentForResponse(momentString: String): ZonedDateTime {
        try {
            return getMoment(momentString)
        } catch (dtpe: DateTimeParseException) {
            throw RemocraResponseException(
                ErrorType.BAD_PATTERN,
            )
        }
    }

    fun format(instant: Instant): String {
        return instant.atZone(clock.zone).format(formatter)
    }

    fun formatNaturel(instant: Instant): String {
        return instant.atZone(clock.zone).format(getFormatter(PATTERN_NATUREL))
    }

    fun formatIsoEtendu(instant: Instant): String {
        return instant.atZone(clock.zone).format(getFormatter(PATTERN_DATE_ONLY))
    }

    private val formatter: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern(PATTERN_MINUTE, Locale.getDefault())

    private fun getFormatter(pattern: String): DateTimeFormatter {
        return DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    }

    @JvmOverloads
    fun format(date: ZonedDateTime, pattern: String = PATTERN_MINUTE): String {
        return date.format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
    }

    fun formatDateOnly(date: ZonedDateTime?): String? {
        return date?.format(DateTimeFormatter.ofPattern(PATTERN_NATUREL_DATE_ONLY, Locale.getDefault()))
    }
}
