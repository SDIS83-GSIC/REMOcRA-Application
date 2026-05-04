package remocra.data

import remocra.db.jooq.remocra.enums.Droit
import java.util.UUID

data class GroupeFonctionnalitesData(
    val groupeFonctionnalitesId: UUID = UUID.randomUUID(),
    val groupeFonctionnalitesCode: String,
    val groupeFonctionnalitesLibelle: String,
    val groupeFonctionnalitesActif: Boolean,
    val groupeFonctionnalitesDroits: Array<Droit?> = arrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroupeFonctionnalitesData

        if (groupeFonctionnalitesActif != other.groupeFonctionnalitesActif) return false
        if (groupeFonctionnalitesId != other.groupeFonctionnalitesId) return false
        if (groupeFonctionnalitesCode != other.groupeFonctionnalitesCode) return false
        if (groupeFonctionnalitesLibelle != other.groupeFonctionnalitesLibelle) return false
        if (!groupeFonctionnalitesDroits.contentEquals(other.groupeFonctionnalitesDroits)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupeFonctionnalitesActif.hashCode()
        result = 31 * result + groupeFonctionnalitesId.hashCode()
        result = 31 * result + groupeFonctionnalitesCode.hashCode()
        result = 31 * result + groupeFonctionnalitesLibelle.hashCode()
        result = 31 * result + groupeFonctionnalitesDroits.contentHashCode()
        return result
    }
}
