package remocra.resteasy

import jakarta.ws.rs.ext.ParamConverter
import jakarta.ws.rs.ext.ParamConverterProvider
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.WKTReader
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

class ParamConverterProvider : ParamConverterProvider {
    @Suppress("UNCHECKED_CAST")
    override fun <T> getConverter(
        rawType: Class<T>,
        genericType: Type,
        annotations: Array<out Annotation>?,
    ): ParamConverter<T>? =
        when (rawType) {
            ZonedDateTime::class.java -> ZonedDateTimeParamConverter as ParamConverter<T>
            LocalDate::class.java -> LocalDateParamConverter as ParamConverter<T>
            OffsetDateTime::class.java -> OffsetDateTimeDateParamConverter as ParamConverter<T>
            Set::class.java -> SetParamConverter as ParamConverter<T>
            Geometry::class.java -> GeometryParamConverter as ParamConverter<T>
            else -> null
        }
}

private object LocalDateParamConverter : ParamConverter<LocalDate> {

    override fun toString(value: LocalDate?): String =
        (value ?: throw IllegalArgumentException()).toString()

    override fun fromString(value: String?): LocalDate? =
        try {
            (value ?: throw IllegalArgumentException()).takeUnless { it.isEmpty() }?.let {
                LocalDate.parse(it)
            }
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException(e)
        }
}

private object ZonedDateTimeParamConverter : ParamConverter<ZonedDateTime> {

    override fun toString(value: ZonedDateTime?): String =
        (value ?: throw IllegalArgumentException()).toString()

    override fun fromString(value: String?): ZonedDateTime? {
        val nonEmptyValue = value?.takeUnless { it.isEmpty() } ?: throw IllegalArgumentException()

        return try {
            ZonedDateTime.parse(nonEmptyValue)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException(e)
        }
    }
}

private object OffsetDateTimeDateParamConverter : ParamConverter<OffsetDateTime> {

    override fun toString(value: OffsetDateTime?): String =
        (value ?: throw IllegalArgumentException()).toString()

    override fun fromString(value: String?): OffsetDateTime? =
        try {
            (value ?: throw IllegalArgumentException()).takeUnless { it.isEmpty() }?.let {
                OffsetDateTime.parse(it)
            }
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException(e)
        }
}

private object SetParamConverter : ParamConverter<Set<*>> {

    override fun toString(value: Set<*>?): String =
        (value ?: throw IllegalArgumentException()).toString()

    override fun fromString(value: String?): Set<*>? =
        (value ?: throw IllegalArgumentException())
            .takeUnless { it.isEmpty() }
            ?.replace("[", "")
            ?.replace("]", "")
            ?.replace("\"", "")
            ?.split(",")
            ?.toHashSet()
}

private object GeometryParamConverter : ParamConverter<Geometry> {

    override fun toString(value: Geometry?): String =
        value?.let { "SRID=${value.srid};${value.toText()}" } ?: throw IllegalArgumentException()

    override fun fromString(value: String?): Geometry? =
        (value ?: throw IllegalArgumentException()).takeUnless { it.isEmpty() }?.split(";")?.let { wkt ->
            val srid = wkt[0].split("=")[1].toInt()
            val geometry: Geometry = WKTReader().read(wkt[1])
            geometry.srid = srid
            geometry
        }
}
