package remocra.utils

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.BAD_GATEWAY
import okhttp3.HttpUrl
import java.util.stream.Collectors

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

fun badGateway(): Response.ResponseBuilder = Response.status(BAD_GATEWAY)

/**
 * Permet de simuler le limit + offset sur une collection, surtout utile pour les nomenclatures en cache afin d'éviter une requête SQL supplémentaire
 */
fun <E> Collection<E>.limitOffset(limit: Long?, offset: Long?): MutableSet<E>? {
    return this.stream().skip(offset ?: 0).limit(limit ?: this.size.toLong()).collect(Collectors.toSet())
}

fun HttpServletRequest.getTextPart(part: String) =
    this.getPart(part).inputStream.reader().readText()

fun HttpServletRequest.getTextPartOrNull(part: String): String? {
    val partObj = this.getPart(part) ?: return null
    val partText = partObj.inputStream.reader().readText()
    if (partText == "null" || partText.trim().isBlank()) {
        return null
    }
    return partText
}

fun HttpUrl.Builder.addQueryParameters(params: MultivaluedMap<String, String>): HttpUrl.Builder {
    params.forEach { (key, values) ->
        values.forEach { value ->
            this.addQueryParameter(key, value)
        }
    }
    return this
}
