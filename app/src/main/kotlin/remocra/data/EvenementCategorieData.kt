package remocra.data

import remocra.db.jooq.remocra.tables.pojos.TypeCrise
import java.util.UUID

data class EvenementCategorieData(
    val evenementCategorieId: UUID,
    val evenementCategorieCode: String,
    val evenementCategorieLibelle: String,
    val evenementCategorieActif: Boolean,
    val evenementCategorieEstUtilisee: Boolean,
)

data class EvenementCategorieWithTypeCriseData(
    val evenementCategorieId: UUID = UUID.randomUUID(),
    val evenementCategorieCode: String,
    val evenementCategorieLibelle: String,
    val evenementCategorieActif: Boolean,
    val evenementCategorieTypeCrise: Collection<TypeCrise>,
)
