/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

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
data class IndisponibiliteTemporaire(
    val indisponibiliteTemporaireId: UUID,
    val indisponibiliteTemporaireDateDebut: ZonedDateTime,
    val indisponibiliteTemporaireDateFin: ZonedDateTime?,
    val indisponibiliteTemporaireMotif: String,
    val indisponibiliteTemporaireObservation: String?,
    val indisponibiliteTemporaireBasculeAutoIndisponible: Boolean,
    val indisponibiliteTemporaireBasculeAutoDisponible: Boolean,
    val indisponibiliteTemporaireMailAvantIndisponibilite: Boolean,
    val indisponibiliteTemporaireMailApresIndisponibilite: Boolean,
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
        val o: IndisponibiliteTemporaire = other as IndisponibiliteTemporaire
        if (this.indisponibiliteTemporaireId != o.indisponibiliteTemporaireId) {
            return false
        }
        if (this.indisponibiliteTemporaireDateDebut != o.indisponibiliteTemporaireDateDebut) {
            return false
        }
        if (this.indisponibiliteTemporaireDateFin == null) {
            if (o.indisponibiliteTemporaireDateFin != null) {
                return false
            }
        } else if (this.indisponibiliteTemporaireDateFin != o.indisponibiliteTemporaireDateFin) {
            return false
        }
        if (this.indisponibiliteTemporaireMotif != o.indisponibiliteTemporaireMotif) {
            return false
        }
        if (this.indisponibiliteTemporaireObservation == null) {
            if (o.indisponibiliteTemporaireObservation != null) {
                return false
            }
        } else if (this.indisponibiliteTemporaireObservation != o.indisponibiliteTemporaireObservation) {
            return false
        }
        if (this.indisponibiliteTemporaireBasculeAutoIndisponible != o.indisponibiliteTemporaireBasculeAutoIndisponible) {
            return false
        }
        if (this.indisponibiliteTemporaireBasculeAutoDisponible != o.indisponibiliteTemporaireBasculeAutoDisponible) {
            return false
        }
        if (this.indisponibiliteTemporaireMailAvantIndisponibilite != o.indisponibiliteTemporaireMailAvantIndisponibilite) {
            return false
        }
        if (this.indisponibiliteTemporaireMailApresIndisponibilite != o.indisponibiliteTemporaireMailApresIndisponibilite) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.indisponibiliteTemporaireId.hashCode()
        result = prime * result + this.indisponibiliteTemporaireDateDebut.hashCode()
        result = prime * result + (if (this.indisponibiliteTemporaireDateFin == null) 0 else this.indisponibiliteTemporaireDateFin.hashCode())
        result = prime * result + this.indisponibiliteTemporaireMotif.hashCode()
        result = prime * result + (if (this.indisponibiliteTemporaireObservation == null) 0 else this.indisponibiliteTemporaireObservation.hashCode())
        result = prime * result + this.indisponibiliteTemporaireBasculeAutoIndisponible.hashCode()
        result = prime * result + this.indisponibiliteTemporaireBasculeAutoDisponible.hashCode()
        result = prime * result + this.indisponibiliteTemporaireMailAvantIndisponibilite.hashCode()
        result = prime * result + this.indisponibiliteTemporaireMailApresIndisponibilite.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("IndisponibiliteTemporaire (")

        sb.append(indisponibiliteTemporaireId)
        sb.append(", ").append(indisponibiliteTemporaireDateDebut)
        sb.append(", ").append(indisponibiliteTemporaireDateFin)
        sb.append(", ").append(indisponibiliteTemporaireMotif)
        sb.append(", ").append(indisponibiliteTemporaireObservation)
        sb.append(", ").append(indisponibiliteTemporaireBasculeAutoIndisponible)
        sb.append(", ").append(indisponibiliteTemporaireBasculeAutoDisponible)
        sb.append(", ").append(indisponibiliteTemporaireMailAvantIndisponibilite)
        sb.append(", ").append(indisponibiliteTemporaireMailApresIndisponibilite)

        sb.append(")")
        return sb.toString()
    }
}
