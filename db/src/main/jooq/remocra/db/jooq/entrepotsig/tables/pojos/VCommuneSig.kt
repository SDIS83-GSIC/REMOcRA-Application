/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.entrepotsig.tables.pojos

import org.locationtech.jts.geom.Geometry
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
data class VCommuneSig(
    val vCommuneSigId: UUID?,
    val vCommuneSigLibelle: String?,
    val vCommuneSigCodeInsee: String?,
    val vCommuneSigCodePostal: String?,
    val vCommuneSigGeometrie: Geometry?,
    val vCommuneSigPprif: Boolean?,
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
        val o: VCommuneSig = other as VCommuneSig
        if (this.vCommuneSigId == null) {
            if (o.vCommuneSigId != null) {
                return false
            }
        } else if (this.vCommuneSigId != o.vCommuneSigId) {
            return false
        }
        if (this.vCommuneSigLibelle == null) {
            if (o.vCommuneSigLibelle != null) {
                return false
            }
        } else if (this.vCommuneSigLibelle != o.vCommuneSigLibelle) {
            return false
        }
        if (this.vCommuneSigCodeInsee == null) {
            if (o.vCommuneSigCodeInsee != null) {
                return false
            }
        } else if (this.vCommuneSigCodeInsee != o.vCommuneSigCodeInsee) {
            return false
        }
        if (this.vCommuneSigCodePostal == null) {
            if (o.vCommuneSigCodePostal != null) {
                return false
            }
        } else if (this.vCommuneSigCodePostal != o.vCommuneSigCodePostal) {
            return false
        }
        if (this.vCommuneSigGeometrie == null) {
            if (o.vCommuneSigGeometrie != null) {
                return false
            }
        } else if (this.vCommuneSigGeometrie != o.vCommuneSigGeometrie) {
            return false
        }
        if (this.vCommuneSigPprif == null) {
            if (o.vCommuneSigPprif != null) {
                return false
            }
        } else if (this.vCommuneSigPprif != o.vCommuneSigPprif) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (if (this.vCommuneSigId == null) 0 else this.vCommuneSigId.hashCode())
        result = prime * result + (if (this.vCommuneSigLibelle == null) 0 else this.vCommuneSigLibelle.hashCode())
        result = prime * result + (if (this.vCommuneSigCodeInsee == null) 0 else this.vCommuneSigCodeInsee.hashCode())
        result = prime * result + (if (this.vCommuneSigCodePostal == null) 0 else this.vCommuneSigCodePostal.hashCode())
        result = prime * result + (if (this.vCommuneSigGeometrie == null) 0 else this.vCommuneSigGeometrie.hashCode())
        result = prime * result + (if (this.vCommuneSigPprif == null) 0 else this.vCommuneSigPprif.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("VCommuneSig (")

        sb.append(vCommuneSigId)
        sb.append(", ").append(vCommuneSigLibelle)
        sb.append(", ").append(vCommuneSigCodeInsee)
        sb.append(", ").append(vCommuneSigCodePostal)
        sb.append(", ").append(vCommuneSigGeometrie)
        sb.append(", ").append(vCommuneSigPprif)

        sb.append(")")
        return sb.toString()
    }
}
