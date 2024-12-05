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
data class OldebVisiteDocument(
    val oldebVisiteDocumentId: UUID,
    val oldebVisiteDocumentOldebVisiteId: UUID,
    val oldebVisiteDocumentDocumentId: UUID,
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
        val o: OldebVisiteDocument = other as OldebVisiteDocument
        if (this.oldebVisiteDocumentId != o.oldebVisiteDocumentId) {
            return false
        }
        if (this.oldebVisiteDocumentOldebVisiteId != o.oldebVisiteDocumentOldebVisiteId) {
            return false
        }
        if (this.oldebVisiteDocumentDocumentId != o.oldebVisiteDocumentDocumentId) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.oldebVisiteDocumentId.hashCode()
        result = prime * result + this.oldebVisiteDocumentOldebVisiteId.hashCode()
        result = prime * result + this.oldebVisiteDocumentDocumentId.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("OldebVisiteDocument (")

        sb.append(oldebVisiteDocumentId)
        sb.append(", ").append(oldebVisiteDocumentOldebVisiteId)
        sb.append(", ").append(oldebVisiteDocumentDocumentId)

        sb.append(")")
        return sb.toString()
    }
}
