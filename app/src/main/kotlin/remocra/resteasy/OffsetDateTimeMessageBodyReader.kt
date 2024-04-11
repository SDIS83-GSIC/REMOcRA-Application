package remocra.resteasy

import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyReader
import java.io.InputStream
import java.lang.reflect.Type
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

class OffsetDateTimeMessageBodyReader : MessageBodyReader<OffsetDateTime> {
    override fun readFrom(
        type: Class<OffsetDateTime>,
        genericType: Type,
        annotations: Array<out Annotation>,
        mediaType: MediaType?,
        httpHeaders: MultivaluedMap<String, String>,
        entityStream: InputStream,
    ): OffsetDateTime =
        try {
            OffsetDateTime.parse(entityStream.reader().readText())
        } catch (e: DateTimeParseException) {
            throw ProcessingException("Error parsing OffsetDateTime.", e)
        }

    override fun isReadable(
        type: Class<*>,
        genericType: Type,
        annotations: Array<out Annotation>,
        mediaType: MediaType,
    ): Boolean = type == OffsetDateTime::class.java
}
