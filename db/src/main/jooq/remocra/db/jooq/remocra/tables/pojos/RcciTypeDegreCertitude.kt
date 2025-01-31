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
data class RcciTypeDegreCertitude(
    val rcciTypeDegreCertitudeId: UUID,
    val rcciTypeDegreCertitudeActif: Boolean,
    val rcciTypeDegreCertitudeCode: String,
    val rcciTypeDegreCertitudeLibelle: String,
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
        val o: RcciTypeDegreCertitude = other as RcciTypeDegreCertitude
        if (this.rcciTypeDegreCertitudeId != o.rcciTypeDegreCertitudeId) {
            return false
        }
        if (this.rcciTypeDegreCertitudeActif != o.rcciTypeDegreCertitudeActif) {
            return false
        }
        if (this.rcciTypeDegreCertitudeCode != o.rcciTypeDegreCertitudeCode) {
            return false
        }
        if (this.rcciTypeDegreCertitudeLibelle != o.rcciTypeDegreCertitudeLibelle) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.rcciTypeDegreCertitudeId.hashCode()
        result = prime * result + this.rcciTypeDegreCertitudeActif.hashCode()
        result = prime * result + this.rcciTypeDegreCertitudeCode.hashCode()
        result = prime * result + this.rcciTypeDegreCertitudeLibelle.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("RcciTypeDegreCertitude (")

        sb.append(rcciTypeDegreCertitudeId)
        sb.append(", ").append(rcciTypeDegreCertitudeActif)
        sb.append(", ").append(rcciTypeDegreCertitudeCode)
        sb.append(", ").append(rcciTypeDegreCertitudeLibelle)

        sb.append(")")
        return sb.toString()
    }
}
