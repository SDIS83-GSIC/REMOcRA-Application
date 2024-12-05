/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

import remocra.db.jooq.remocra.enums.TypeCivilite
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
data class OldebProprietaire(
    val oldebProprietaireId: UUID,
    val oldebProprietaireOrganisme: Boolean,
    val oldebProprietaireRaisonSociale: String?,
    val oldebProprietaireCivilite: TypeCivilite,
    val oldebProprietaireNom: String,
    val oldebProprietairePrenom: String,
    val oldebProprietaireTelephone: String?,
    val oldebProprietaireEmail: String?,
    val oldebProprietaireNumVoie: String?,
    val oldebProprietaireVoie: String?,
    val oldebProprietaireLieuDit: String?,
    val oldebProprietaireCodePostal: String,
    val oldebProprietaireVille: String,
    val oldebProprietairePays: String,
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
        val o: OldebProprietaire = other as OldebProprietaire
        if (this.oldebProprietaireId != o.oldebProprietaireId) {
            return false
        }
        if (this.oldebProprietaireOrganisme != o.oldebProprietaireOrganisme) {
            return false
        }
        if (this.oldebProprietaireRaisonSociale == null) {
            if (o.oldebProprietaireRaisonSociale != null) {
                return false
            }
        } else if (this.oldebProprietaireRaisonSociale != o.oldebProprietaireRaisonSociale) {
            return false
        }
        if (this.oldebProprietaireCivilite != o.oldebProprietaireCivilite) {
            return false
        }
        if (this.oldebProprietaireNom != o.oldebProprietaireNom) {
            return false
        }
        if (this.oldebProprietairePrenom != o.oldebProprietairePrenom) {
            return false
        }
        if (this.oldebProprietaireTelephone == null) {
            if (o.oldebProprietaireTelephone != null) {
                return false
            }
        } else if (this.oldebProprietaireTelephone != o.oldebProprietaireTelephone) {
            return false
        }
        if (this.oldebProprietaireEmail == null) {
            if (o.oldebProprietaireEmail != null) {
                return false
            }
        } else if (this.oldebProprietaireEmail != o.oldebProprietaireEmail) {
            return false
        }
        if (this.oldebProprietaireNumVoie == null) {
            if (o.oldebProprietaireNumVoie != null) {
                return false
            }
        } else if (this.oldebProprietaireNumVoie != o.oldebProprietaireNumVoie) {
            return false
        }
        if (this.oldebProprietaireVoie == null) {
            if (o.oldebProprietaireVoie != null) {
                return false
            }
        } else if (this.oldebProprietaireVoie != o.oldebProprietaireVoie) {
            return false
        }
        if (this.oldebProprietaireLieuDit == null) {
            if (o.oldebProprietaireLieuDit != null) {
                return false
            }
        } else if (this.oldebProprietaireLieuDit != o.oldebProprietaireLieuDit) {
            return false
        }
        if (this.oldebProprietaireCodePostal != o.oldebProprietaireCodePostal) {
            return false
        }
        if (this.oldebProprietaireVille != o.oldebProprietaireVille) {
            return false
        }
        if (this.oldebProprietairePays != o.oldebProprietairePays) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.oldebProprietaireId.hashCode()
        result = prime * result + this.oldebProprietaireOrganisme.hashCode()
        result = prime * result + (if (this.oldebProprietaireRaisonSociale == null) 0 else this.oldebProprietaireRaisonSociale.hashCode())
        result = prime * result + this.oldebProprietaireCivilite.hashCode()
        result = prime * result + this.oldebProprietaireNom.hashCode()
        result = prime * result + this.oldebProprietairePrenom.hashCode()
        result = prime * result + (if (this.oldebProprietaireTelephone == null) 0 else this.oldebProprietaireTelephone.hashCode())
        result = prime * result + (if (this.oldebProprietaireEmail == null) 0 else this.oldebProprietaireEmail.hashCode())
        result = prime * result + (if (this.oldebProprietaireNumVoie == null) 0 else this.oldebProprietaireNumVoie.hashCode())
        result = prime * result + (if (this.oldebProprietaireVoie == null) 0 else this.oldebProprietaireVoie.hashCode())
        result = prime * result + (if (this.oldebProprietaireLieuDit == null) 0 else this.oldebProprietaireLieuDit.hashCode())
        result = prime * result + this.oldebProprietaireCodePostal.hashCode()
        result = prime * result + this.oldebProprietaireVille.hashCode()
        result = prime * result + this.oldebProprietairePays.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("OldebProprietaire (")

        sb.append(oldebProprietaireId)
        sb.append(", ").append(oldebProprietaireOrganisme)
        sb.append(", ").append(oldebProprietaireRaisonSociale)
        sb.append(", ").append(oldebProprietaireCivilite)
        sb.append(", ").append(oldebProprietaireNom)
        sb.append(", ").append(oldebProprietairePrenom)
        sb.append(", ").append(oldebProprietaireTelephone)
        sb.append(", ").append(oldebProprietaireEmail)
        sb.append(", ").append(oldebProprietaireNumVoie)
        sb.append(", ").append(oldebProprietaireVoie)
        sb.append(", ").append(oldebProprietaireLieuDit)
        sb.append(", ").append(oldebProprietaireCodePostal)
        sb.append(", ").append(oldebProprietaireVille)
        sb.append(", ").append(oldebProprietairePays)

        sb.append(")")
        return sb.toString()
    }
}
