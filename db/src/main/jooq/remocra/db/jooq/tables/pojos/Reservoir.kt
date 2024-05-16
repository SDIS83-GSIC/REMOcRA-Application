/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.tables.pojos

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
data class Reservoir(
    val reservoirId: UUID,
    val reservoirActif: Boolean,
    val reservoirNom: String,
    val reservoirCapacite: Int,
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
        val o: Reservoir = other as Reservoir
        if (this.reservoirId != o.reservoirId) {
            return false
        }
        if (this.reservoirActif != o.reservoirActif) {
            return false
        }
        if (this.reservoirNom != o.reservoirNom) {
            return false
        }
        if (this.reservoirCapacite != o.reservoirCapacite) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.reservoirId.hashCode()
        result = prime * result + this.reservoirActif.hashCode()
        result = prime * result + this.reservoirNom.hashCode()
        result = prime * result + this.reservoirCapacite.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Reservoir (")

        sb.append(reservoirId)
        sb.append(", ").append(reservoirActif)
        sb.append(", ").append(reservoirNom)
        sb.append(", ").append(reservoirCapacite)

        sb.append(")")
        return sb.toString()
    }
}
