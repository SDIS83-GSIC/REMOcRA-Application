package remocra.resteasy

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Mapper des exceptions non gérées dans la servlet. Cela permet d'avoir un événement Sentry avec le
 * contexte de la requête de servlet.
 */
class UnhandledExceptionMapper : ExceptionMapper<Throwable> {
    override fun toResponse(exception: Throwable): Response {
        if (exception is WebApplicationException) {
            val response: Response? = exception.response
            if (response != null) {
                return response
            }
        }
        logger.error("Unhandled exception: {}", exception.message, exception)
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            // TODO: mediatype ?
            .entity(exception.message)
            .build()
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UnhandledExceptionMapper::class.java)
    }
}
