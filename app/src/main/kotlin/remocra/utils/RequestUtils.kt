package remocra.utils

import org.jooq.Record
import org.jooq.Result
import remocra.data.DashboardQueryRequestData
import remocra.data.enums.ErrorType
import remocra.exception.RemocraResponseException
import java.util.UUID

class RequestUtils {

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
