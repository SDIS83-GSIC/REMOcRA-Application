/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.tables.pojos

import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID
import javax.annotation.processing.Generated

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.3",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
data class Api(
    val apiOrganismeId: UUID,
    val apiPassword: String,
    val apiDerniereConnexion: LocalDateTime?,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (this::class != other::class) {
            return false
        }
        val o: Api = other as Api
        if (this.apiOrganismeId != o.apiOrganismeId) {
            return false
        }
        if (this.apiPassword != o.apiPassword) {
            return false
        }
        if (this.apiDerniereConnexion == null) {
            if (o.apiDerniereConnexion != null) {
                return false
            }
        } else if (this.apiDerniereConnexion != o.apiDerniereConnexion) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.apiOrganismeId.hashCode()
        result = prime * result + this.apiPassword.hashCode()
        result = prime * result + (if (this.apiDerniereConnexion == null) 0 else this.apiDerniereConnexion.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Api (")

        sb.append(apiOrganismeId)
        sb.append(", ").append(apiPassword)
        sb.append(", ").append(apiDerniereConnexion)

        sb.append(")")
        return sb.toString()
    }
}
