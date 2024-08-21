/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

import remocra.db.jooq.remocra.enums.Droit
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
        "jOOQ version:3.19.3",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
data class ProfilDroit(
    val profilDroitId: UUID,
    val profilDroitCode: String,
    val profilDroitLibelle: String,
    val profilDroitDroits: Array<Droit?>,
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
        val o: ProfilDroit = other as ProfilDroit
        if (this.profilDroitId != o.profilDroitId) {
            return false
        }
        if (this.profilDroitCode != o.profilDroitCode) {
            return false
        }
        if (this.profilDroitLibelle != o.profilDroitLibelle) {
            return false
        }
        if (!Arrays.deepEquals(this.profilDroitDroits, o.profilDroitDroits)) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.profilDroitId.hashCode()
        result = prime * result + this.profilDroitCode.hashCode()
        result = prime * result + this.profilDroitLibelle.hashCode()
        result = prime * result + Arrays.deepHashCode(this.profilDroitDroits)
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("ProfilDroit (")

        sb.append(profilDroitId)
        sb.append(", ").append(profilDroitCode)
        sb.append(", ").append(profilDroitLibelle)
        sb.append(", ").append(Arrays.deepToString(profilDroitDroits))

        sb.append(")")
        return sb.toString()
    }
}
