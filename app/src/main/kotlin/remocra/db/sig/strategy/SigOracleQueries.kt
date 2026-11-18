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
        // Nombres entiers
        "integer", "int", "int4" -> SigPostgreSQLType.INTEGER
        "bigint", "int8" -> SigPostgreSQLType.BIGINT
        "smallint", "int2" -> SigPostgreSQLType.SMALLINT

        // Nombres décimaux / flottants
        "real", "float4", "binary_float" -> SigPostgreSQLType.REAL
        "double precision", "float8", "binary_double", "float" -> SigPostgreSQLType.DOUBLE_PRECISION
        "numeric", "decimal", "dec", "number" -> SigPostgreSQLType.NUMERIC

        // Texte
        "text", "varchar", "character varying", "character", "char", "nchar",
        "varchar2", "nvarchar2", "clob", "nclob", "long", "xmltype", "sys.xmltype",
        -> SigPostgreSQLType.TEXT

        // Dates et heures
        "timestamp", "timestamp without time zone" -> SigPostgreSQLType.TIMESTAMP
        "timestamp with time zone", "timestamptz" -> SigPostgreSQLType.TIMESTAMP_WITH_TIME_ZONE
        "date" -> SigPostgreSQLType.DATE
        "time", "time without time zone" -> SigPostgreSQLType.TIME
        "time with time zone", "timetz" -> SigPostgreSQLType.TIME_WITH_TIME_ZONE

        // Booléen
        "boolean", "bool" -> SigPostgreSQLType.BOOLEAN

        // Géométries (générique + sous-types PostGIS)
        "geometry", "geography", "user-defined",
        "sdo_geometry", "mdsys.sdo_geometry",
        -> SigPostgreSQLType.GEOMETRY
        "geometrycollection" -> SigPostgreSQLType.GEOMETRYCOLLECTION
        "point" -> SigPostgreSQLType.POINT
        "linestring" -> SigPostgreSQLType.LINESTRING
        "polygon" -> SigPostgreSQLType.POLYGON
        "multipoint" -> SigPostgreSQLType.MULTIPOINT
        "multilinestring" -> SigPostgreSQLType.MULTILINESTRING
        "multipolygon" -> SigPostgreSQLType.MULTIPOLYGON

        // Autres types
        "uuid" -> SigPostgreSQLType.UUID
        "bytea", "blob", "raw", "long raw" -> SigPostgreSQLType.BYTEA
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
        // Types géométriques à convertir en WKT pour Oracle car ils nécessitent un traitement spécifique
        val geomTypes = setOf(
            SigPostgreSQLType.GEOMETRY,
            SigPostgreSQLType.GEOMETRYCOLLECTION,
            SigPostgreSQLType.POINT,
            SigPostgreSQLType.LINESTRING,
            SigPostgreSQLType.POLYGON,
            SigPostgreSQLType.MULTIPOINT,
            SigPostgreSQLType.MULTILINESTRING,
            SigPostgreSQLType.MULTIPOLYGON,
        )

        val selectClause = listFields.joinToString(", ") { col ->
            val colName = col.columnName.validate().uppercase()
            if (col.columnType in geomTypes) {
                // Si types géométriques, on les convertit en WKT
                "SDO_UTIL.TO_WKTGEOMETRY($colName) AS $colName"
            } else {
                colName
            }
        }
        return delegateQueries.selectAll(
            """
                SELECT $selectClause
                FROM ${schemaSource.validate().uppercase()}.${tableSource.validate().uppercase()}
            """.trimIndent(),
        ).map { row ->
            row.mapValues { (_, value) ->
                // On doit mapper les types CLOB en String pour éviter les problèmes de mémoire avec Oracle
                // Cela concerne les objets volumineux (CLOB) qui ne sont pas entièrement chargés en mémoire par défaut
                // et qu'il faut retraiter pour obtenir notamment les géométries (WKT) et les gros textes.
                if (value is java.sql.Clob) {
                    val text = value.getSubString(1, value.length().toInt())
                    // On libère la mémoire Oracle immédiatement après lecture de la ligne.
                    value.free()
                    text
                } else {
                    value
                }
            }
        }
    }
}
