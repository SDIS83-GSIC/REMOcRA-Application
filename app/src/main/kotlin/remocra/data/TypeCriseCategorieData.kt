package remocra.data

import remocra.db.jooq.remocra.enums.TypeGeometry
import java.util.UUID

data class TypeCriseCategorieData(
    val evenementSousCategorieId: UUID,
    val evenementSousCategorieCode: String,
    val evenementSousCategorieLibelle: String,
    val evenementSousCategorieTypeGeometrie: TypeGeometry,
    val criseCategorieId: UUID,
    val criseCategorieLibelle: String,
)
