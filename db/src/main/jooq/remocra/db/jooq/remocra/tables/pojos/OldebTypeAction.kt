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
data class OldebTypeAction(
    val oldebTypeActionId: UUID,
    val oldebTypeActionActif: Boolean,
    val oldebTypeActionCode: String,
    val oldebTypeActionLibelle: String,
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
        val o: OldebTypeAction = other as OldebTypeAction
        if (this.oldebTypeActionId != o.oldebTypeActionId) {
            return false
        }
        if (this.oldebTypeActionActif != o.oldebTypeActionActif) {
            return false
        }
        if (this.oldebTypeActionCode != o.oldebTypeActionCode) {
            return false
        }
        if (this.oldebTypeActionLibelle != o.oldebTypeActionLibelle) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.oldebTypeActionId.hashCode()
        result = prime * result + this.oldebTypeActionActif.hashCode()
        result = prime * result + this.oldebTypeActionCode.hashCode()
        result = prime * result + this.oldebTypeActionLibelle.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("OldebTypeAction (")

        sb.append(oldebTypeActionId)
        sb.append(", ").append(oldebTypeActionActif)
        sb.append(", ").append(oldebTypeActionCode)
        sb.append(", ").append(oldebTypeActionLibelle)

        sb.append(")")
        return sb.toString()
    }
}
