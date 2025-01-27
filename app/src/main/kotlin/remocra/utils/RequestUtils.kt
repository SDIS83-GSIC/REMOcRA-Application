package remocra.utils

import org.jooq.Record
import org.jooq.Result
import remocra.data.DashboardQueryRequestData
import remocra.data.enums.ErrorType
import remocra.exception.RemocraResponseException
import java.util.UUID

class RequestUtils {

    class InvalidQueryException(message: String) : Exception(message)

    fun validateReadOnlyQuery(sqlQuery: String) {
        // Convertir la requête en minuscules pour une analyse insensible à la casse
        val queryLower = sqlQuery.trim().lowercase()

        // Liste des mots-clés interdits (actions en écriture ou dangereuses)
        val forbiddenKeywords = listOf(
            "insert", "update", "delete", "drop", "alter", "create", "truncate",
            "merge", "exec", "execute", "call", "--", "/*", "*/",
        )

        // Liste des mots-clés autorisés pour les requêtes en lecture
        val allowedKeywords = listOf("select", "with")

        // Vérifier si des mots-clés interdits sont présents dans la requête
        for (keyword in forbiddenKeywords) {
            if (queryLower.contains(keyword)) {
                throw RemocraResponseException(ErrorType.DASHBOARD_INVALID_KEYWORD)
            }
        }

        // Vérifier que la requête commence par un mot-clé autorisé
        val startsWithAllowedKeyword = allowedKeywords.any { queryLower.startsWith(it) }
        if (!startsWithAllowedKeyword) {
            throw RemocraResponseException(ErrorType.DASHBOARD_INVALID_FIRST_KEYWORD)
        }

        // Vérifier l'absence de sous-requêtes interdites (exemple : injection de requêtes)
        val suspiciousPatterns = listOf(
            Regex(""";\s*(?!${'$'}).*"""), // Commandes multiples dans une requête
            Regex("""--.*"""), // Commentaires SQL
            Regex("""/\*[\s\S]*?\*/"""), // Bloc de commentaires SQL
        )

        for (pattern in suspiciousPatterns) {
            if (pattern.containsMatchIn(queryLower)) {
                throw RemocraResponseException(ErrorType.DASHBOARD_INVALID_MULTIPLE_INSTRUCTION)
            }
        }
    }

    //    Extrait les schémas, tables et colonnes de la requête
    fun parseSQLQuery(sqlQuery: String): Map<String, Any?> {
        val schemaTableRegex = Regex("""(?:(\w+)\.)?(\w+)(?:\s+AS\s+\w+|\s+\w+)?""", RegexOption.IGNORE_CASE)
        val selectRegex = Regex("""SELECT\s+(.*?)\s+FROM\s+""", RegexOption.IGNORE_CASE)
        val fromRegex = Regex("""FROM\s+(.*?)(\s+WHERE|\s+LIMIT|\s+ORDER|\s+JOIN|\s*$)""", RegexOption.IGNORE_CASE)
        val withRegex = Regex("""WITH\s+(.*?)\s+SELECT\s+""", RegexOption.IGNORE_CASE)

        val result = mutableMapOf<String, Any?>()

        // Extraire les sous-requêtes WITH si présentes
        val withMatches = withRegex.find(sqlQuery)?.groups?.get(1)?.value
        val withClauses = mutableMapOf<String, String>()

        if (!withMatches.isNullOrEmpty()) {
            // Parse chaque sous-requête dans la clause WITH
            withMatches.split(",").forEach { withClause ->
                val parts = withClause.split("AS", ignoreCase = true, limit = 2)
                if (parts.size == 2) {
                    val name = parts[0].trim()
                    val definition = parts[1].trim()
                    withClauses[name] = definition
                }
            }
        }

        // Ajouter les sous-requêtes WITH au résultat
        result["withClauses"] = withClauses

        // Extraire les colonnes
        val columns = selectRegex.find(sqlQuery)?.groups?.get(1)?.value
            ?.split(",")
            ?.map { it.trim() }
            ?: emptyList()

        // Extraire la clause FROM
        val fromMatch = fromRegex.find(sqlQuery)
        val fromClause = fromMatch?.groups?.get(1)?.value?.trim()

        val tables = mutableMapOf<String, String?>()

        if (!fromClause.isNullOrEmpty()) {
            // Tester si la regex capture quelque chose
            val matches = schemaTableRegex.findAll(fromClause).toList()

            for (match in matches) {
                val schema = match.groups[1]?.value // Schéma
                val table = match.groups[2]?.value // Nom de table

                if (!table.isNullOrEmpty()) {
                    tables[table] = schema
                }
            }
        }

        // Ajouter les résultats
        result["columns"] = columns
        result["tables"] = tables

        return result
    }

