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
data class LDiametreNature(
    val diametreId: UUID,
    val natureId: UUID,
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
        val o: LDiametreNature = other as LDiametreNature
        if (this.diametreId != o.diametreId) {
            return false
        }
        if (this.natureId != o.natureId) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.diametreId.hashCode()
        result = prime * result + this.natureId.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("LDiametreNature (")

        sb.append(diametreId)
        sb.append(", ").append(natureId)

        sb.append(")")
        return sb.toString()
    }
}
