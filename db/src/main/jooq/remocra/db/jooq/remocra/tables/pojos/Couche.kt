/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

import java.io.Serializable
import java.util.Arrays
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
data class Couche(
    val coucheId: UUID,
    val coucheCode: String,
    val coucheGroupeCoucheId: UUID,
    val coucheOrdre: Int,
    val coucheLibelle: String,
    val coucheSource: String,
    val coucheProjection: String,
    val coucheUrl: String,
    val coucheNom: String,
    val coucheFormat: String,
    val couchePublic: Boolean,
    val coucheActive: Boolean,
    val coucheIcone: ByteArray?,
    val coucheLegende: ByteArray?,
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
        val o: Couche = other as Couche
        if (this.coucheId != o.coucheId) {
            return false
        }
        if (this.coucheCode != o.coucheCode) {
            return false
        }
        if (this.coucheGroupeCoucheId != o.coucheGroupeCoucheId) {
            return false
        }
        if (this.coucheOrdre != o.coucheOrdre) {
            return false
        }
        if (this.coucheLibelle != o.coucheLibelle) {
            return false
        }
        if (this.coucheSource != o.coucheSource) {
            return false
        }
        if (this.coucheProjection != o.coucheProjection) {
            return false
        }
        if (this.coucheUrl != o.coucheUrl) {
            return false
        }
        if (this.coucheNom != o.coucheNom) {
            return false
        }
        if (this.coucheFormat != o.coucheFormat) {
            return false
        }
        if (this.couchePublic != o.couchePublic) {
            return false
        }
        if (this.coucheActive != o.coucheActive) {
            return false
        }
        if (this.coucheIcone == null) {
            if (o.coucheIcone != null) {
                return false
            }
        } else if (!Arrays.equals(this.coucheIcone, o.coucheIcone)) {
            return false
        }
        if (this.coucheLegende == null) {
            if (o.coucheLegende != null) {
                return false
            }
        } else if (!Arrays.equals(this.coucheLegende, o.coucheLegende)) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.coucheId.hashCode()
        result = prime * result + this.coucheCode.hashCode()
        result = prime * result + this.coucheGroupeCoucheId.hashCode()
        result = prime * result + this.coucheOrdre.hashCode()
        result = prime * result + this.coucheLibelle.hashCode()
        result = prime * result + this.coucheSource.hashCode()
        result = prime * result + this.coucheProjection.hashCode()
        result = prime * result + this.coucheUrl.hashCode()
        result = prime * result + this.coucheNom.hashCode()
        result = prime * result + this.coucheFormat.hashCode()
        result = prime * result + this.couchePublic.hashCode()
        result = prime * result + this.coucheActive.hashCode()
        result = prime * result + (if (this.coucheIcone == null) 0 else Arrays.hashCode(this.coucheIcone))
        result = prime * result + (if (this.coucheLegende == null) 0 else Arrays.hashCode(this.coucheLegende))
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Couche (")

        sb.append(coucheId)
        sb.append(", ").append(coucheCode)
        sb.append(", ").append(coucheGroupeCoucheId)
        sb.append(", ").append(coucheOrdre)
        sb.append(", ").append(coucheLibelle)
        sb.append(", ").append(coucheSource)
        sb.append(", ").append(coucheProjection)
        sb.append(", ").append(coucheUrl)
        sb.append(", ").append(coucheNom)
        sb.append(", ").append(coucheFormat)
        sb.append(", ").append(couchePublic)
        sb.append(", ").append(coucheActive)
        sb.append(", ").append("[binary...]")
        sb.append(", ").append("[binary...]")

        sb.append(")")
        return sb.toString()
    }
}
