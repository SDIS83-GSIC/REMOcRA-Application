/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

import java.io.Serializable
import java.util.UUID
import javax.annotation.processing.Generated

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.11",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
data class LCourrierContactGestionnaire(
    val courrierId: UUID,
    val contactId: UUID,
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
        val o: LCourrierContactGestionnaire = other as LCourrierContactGestionnaire
        if (this.courrierId != o.courrierId) {
            return false
        }
        if (this.contactId != o.contactId) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.courrierId.hashCode()
        result = prime * result + this.contactId.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("LCourrierContactGestionnaire (")

        sb.append(courrierId)
        sb.append(", ").append(contactId)

        sb.append(")")
        return sb.toString()
    }
}
