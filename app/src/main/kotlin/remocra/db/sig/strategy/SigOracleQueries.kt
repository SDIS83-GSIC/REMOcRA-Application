package remocra.db.sig.strategy

import jakarta.inject.Inject
import remocra.db.sig.data.ColumnInfo
import remocra.db.sig.data.SigPostgreSQLType
import java.util.stream.Stream

class SigOracleQueries @Inject constructor(
    private val delegateQueries: DelegateQueries,
) : SigQueries {

    override fun getMetaStructureTable(
        schemaName: String,
        tableName: String,
    ): List<ColumnInfo> {
        return delegateQueries
            .getMetaStructureTable(
                query = """
                SELECT
                    column_name,
                    data_type,
                    nullable
                FROM all_tab_columns
                WHERE owner = UPPER(?)
                    AND table_name = UPPER(?)
                ORDER BY column_id
                """.trimIndent(),
                preparedStatement = {
                    this.setString(1, schemaName)
                    this.setString(2, tableName)
                },
            ) { rs ->
                ColumnInfo(
                    schemaName = schemaName,
                    columnName = rs.getString("column_name").lowercase(),
                    columnType = mapDatabaseTypeToPostgreSQL(rs.getString("data_type")),
                    columnNullable = rs.getString("nullable") == "Y",
                )
            }
    }

    /**
     * Mappe les types Oracle vers les types PostgreSQL (normalisation)
     */
    private fun mapDatabaseTypeToPostgreSQL(postgresType: String): SigPostgreSQLType = when (postgresType.lowercase()) {
        // Types texte
        "text", "varchar", "character varying" -> SigPostgreSQLType.TEXT

        // Fallback
        else -> SigPostgreSQLType.TEXT
    }

    override fun selectAll(
        listFields: List<ColumnInfo>,
        schemaSource: String,
        tableSource: String,
    ): Stream<Map<String, Any?>> {
        return delegateQueries.selectAll(
            """
                SELECT ${listFields.joinToString(", ") { it.columnName.validate() } .uppercase()}
                FROM ${schemaSource.validate().uppercase()}.${tableSource.validate().uppercase()}
            """.trimIndent(),
        )
    }
}
