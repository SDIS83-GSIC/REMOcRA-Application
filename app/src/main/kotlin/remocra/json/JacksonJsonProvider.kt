package remocra.json

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider
import com.google.inject.Inject
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import remocra.web.badRequest
import remocra.web.text
import remocra.web.unprocessableEntity
import java.io.InputStream
import java.lang.reflect.Type

/**
 * Handles JSON parsing and mapping exceptions when reading.
 *
 * jackson-jaxrs-base comes with exception mappers for those, but we don't want exceptions thrown
 * afterwards to possibly leak sensible information. Only exception thrown when parsing the request
 * body should result in 4xx errors with an informative message, other Jackson exceptions should
 * result in a 5xx opaque message (with details logged for later analysis).
 */
class JacksonJsonProvider : JacksonJsonProvider() {

    @Inject override fun setMapper(m: ObjectMapper) = super.setMapper(m)

    override fun readFrom(
        type: Class<Any>,
        genericType: Type,
        annotations: Array<Annotation>,
        mediaType: MediaType,
        httpHeaders: MultivaluedMap<String, String>,
        entityStream: InputStream,
    ): Any {
        try {
            return super.readFrom(
                type,
                genericType,
                annotations,
                mediaType,
                httpHeaders,
                entityStream,
            )
        } catch (jpe: JsonParseException) {
            throw BadRequestException(badRequest().text(jpe.message).build(), jpe)
        } catch (jme: JsonMappingException) {
            throw WebApplicationException(jme, unprocessableEntity().text(jme.message).build())
        }
    }
}
