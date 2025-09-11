package remocra.data

import remocra.db.jooq.remocra.enums.Droit
import java.util.UUID

data class GroupeFonctionnalitesData(
    val groupeFonctionnalitesId: UUID = UUID.randomUUID(),
    val groupeFonctionnalitesCode: String,
    val groupeFonctionnalitesLibelle: String,
    val groupeFonctionnalitesActif: Boolean,
    val groupeFonctionnalitesDroits: Array<Droit?> = arrayOf(),
)
