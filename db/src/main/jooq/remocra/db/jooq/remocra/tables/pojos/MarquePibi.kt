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
data class MarquePibi(
    val marquePibiId: UUID,
    val marquePibiActif: Boolean,
    val marquePibiCode: String,
    val marquePibiLibelle: String,
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
        val o: MarquePibi = other as MarquePibi
        if (this.marquePibiId != o.marquePibiId) {
            return false
        }
        if (this.marquePibiActif != o.marquePibiActif) {
            return false
        }
        if (this.marquePibiCode != o.marquePibiCode) {
            return false
        }
        if (this.marquePibiLibelle != o.marquePibiLibelle) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.marquePibiId.hashCode()
        result = prime * result + this.marquePibiActif.hashCode()
        result = prime * result + this.marquePibiCode.hashCode()
        result = prime * result + this.marquePibiLibelle.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("MarquePibi (")

        sb.append(marquePibiId)
        sb.append(", ").append(marquePibiActif)
        sb.append(", ").append(marquePibiCode)
        sb.append(", ").append(marquePibiLibelle)

        sb.append(")")
        return sb.toString()
    }
}
