package remocra.data

import remocra.db.jooq.remocra.enums.TypeGeometry
import java.util.UUID

data class TypeCriseCategorieData(
    val typeCriseCategorieId: UUID,
    val typeCriseCategorieCode: String,
    val typeCriseCategorieLibelle: String,
    val typeCriseCategorieTypeGeometrie: TypeGeometry,
    val criseCategorieId: UUID,
    val criseCategorieLibelle: String,
)
