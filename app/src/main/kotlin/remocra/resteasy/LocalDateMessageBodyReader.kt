package remocra.resteasy

import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyReader
import java.io.InputStream
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeParseException

class LocalDateMessageBodyReader : MessageBodyReader<LocalDate> {
    override fun readFrom(
        type: Class<LocalDate>,
        genericType: Type,
        annotations: Array<out Annotation>,
        mediaType: MediaType?,
        httpHeaders: MultivaluedMap<String, String>,
        entityStream: InputStream,
    ): LocalDate =
        try {
            LocalDate.parse(entityStream.reader().readText())
        } catch (e: DateTimeParseException) {
            throw ProcessingException("Error parsing LocalDate.", e)
        }

    override fun isReadable(
        type: Class<*>,
        genericType: Type,
        annotations: Array<out Annotation>,
        mediaType: MediaType,
    ): Boolean = type == LocalDate::class.java
}
