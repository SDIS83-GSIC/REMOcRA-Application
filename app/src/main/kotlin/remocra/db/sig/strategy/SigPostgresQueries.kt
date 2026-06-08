package remocra.db.sig.strategy

import jakarta.inject.Inject
import remocra.db.sig.data.ColumnInfo
import remocra.db.sig.data.SigPostgreSQLType
import java.util.stream.Stream

class SigPostgresQueries @Inject constructor(
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
                    is_nullable,
                    udt_name -- Type réel PostgreSQL
                FROM information_schema.columns
                WHERE table_schema = ?
                    AND table_name = ?
                ORDER BY ordinal_position;
                """.trimIndent(),
                preparedStatement = {
                    this.setString(1, schemaName)
                    this.setString(2, tableName)
                },
            ) { rs ->
                ColumnInfo(
                    schemaName = schemaName,
                    columnName = rs.getString("column_name"),
                    columnType = mapDatabaseTypeToPostgreSQL(rs.getString("udt_name")),
                    columnNullable = rs.getBoolean("is_nullable"),
                )
            }
    }

    /**
     * Mappe les types PostgreSQL vers les types PostgreSQL (normalisation)
     */
    private fun mapDatabaseTypeToPostgreSQL(postgresType: String): SigPostgreSQLType = when (postgresType.lowercase()) {
        // Types entiers
        "decimal_integer", "int4", "integer", "tinyint" -> SigPostgreSQLType.INTEGER
        "int8", "bigint" -> SigPostgreSQLType.BIGINT
        "int2", "smallint" -> SigPostgreSQLType.SMALLINT

        // Types décimaux
        "float4", "real" -> SigPostgreSQLType.REAL
        "float8", "double precision" -> SigPostgreSQLType.DOUBLE_PRECISION
        "numeric", "decimal" -> SigPostgreSQLType.NUMERIC

        // Types texte
        "text", "varchar", "character varying" -> SigPostgreSQLType.TEXT
        "char", "character" -> SigPostgreSQLType.CHAR
        "bpchar" -> SigPostgreSQLType.CHARACTER

        // Types date/heure
        "timestamp", "timestamp without time zone" -> SigPostgreSQLType.TIMESTAMP
        "timestamp with time zone", "timestamptz" -> SigPostgreSQLType.TIMESTAMP_WITH_TIME_ZONE
        "date" -> SigPostgreSQLType.DATE
        "time", "time without time zone" -> SigPostgreSQLType.TIME
        "timetz", "time with time zone" -> SigPostgreSQLType.TIME_WITH_TIME_ZONE

        // Types booléen
        "bool", "boolean" -> SigPostgreSQLType.BOOLEAN

        // Types géométriques
        "geometry" -> SigPostgreSQLType.GEOMETRY
        "geometry collection", "geometrycollection" -> SigPostgreSQLType.GEOMETRYCOLLECTION
        "point" -> SigPostgreSQLType.POINT
        "linestring" -> SigPostgreSQLType.LINESTRING
        "polygon" -> SigPostgreSQLType.POLYGON
        "multipoint" -> SigPostgreSQLType.MULTIPOINT
        "multilinestring" -> SigPostgreSQLType.MULTILINESTRING
        "multipolygon" -> SigPostgreSQLType.MULTIPOLYGON

        // Types UUID et autres
        "uuid" -> SigPostgreSQLType.UUID
        "bytea" -> SigPostgreSQLType.BYTEA
        "json" -> SigPostgreSQLType.JSON
        "jsonb" -> SigPostgreSQLType.JSONB

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
                SELECT ${listFields.joinToString(", ") { it.columnName.validate() }}
                FROM ${schemaSource.validate()}.${tableSource.validate()}
            """.trimIndent(),
        )
    }
}
