package remocra.utils

import org.jooq.Record
import org.jooq.Result
import remocra.auth.UserInfo
import remocra.data.DashboardQueryRequestData
import remocra.data.enums.ErrorType
import remocra.exception.RemocraResponseException
import java.util.UUID

class RequestUtils {

    private val PLACEHOLDER_DELIMITER = "#"

    // Utilisé pour exécuter une "fausse" requête, typiquement pour tester sa validité, sans s'intéresser au résultat (qui ne remontera rien, au vu de la valeur !)
    private val dummyUUID = "00000000-0000-0000-0000-000000000000"

    private enum class VariableContextUtilisateur(val varName: String) { ZONE_COMPETENCE_ID("ZONE_COMPETENCE_ID"), UTILISATEUR_ID("UTILISATEUR_ID"), ORGANISME_ID("ORGANISME_ID") }

    /**
     * Remplace les paramètres globaux, dans une requête SQL pouvant les contenir, par les données de <b>l'utilisateur connecté</b>.
     *
     */
    fun replaceGlobalParameters(userInfo: UserInfo?, requeteSql: String): String {
        // On vérifie si la requête contient les variables utilisateur, si oui on les remplace par les informations de l'utilisateur
        val remplacementMap = mapOf(
            VariableContextUtilisateur.ZONE_COMPETENCE_ID to userInfo?.let { if (it.zoneCompetence != null) it.zoneCompetence!!.zoneIntegrationId else dummyUUID }.toString(),
            VariableContextUtilisateur.UTILISATEUR_ID to userInfo?.utilisateur?.utilisateurId.toString(),
            VariableContextUtilisateur.ORGANISME_ID to userInfo?.utilisateur?.let { if (it.utilisateurOrganismeId != null) it.utilisateurOrganismeId else dummyUUID }.toString(),
        )
        return replaceFromMap(requeteSql, remplacementMap)
    }

    /**
     * Remplace les paramètres globaux, dans une requête SQL pouvant les contenir, par les données individuellement passées en paramètre
     */
    fun replaceGlobalParameters(requeteSql: String, zoneCompetenceId: UUID?, utilisateurId: UUID?, organismeId: UUID?): String {
        val remplacementMap: MutableMap<VariableContextUtilisateur, String> = mutableMapOf()
        remplacementMap[VariableContextUtilisateur.ZONE_COMPETENCE_ID] = (zoneCompetenceId ?: dummyUUID).toString()
        remplacementMap[VariableContextUtilisateur.UTILISATEUR_ID] = (utilisateurId ?: dummyUUID).toString()
        remplacementMap[VariableContextUtilisateur.ORGANISME_ID] = (organismeId ?: dummyUUID).toString()

        return replaceFromMap(requeteSql, remplacementMap)
    }

    private fun replaceFromMap(requeteSql: String, remplacementMap: Map<VariableContextUtilisateur, String>): String {
        var requeteModifiee = requeteSql
        remplacementMap.forEach { (cle, valeur) ->
            val regex = Regex("=\\s*\\$PLACEHOLDER_DELIMITER${cle.varName}\\$PLACEHOLDER_DELIMITER")
            if (valeur === dummyUUID) {
                requeteModifiee = requeteModifiee.replace(regex, "IS NOT NULL")
            } else {
                requeteModifiee = requeteModifiee.replace("$PLACEHOLDER_DELIMITER${cle.varName}$PLACEHOLDER_DELIMITER", "'$valeur'")
            }
        }
        return requeteModifiee
    }

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

    fun mapQueryToFieldData(data: Result<Record>, queryRequest: DashboardQueryRequestData): FieldData? {
        // Extraction des noms de champs
        if (data.isEmpty()) {
            return null
        }
        val fields = data[0].fields().map { it.name } as? List<String> ?: throw RemocraResponseException(ErrorType.DASHBOARD_FIELD_REQUIRE)

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
