package remocra.web

import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/** As defined in [RFC 4918](https://tools.ietf.org/html/rfc4918#section-11.2) */
const val SC_UNPROCESSABLE_ENTITY = 422

/** As defined in [RFC 4918](https://tools.ietf.org/html/rfc4918#section-11.2) */
val UNPROCESSABLE_ENTITY =
    object : Response.StatusType {
        override fun getStatusCode(): Int = SC_UNPROCESSABLE_ENTITY

        override fun getFamily() = Response.Status.Family.CLIENT_ERROR

        override fun getReasonPhrase() = "Unprocessable entity"
    }

fun Response.ResponseBuilder.text(body: String?): Response.ResponseBuilder =
    type(MediaType.TEXT_PLAIN_TYPE).entity(body)

fun textResponse(status: Response.Status, body: String?): Response =
    textResponse(status as Response.StatusType, body)

fun textResponse(status: Response.StatusType, body: String?): Response =
    Response.status(status).text(body).build()

fun textResponse(status: Int, body: String?): Response = Response.status(status).text(body).build()

fun created(): Response.ResponseBuilder = Response.status(Response.Status.CREATED)

fun badRequest(): Response.ResponseBuilder = Response.status(Response.Status.BAD_REQUEST)

fun notFound(): Response.ResponseBuilder = Response.status(Response.Status.NOT_FOUND)

fun forbidden(): Response.ResponseBuilder = Response.status(Response.Status.FORBIDDEN)

fun conflict(): Response.ResponseBuilder = Response.status(Response.Status.CONFLICT)

fun unprocessableEntity(): Response.ResponseBuilder = Response.status(UNPROCESSABLE_ENTITY)
