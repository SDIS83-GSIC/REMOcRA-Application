/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.EvenementStatut
import remocra.db.jooq.remocra.enums.EvenementStatutMode
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
data class Evenement(
    val evenementId: UUID,
    val evenementTypeCriseCategorieId: UUID?,
    val evenementLibelle: String,
    val evenementDescription: String?,
    val evenementOrigine: String?,
    val evenementDateConstat: ZonedDateTime,
    val evenementImportance: Int?,
    val evenementTags: String?,
    val evenementIsClosed: Boolean?,
    val evenementDateCloture: ZonedDateTime?,
    val evenementGeometrie: Geometry?,
    val evenementCriseId: UUID?,
    val evenementStatut: EvenementStatut?,
    val utilisateurId: UUID?,
    val evenementStatutMode: EvenementStatutMode?,
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
        val o: Evenement = other as Evenement
        if (this.evenementId != o.evenementId) {
            return false
        }
        if (this.evenementTypeCriseCategorieId == null) {
            if (o.evenementTypeCriseCategorieId != null) {
                return false
            }
        } else if (this.evenementTypeCriseCategorieId != o.evenementTypeCriseCategorieId) {
            return false
        }
        if (this.evenementLibelle != o.evenementLibelle) {
            return false
        }
        if (this.evenementDescription == null) {
            if (o.evenementDescription != null) {
                return false
            }
        } else if (this.evenementDescription != o.evenementDescription) {
            return false
        }
        if (this.evenementOrigine == null) {
            if (o.evenementOrigine != null) {
                return false
            }
        } else if (this.evenementOrigine != o.evenementOrigine) {
            return false
        }
        if (this.evenementDateConstat != o.evenementDateConstat) {
            return false
        }
        if (this.evenementImportance == null) {
            if (o.evenementImportance != null) {
                return false
            }
        } else if (this.evenementImportance != o.evenementImportance) {
            return false
        }
        if (this.evenementTags == null) {
            if (o.evenementTags != null) {
                return false
            }
        } else if (this.evenementTags != o.evenementTags) {
            return false
        }
        if (this.evenementIsClosed == null) {
            if (o.evenementIsClosed != null) {
                return false
            }
        } else if (this.evenementIsClosed != o.evenementIsClosed) {
            return false
        }
        if (this.evenementDateCloture == null) {
            if (o.evenementDateCloture != null) {
                return false
            }
        } else if (this.evenementDateCloture != o.evenementDateCloture) {
            return false
        }
        if (this.evenementGeometrie == null) {
            if (o.evenementGeometrie != null) {
                return false
            }
        } else if (this.evenementGeometrie != o.evenementGeometrie) {
            return false
        }
        if (this.evenementCriseId == null) {
            if (o.evenementCriseId != null) {
                return false
            }
        } else if (this.evenementCriseId != o.evenementCriseId) {
            return false
        }
        if (this.evenementStatut == null) {
            if (o.evenementStatut != null) {
                return false
            }
        } else if (this.evenementStatut != o.evenementStatut) {
            return false
        }
        if (this.utilisateurId == null) {
            if (o.utilisateurId != null) {
                return false
            }
        } else if (this.utilisateurId != o.utilisateurId) {
            return false
        }
        if (this.evenementStatutMode == null) {
            if (o.evenementStatutMode != null) {
                return false
            }
        } else if (this.evenementStatutMode != o.evenementStatutMode) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.evenementId.hashCode()
        result = prime * result + (if (this.evenementTypeCriseCategorieId == null) 0 else this.evenementTypeCriseCategorieId.hashCode())
        result = prime * result + this.evenementLibelle.hashCode()
        result = prime * result + (if (this.evenementDescription == null) 0 else this.evenementDescription.hashCode())
        result = prime * result + (if (this.evenementOrigine == null) 0 else this.evenementOrigine.hashCode())
        result = prime * result + this.evenementDateConstat.hashCode()
        result = prime * result + (if (this.evenementImportance == null) 0 else this.evenementImportance.hashCode())
        result = prime * result + (if (this.evenementTags == null) 0 else this.evenementTags.hashCode())
        result = prime * result + (if (this.evenementIsClosed == null) 0 else this.evenementIsClosed.hashCode())
        result = prime * result + (if (this.evenementDateCloture == null) 0 else this.evenementDateCloture.hashCode())
        result = prime * result + (if (this.evenementGeometrie == null) 0 else this.evenementGeometrie.hashCode())
        result = prime * result + (if (this.evenementCriseId == null) 0 else this.evenementCriseId.hashCode())
        result = prime * result + (if (this.evenementStatut == null) 0 else this.evenementStatut.hashCode())
        result = prime * result + (if (this.utilisateurId == null) 0 else this.utilisateurId.hashCode())
        result = prime * result + (if (this.evenementStatutMode == null) 0 else this.evenementStatutMode.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Evenement (")

        sb.append(evenementId)
        sb.append(", ").append(evenementTypeCriseCategorieId)
        sb.append(", ").append(evenementLibelle)
        sb.append(", ").append(evenementDescription)
        sb.append(", ").append(evenementOrigine)
        sb.append(", ").append(evenementDateConstat)
        sb.append(", ").append(evenementImportance)
        sb.append(", ").append(evenementTags)
        sb.append(", ").append(evenementIsClosed)
        sb.append(", ").append(evenementDateCloture)
        sb.append(", ").append(evenementGeometrie)
        sb.append(", ").append(evenementCriseId)
        sb.append(", ").append(evenementStatut)
        sb.append(", ").append(utilisateurId)
        sb.append(", ").append(evenementStatutMode)

        sb.append(")")
        return sb.toString()
    }
}
