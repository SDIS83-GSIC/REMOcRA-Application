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
data class OldebTypeAvis(
    val oldebTypeAvisId: UUID,
    val oldebTypeAvisActif: Boolean,
    val oldebTypeAvisCode: String,
    val oldebTypeAvisLibelle: String,
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
        val o: OldebTypeAvis = other as OldebTypeAvis
        if (this.oldebTypeAvisId != o.oldebTypeAvisId) {
            return false
        }
        if (this.oldebTypeAvisActif != o.oldebTypeAvisActif) {
            return false
        }
        if (this.oldebTypeAvisCode != o.oldebTypeAvisCode) {
            return false
        }
        if (this.oldebTypeAvisLibelle != o.oldebTypeAvisLibelle) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.oldebTypeAvisId.hashCode()
        result = prime * result + this.oldebTypeAvisActif.hashCode()
        result = prime * result + this.oldebTypeAvisCode.hashCode()
        result = prime * result + this.oldebTypeAvisLibelle.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("OldebTypeAvis (")

        sb.append(oldebTypeAvisId)
        sb.append(", ").append(oldebTypeAvisActif)
        sb.append(", ").append(oldebTypeAvisCode)
        sb.append(", ").append(oldebTypeAvisLibelle)

        sb.append(")")
        return sb.toString()
    }
}
