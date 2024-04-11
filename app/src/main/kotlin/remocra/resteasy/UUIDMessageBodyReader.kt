package remocra.resteasy

import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyReader
import java.io.InputStream
import java.lang.reflect.Type
import java.time.format.DateTimeParseException
import java.util.UUID

class UUIDMessageBodyReader : MessageBodyReader<UUID> {
    override fun readFrom(
        type: Class<UUID>,
        genericType: Type,
        annotations: Array<out Annotation>,
        mediaType: MediaType?,
        httpHeaders: MultivaluedMap<String, String>,
        entityStream: InputStream,
    ): UUID =
        try {
            UUID.fromString(entityStream.reader().readText())
        } catch (e: DateTimeParseException) {
            throw ProcessingException("Error parsing UUID.", e)
        }

    override fun isReadable(
        type: Class<*>,
        genericType: Type,
        annotations: Array<out Annotation>,
        mediaType: MediaType,
    ): Boolean = type == UUID::class.java
}
