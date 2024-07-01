package remocra.utils

import jakarta.inject.Inject
import java.time.Clock
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ZoneDateTimeUtils @Inject constructor(private val clock: Clock) {

    fun format(zonedDateTime: ZonedDateTime?): String? =
        zonedDateTime?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH).withZone(clock.zone))
}
