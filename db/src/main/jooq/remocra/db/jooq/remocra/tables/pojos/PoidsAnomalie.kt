/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

import remocra.db.jooq.remocra.enums.TypeVisite
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
data class PoidsAnomalie(
    val poidsAnomalieId: UUID,
    val poidsAnomalieAnomalieId: UUID,
    val poidsAnomalieNatureId: UUID,
    val poidsAnomalieTypeVisite: Array<TypeVisite?>?,
    val poidsAnomalieValIndispoHbe: Int?,
    val poidsAnomalieValIndispoTerrestre: Int?,
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
        val o: PoidsAnomalie = other as PoidsAnomalie
        if (this.poidsAnomalieId != o.poidsAnomalieId) {
            return false
        }
        if (this.poidsAnomalieAnomalieId != o.poidsAnomalieAnomalieId) {
            return false
        }
        if (this.poidsAnomalieNatureId != o.poidsAnomalieNatureId) {
            return false
        }
        if (this.poidsAnomalieTypeVisite == null) {
            if (o.poidsAnomalieTypeVisite != null) {
                return false
            }
        } else if (!Arrays.deepEquals(this.poidsAnomalieTypeVisite, o.poidsAnomalieTypeVisite)) {
            return false
        }
        if (this.poidsAnomalieValIndispoHbe == null) {
            if (o.poidsAnomalieValIndispoHbe != null) {
                return false
            }
        } else if (this.poidsAnomalieValIndispoHbe != o.poidsAnomalieValIndispoHbe) {
            return false
        }
        if (this.poidsAnomalieValIndispoTerrestre == null) {
            if (o.poidsAnomalieValIndispoTerrestre != null) {
                return false
            }
        } else if (this.poidsAnomalieValIndispoTerrestre != o.poidsAnomalieValIndispoTerrestre) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.poidsAnomalieId.hashCode()
        result = prime * result + this.poidsAnomalieAnomalieId.hashCode()
        result = prime * result + this.poidsAnomalieNatureId.hashCode()
        result = prime * result + (if (this.poidsAnomalieTypeVisite == null) 0 else Arrays.deepHashCode(this.poidsAnomalieTypeVisite))
        result = prime * result + (if (this.poidsAnomalieValIndispoHbe == null) 0 else this.poidsAnomalieValIndispoHbe.hashCode())
        result = prime * result + (if (this.poidsAnomalieValIndispoTerrestre == null) 0 else this.poidsAnomalieValIndispoTerrestre.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("PoidsAnomalie (")

        sb.append(poidsAnomalieId)
        sb.append(", ").append(poidsAnomalieAnomalieId)
        sb.append(", ").append(poidsAnomalieNatureId)
        sb.append(", ").append(Arrays.deepToString(poidsAnomalieTypeVisite))
        sb.append(", ").append(poidsAnomalieValIndispoHbe)
        sb.append(", ").append(poidsAnomalieValIndispoTerrestre)

        sb.append(")")
        return sb.toString()
    }
}
