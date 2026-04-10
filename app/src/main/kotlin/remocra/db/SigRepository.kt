package remocra.db

import jakarta.annotation.Nullable
import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.Result

/**
 * Le contexte est nullable car, si la connexion à une base de données externe n'est pas configurée,
 * il n'est pas possible de l'initialiser correctement.
 * Le repository doit cependant rester injectable, bien qu'il soit inutile sans contexte.
 * Le contexte est donc null lorsque les informations de connexion ne sont pas définies.
 * Il est donc nécessaire, à chaque utilisation du contexte, de vérifier qu'il n'est pas nul (dsl!!.select[...]) ;
 * dans le cas contraire, une RuntimeException est justifiée.
*/
class SigRepository @Inject constructor(@param:Sig @param:Nullable private val dsl: DSLContext?) {

    fun getMetaStructureTable(schemaName: String, tableName: String): List<ColumnInfo> {
        val databaseType = detectDatabaseType()
        return dsl!!.meta().tables
            .first { table ->
                table.schema!!.name == schemaName && table.name == tableName
            }.fields()
            .map { field ->
                ColumnInfo(
                    schemaName = schemaName,
                    columnName = field.name,
                    columnType = mapDatabaseTypeToSQL(field.dataType.typeName, databaseType),
                    columnNullable = field.dataType.nullable(),
                )
            }
    }

    data class ColumnInfo(
        val schemaName: String,
        val columnName: String,
        val columnType: String,
        val columnNullable: Boolean,
    )

    /**
     * Détecte le type de base de données utilisée pour la source SIG
     */
    private fun detectDatabaseType(): DatabaseType {
        return try {
            val conn = dsl!!.configuration().connectionProvider().acquire()
            val databaseProductName = conn?.metaData?.databaseProductName?.lowercase() ?: "unknown"
            conn?.close()

            // Pour l'instant, on ne gère que Oracle et Postgresl (à voir si d'autres clients ont d'autres types de base de données)
            when {
                databaseProductName.contains("postgresql") || databaseProductName.contains("postgres") -> DatabaseType.POSTGRESQL
                else -> throw IllegalArgumentException("Le type de la base de données n'est pas supporté par REMOcRA.")
            }
        } catch (_: Exception) {
            throw IllegalArgumentException("Le type de la base de données n'est pas supporté par REMOcRA.")
        }
    }

    /**
     * Mappe les types internes de la base de données vers les types PostgreSQL
     * Tous les types sont normalisés en PostgreSQL pour la requête CREATE TABLE
     */
    private fun mapDatabaseTypeToSQL(dbType: String, databaseType: DatabaseType): String =
        when (databaseType) {
            DatabaseType.POSTGRESQL -> mapPostgresTypeToSQL(dbType)
        }

    /**
     * Mappe les types PostgreSQL vers les types PostgreSQL (normalisation)
     */
    private fun mapPostgresTypeToSQL(postgresType: String): String = when (postgresType.lowercase()) {
        // Types entiers
        "decimal_integer", "int4", "integer" -> "INTEGER"
        "int8", "bigint" -> "BIGINT"
        "int2", "smallint" -> "SMALLINT"

        // Types décimaux
        "float4", "real" -> "REAL"
        "float8", "double precision" -> "DOUBLE PRECISION"
        "numeric", "decimal" -> "NUMERIC"

        // Types texte
        "text", "varchar", "character varying" -> "TEXT"
        "char", "character" -> "CHAR"
        "bpchar" -> "CHARACTER"

        // Types date/heure
        "timestamp", "timestamp without time zone" -> "TIMESTAMP"
        "timestamp with time zone", "timestamptz" -> "TIMESTAMP WITH TIME ZONE"
        "date" -> "DATE"
        "time", "time without time zone" -> "TIME"
        "timetz", "time with time zone" -> "TIME WITH TIME ZONE"

        // Types booléen
        "bool", "boolean" -> "BOOLEAN"

        // Types géométriques
        "geometry" -> "GEOMETRY"
        "geometry collection", "geometrycollection" -> "GEOMETRYCOLLECTION"
        "point" -> "POINT"
        "linestring" -> "LINESTRING"
        "polygon" -> "POLYGON"
        "multipoint" -> "MULTIPOINT"
        "multilinestring" -> "MULTILINESTRING"
        "multipolygon" -> "MULTIPOLYGON"

        // Types UUID et autres
        "uuid" -> "UUID"
        "bytea" -> "BYTEA"
        "json" -> "JSON"
        "jsonb" -> "JSONB"

        // Fallback
        else -> postgresType.uppercase()
    }

    /**
     * Énumération des types de base de données supportés
     */
    enum class DatabaseType {
        POSTGRESQL,
    }

    fun selectAll(listFields: List<Field<out Any>>, schemaSource: String, tableSource: String): Result<Record> =
        dsl!!.select(listFields).from("$schemaSource.$tableSource").fetch()
}