    //    Retourne le type des colonnes de la requête demandée
    fun generateSQLQuery(parsedQuery: Map<String, Any?>): String {
        // Récupérer les colonnes avec une vérification sécurisée
        val columns = (parsedQuery["columns"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()

        // Récupérer les tables et leurs schémas avec une vérification sécurisée
        val tables = (parsedQuery["tables"] as? Map<*, *>)?.filterKeys { it is String }?.mapKeys { it.key as String }
            ?.mapValues { it.value as? String }

        if (tables.isNullOrEmpty()) {
            throw RemocraResponseException(ErrorType.DASHBOARD_NO_TABLE_IN_PARSED_QUERY)
        }

        // Construire les conditions pour column_name
        val columnCondition = if (columns.isNotEmpty()) {
            columns.filter { it.isNotEmpty() && it != "*" }
                .joinToString(" OR ") { "column_name = '$it'" }
        } else {
            null
        }

        // Construire la requête SQL pour chaque table
        val tableConditions = tables.map { (tableAlias, schema) ->
            val schemaCondition = if (!schema.isNullOrEmpty()) "table_schema = '$schema'" else "1=1"
            "($schemaCondition AND table_name = '$tableAlias')"
        }.joinToString(" OR ")

        // Construire la requête SQL finale
        val queryGetGeometryCol = buildString {
            appendLine("SELECT table_schema, table_name, column_name, udt_name")
            appendLine("FROM information_schema.columns")
            append("WHERE ($tableConditions)")

            // Ajouter les conditions de colonne uniquement si elles existent
            if (!columnCondition.isNullOrEmpty()) {
                appendLine(" AND ($columnCondition)") // Ajouter "AND" seulement si columnCondition existe
            }
        }.trim()

        // Retourner la requête à exécuter
        return queryGetGeometryCol
    }

    //    Réécrit la requête en castant les colonnes de type geometry
    fun rewriteQueryWithGeoJSON(
        originalQuery: String,
        geometryColumns: Result<Record>,
    ): String {
        // Extraire les noms des colonnes géométriques (udt_name = 'geometry') depuis le Result<Record>
        val geoColumns = geometryColumns.filter { record ->
            record["udt_name"] == "geometry" // Filtrer uniquement les colonnes de type 'geometry'
        }.mapNotNull { record ->
            record["column_name"] as? String // Récupérer les noms des colonnes
        }.toSet() // Utiliser un ensemble pour éviter les doublons

        // Regex pour capturer la partie SELECT (incluant les clauses WITH si présentes)
        val withRegex = Regex("""WITH\s+(.*?)\s+SELECT""", RegexOption.IGNORE_CASE)
        val selectRegex = Regex("""SELECT\s+(.*?)\s+FROM""", RegexOption.IGNORE_CASE)

        // Extraire la clause WITH si elle existe
        val withMatch = withRegex.find(originalQuery)
        val withPart = withMatch?.groups?.get(1)?.value?.trim()

        // Extraire la clause SELECT
        val selectMatch = selectRegex.find(originalQuery)
        if (selectMatch == null) {
            throw RemocraResponseException(ErrorType.DASHBOARD_NO_SELECT_KEYWORD)
        }

        val selectPart = selectMatch.groups[1]?.value?.trim()

        val modifiedColumns = mutableListOf<String>()

        if (selectPart == "*") {
            // Développer le * en toutes les colonnes de la table
            val allColumns = geometryColumns.mapNotNull { record ->
                record["column_name"] as? String
            }.toSet()

            allColumns.forEach { column ->
                if (geoColumns.contains(column)) {
                    // Si c'est une colonne géométrique, ajouter ST_AsGeoJSON
                    modifiedColumns.add("ST_AsGeoJSON($column) AS ${column}_geojson")
                } else {
                    // Sinon, ajouter la colonne telle quelle
                    modifiedColumns.add(column)
                }
            }
        } else {
            // Sinon, analyser les colonnes existantes dans la requête SELECT
            val originalColumns = selectPart?.split(",")?.map { it.trim() }?.toMutableSet()

            if (originalColumns != null) {
                originalColumns.forEach { column ->
                    if (geoColumns.contains(column)) {
                        // Remplacer la colonne géométrique par ST_AsGeoJSON
                        modifiedColumns.add("ST_AsGeoJSON($column) AS ${column}_geojson")
                    } else {
                        // Garder les colonnes non géométriques telles quelles
                        modifiedColumns.add(column)
                    }
                }
            }
        }

        // Reconstruire la partie SELECT
        val modifiedSelectPart = "SELECT ${modifiedColumns.joinToString(", ")} FROM"

        // Reconstruire la requête finale en réintégrant les clauses WITH si elles existent
        return originalQuery.replaceRange(
            selectMatch.range.first,
            selectMatch.range.last + 1,
            modifiedSelectPart,
        ).trim()
    }

    fun mapQueryToFieldData(data: Result<Record>, queryRequest: DashboardQueryRequestData): FieldData {
        // Extraction des noms de champs
        val fields = data.get(0).fields().map { it.getName() } as? List<String> ?: throw RemocraResponseException(ErrorType.DASHBOARD_FIELD_REQUIRE)

        return FieldData(
            queryId = queryRequest.queryId,
            queryTitle = queryRequest.queryTitle,
            querySql = queryRequest.query,
            name = fields,
            values = data.map { it.intoList() },
        )
    }

    data class FieldData(
        val queryId: UUID?,
        val queryTitle: String,
        val querySql: String,
        val name: List<String>,
        val values: MutableList<MutableList<Any>>?,
    )
}
