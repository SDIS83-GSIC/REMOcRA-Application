/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

import remocra.db.jooq.remocra.enums.DroitApi
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
data class TypeOrganisme(
    val typeOrganismeId: UUID,
    val typeOrganismeActif: Boolean,
    val typeOrganismeProtected: Boolean,
    val typeOrganismeCode: String,
    val typeOrganismeLibelle: String,
    val typeOrganismeParentId: UUID?,
    val typeOrganismeDroitApi: Array<DroitApi?>?,
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
        val o: TypeOrganisme = other as TypeOrganisme
        if (this.typeOrganismeId != o.typeOrganismeId) {
            return false
        }
        if (this.typeOrganismeActif != o.typeOrganismeActif) {
            return false
        }
        if (this.typeOrganismeProtected != o.typeOrganismeProtected) {
            return false
        }
        if (this.typeOrganismeCode != o.typeOrganismeCode) {
            return false
        }
        if (this.typeOrganismeLibelle != o.typeOrganismeLibelle) {
            return false
        }
        if (this.typeOrganismeParentId == null) {
            if (o.typeOrganismeParentId != null) {
                return false
            }
        } else if (this.typeOrganismeParentId != o.typeOrganismeParentId) {
            return false
        }
        if (this.typeOrganismeDroitApi == null) {
            if (o.typeOrganismeDroitApi != null) {
                return false
            }
        } else if (!Arrays.deepEquals(this.typeOrganismeDroitApi, o.typeOrganismeDroitApi)) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.typeOrganismeId.hashCode()
        result = prime * result + this.typeOrganismeActif.hashCode()
        result = prime * result + this.typeOrganismeProtected.hashCode()
        result = prime * result + this.typeOrganismeCode.hashCode()
        result = prime * result + this.typeOrganismeLibelle.hashCode()
        result = prime * result + (if (this.typeOrganismeParentId == null) 0 else this.typeOrganismeParentId.hashCode())
        result = prime * result + (if (this.typeOrganismeDroitApi == null) 0 else Arrays.deepHashCode(this.typeOrganismeDroitApi))
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("TypeOrganisme (")

        sb.append(typeOrganismeId)
        sb.append(", ").append(typeOrganismeActif)
        sb.append(", ").append(typeOrganismeProtected)
        sb.append(", ").append(typeOrganismeCode)
        sb.append(", ").append(typeOrganismeLibelle)
        sb.append(", ").append(typeOrganismeParentId)
        sb.append(", ").append(Arrays.deepToString(typeOrganismeDroitApi))

        sb.append(")")
        return sb.toString()
    }
}
