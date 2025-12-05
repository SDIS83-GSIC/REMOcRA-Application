package remocra.data

import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.enums.TypeParametreEvenementComplement
import remocra.utils.BuildDynamicForm
import java.util.UUID

data class EvenementSousCategoryData(
    val evenementSousCategorieId: UUID,
    val evenementSousCategorieCode: String,
    val evenementSousCategorieLibelle: String,
    val evenementSousCategorieTypeGeometrie: TypeGeometry,
    val criseCategorieId: UUID,
    val criseCategorieLibelle: String,
)

data class EvenementSousCategorieWithComplementData(
    val evenementSousCategorieId: UUID = UUID.randomUUID(),
    val evenementSousCategorieCode: String,
    val evenementSousCategorieLibelle: String,
    val evenementSousCategorieTypeGeometrie: TypeGeometry?,
    val evenementSousCategorieEvenementCategorieId: UUID?,
    val evenementSousCategorieActif: Boolean,
    val evenementSousCategorieComplement: Collection<SousCategorieComplement>,
)

data class SousCategorieComplement(
    val evenementSousCategorieId: UUID = UUID.randomUUID(),
    val sousCategorieComplementId: UUID = UUID.randomUUID(),
    val sousCategorieComplementLibelle: String?,
    val sousCategorieComplementSql: String?,
    val sousCategorieComplementSqlId: String?,
    val sousCategorieComplementSqlLibelle: String?,
    val sousCategorieComplementValeurDefaut: String?,
    val sousCategorieComplementEstRequis: Boolean?,
    val sousCategorieComplementType: TypeParametreEvenementComplement,
)

data class EvenementSousCategorieDetails(
    val evenementSousCategorieId: UUID?,
    val evenementSousCategorieCode: String?,
    val evenementSousCategorieLibelle: String?,
    val evenementSousCategorieTypeGeometrie: TypeGeometry?,
    val evenementSousCategorieComplement: Collection<BuildDynamicForm.DynamicFormParametreFront>?,
)

data class TypeEvent(
    val id: UUID,
    val libelle: String,
    val parameters: List<SousCategorieComplement>,
)
