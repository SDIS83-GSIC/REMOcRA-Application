package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.Result
import javax.annotation.Nullable

/**
 * Le contexte est nullable car, si la connexion à une base de données externe n'est pas configurée,
 * il n'est pas possible de l'initialiser correctement.
 * Le repository doit cependant rester injectable, bien qu'il soit inutile sans contexte.
 * Le contexte est donc null lorsque les informations de connexion ne sont pas définies.
 * Il est donc nécessaire, à chaque utilisation du contexte, de vérifier qu'il n'est pas nul (dsl!!.select[...]) ;
 * dans le cas contraire, une RuntimeException est justifiée.
*/
class SigRepository @Inject constructor(@Sig @Nullable private val dsl: DSLContext?) {

    fun getMetaStructureTable(schemaName: String, tableName: String): List<ColumnInfo> =
        dsl!!.meta().tables
            .first { table ->
                table.schema!!.name == schemaName && table.name == tableName
            }.fields()
            .map { field ->
                ColumnInfo(
                    schemaName = schemaName,
                    columnName = field.name,
                    columnType = field.dataType.typeName,
                    columnNullable = field.dataType.nullable(),
                )
            }

    data class ColumnInfo(
        val schemaName: String,
        val columnName: String,
        val columnType: String,
        val columnNullable: Boolean,
    )

    fun selectAll(listFields: List<Field<out Any>>, schemaSource: String, tableSource: String): Result<Record> =
        dsl!!.select(listFields).from("$schemaSource.$tableSource").fetch()
}
