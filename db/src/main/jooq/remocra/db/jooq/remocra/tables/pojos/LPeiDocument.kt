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
        "jOOQ version:3.19.3",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
data class LPeiDocument(
    val peiId: UUID,
    val documentId: UUID,
    val isPhotoPei: Boolean,
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
        val o: LPeiDocument = other as LPeiDocument
        if (this.peiId != o.peiId) {
            return false
        }
        if (this.documentId != o.documentId) {
            return false
        }
        if (this.isPhotoPei != o.isPhotoPei) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.peiId.hashCode()
        result = prime * result + this.documentId.hashCode()
        result = prime * result + this.isPhotoPei.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("LPeiDocument (")

        sb.append(peiId)
        sb.append(", ").append(documentId)
        sb.append(", ").append(isPhotoPei)

        sb.append(")")
        return sb.toString()
    }
}
