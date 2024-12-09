/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.incoming.tables.pojos

import remocra.db.jooq.remocra.enums.TypeVisite
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
        "jOOQ version:3.19.11",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
data class Visite(
    val visiteId: UUID,
    val visitePeiId: UUID,
    val visiteTourneeId: UUID,
    val visiteDate: ZonedDateTime,
    val visiteTypeVisite: TypeVisite,
    val visiteAgent1: String?,
    val visiteAgent2: String?,
    val visiteObservation: String?,
    val hasAnomalieChanges: Boolean?,
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
        val o: Visite = other as Visite
        if (this.visiteId != o.visiteId) {
            return false
        }
        if (this.visitePeiId != o.visitePeiId) {
            return false
        }
        if (this.visiteTourneeId != o.visiteTourneeId) {
            return false
        }
        if (this.visiteDate != o.visiteDate) {
            return false
        }
        if (this.visiteTypeVisite != o.visiteTypeVisite) {
            return false
        }
        if (this.visiteAgent1 == null) {
            if (o.visiteAgent1 != null) {
                return false
            }
        } else if (this.visiteAgent1 != o.visiteAgent1) {
            return false
        }
        if (this.visiteAgent2 == null) {
            if (o.visiteAgent2 != null) {
                return false
            }
        } else if (this.visiteAgent2 != o.visiteAgent2) {
            return false
        }
        if (this.visiteObservation == null) {
            if (o.visiteObservation != null) {
                return false
            }
        } else if (this.visiteObservation != o.visiteObservation) {
            return false
        }
        if (this.hasAnomalieChanges == null) {
            if (o.hasAnomalieChanges != null) {
                return false
            }
        } else if (this.hasAnomalieChanges != o.hasAnomalieChanges) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.visiteId.hashCode()
        result = prime * result + this.visitePeiId.hashCode()
        result = prime * result + this.visiteTourneeId.hashCode()
        result = prime * result + this.visiteDate.hashCode()
        result = prime * result + this.visiteTypeVisite.hashCode()
        result = prime * result + (if (this.visiteAgent1 == null) 0 else this.visiteAgent1.hashCode())
        result = prime * result + (if (this.visiteAgent2 == null) 0 else this.visiteAgent2.hashCode())
        result = prime * result + (if (this.visiteObservation == null) 0 else this.visiteObservation.hashCode())
        result = prime * result + (if (this.hasAnomalieChanges == null) 0 else this.hasAnomalieChanges.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Visite (")

        sb.append(visiteId)
        sb.append(", ").append(visitePeiId)
        sb.append(", ").append(visiteTourneeId)
        sb.append(", ").append(visiteDate)
        sb.append(", ").append(visiteTypeVisite)
        sb.append(", ").append(visiteAgent1)
        sb.append(", ").append(visiteAgent2)
        sb.append(", ").append(visiteObservation)
        sb.append(", ").append(hasAnomalieChanges)

        sb.append(")")
        return sb.toString()
    }
}
