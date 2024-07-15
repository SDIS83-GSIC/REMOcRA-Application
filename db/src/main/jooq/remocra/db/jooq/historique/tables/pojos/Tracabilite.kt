/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.historique.tables.pojos

import org.jooq.JSONB
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import java.io.Serializable
import java.time.ZonedDateTime
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
data class Tracabilite(
    val tracabiliteId: UUID,
    val tracabiliteTypeOperation: TypeOperation,
    val tracabiliteDate: ZonedDateTime,
    val tracabiliteObjetId: UUID,
    val tracabiliteTypeObjet: TypeObjet,
    val tracabiliteObjetData: JSONB,
    val tracabiliteAuteurId: UUID,
    val tracabiliteAuteurData: JSONB,
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
        val o: Tracabilite = other as Tracabilite
        if (this.tracabiliteId != o.tracabiliteId) {
            return false
        }
        if (this.tracabiliteTypeOperation != o.tracabiliteTypeOperation) {
            return false
        }
        if (this.tracabiliteDate != o.tracabiliteDate) {
            return false
        }
        if (this.tracabiliteObjetId != o.tracabiliteObjetId) {
            return false
        }
        if (this.tracabiliteTypeObjet != o.tracabiliteTypeObjet) {
            return false
        }
        if (this.tracabiliteObjetData != o.tracabiliteObjetData) {
            return false
        }
        if (this.tracabiliteAuteurId != o.tracabiliteAuteurId) {
            return false
        }
        if (this.tracabiliteAuteurData != o.tracabiliteAuteurData) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.tracabiliteId.hashCode()
        result = prime * result + this.tracabiliteTypeOperation.hashCode()
        result = prime * result + this.tracabiliteDate.hashCode()
        result = prime * result + this.tracabiliteObjetId.hashCode()
        result = prime * result + this.tracabiliteTypeObjet.hashCode()
        result = prime * result + this.tracabiliteObjetData.hashCode()
        result = prime * result + this.tracabiliteAuteurId.hashCode()
        result = prime * result + this.tracabiliteAuteurData.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Tracabilite (")

        sb.append(tracabiliteId)
        sb.append(", ").append(tracabiliteTypeOperation)
        sb.append(", ").append(tracabiliteDate)
        sb.append(", ").append(tracabiliteObjetId)
        sb.append(", ").append(tracabiliteTypeObjet)
        sb.append(", ").append(tracabiliteObjetData)
        sb.append(", ").append(tracabiliteAuteurId)
        sb.append(", ").append(tracabiliteAuteurData)

        sb.append(")")
        return sb.toString()
    }
}
