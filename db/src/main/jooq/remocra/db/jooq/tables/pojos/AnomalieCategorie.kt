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
data class AnomalieCategorie(
    val anomalieCategorieId: UUID,
    val anomalieCategorieCode: String,
    val anomalieCategorieLibelle: String,
    val anomalieCategorieActif: Boolean,
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
        val o: AnomalieCategorie = other as AnomalieCategorie
        if (this.anomalieCategorieId != o.anomalieCategorieId) {
            return false
        }
        if (this.anomalieCategorieCode != o.anomalieCategorieCode) {
            return false
        }
        if (this.anomalieCategorieLibelle != o.anomalieCategorieLibelle) {
            return false
        }
        if (this.anomalieCategorieActif != o.anomalieCategorieActif) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.anomalieCategorieId.hashCode()
        result = prime * result + this.anomalieCategorieCode.hashCode()
        result = prime * result + this.anomalieCategorieLibelle.hashCode()
        result = prime * result + this.anomalieCategorieActif.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("AnomalieCategorie (")

        sb.append(anomalieCategorieId)
        sb.append(", ").append(anomalieCategorieCode)
        sb.append(", ").append(anomalieCategorieLibelle)
        sb.append(", ").append(anomalieCategorieActif)

        sb.append(")")
        return sb.toString()
    }
}
