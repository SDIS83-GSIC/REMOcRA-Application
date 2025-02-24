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
data class TypeCrise(
    val typeCriseId: UUID,
    val typeCriseCode: String,
    val typeCriseLibelle: String,
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
        val o: TypeCrise = other as TypeCrise
        if (this.typeCriseId != o.typeCriseId) {
            return false
        }
        if (this.typeCriseCode != o.typeCriseCode) {
            return false
        }
        if (this.typeCriseLibelle != o.typeCriseLibelle) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.typeCriseId.hashCode()
        result = prime * result + this.typeCriseCode.hashCode()
        result = prime * result + this.typeCriseLibelle.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("TypeCrise (")

        sb.append(typeCriseId)
        sb.append(", ").append(typeCriseCode)
        sb.append(", ").append(typeCriseLibelle)

        sb.append(")")
        return sb.toString()
    }
}
