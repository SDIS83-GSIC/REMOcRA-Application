package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.IdLibelleRapportPersonnalise
import remocra.data.RapportPersonnaliseData
import remocra.data.RapportPersonnaliseParametreData
import remocra.data.enums.ErrorType
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.remocra.enums.TypeParametreRapportPersonnalise
import remocra.exception.RemocraResponseException
import remocra.utils.DateUtils

class RapportPersonnaliseUtils {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var dateUtils: DateUtils

    private enum class VariableContextUtilisateur(val varName: String) { ZONE_COMPETENCE_ID("ZONE_COMPETENCE_ID"), UTILISATEUR_ID("UTILISATEUR_ID"), ORGANISME_ID("ORGANISME_ID") }

    fun formatParametreRequeteSql(userInfo: UserInfo?, requeteSql: String?): String? {
        // On vérifie si la requête contient les variable utilisateur, si oui on les remplace par les informations de l'utilisateur
        val remplacementMap = mapOf(
            VariableContextUtilisateur.ZONE_COMPETENCE_ID.varName to userInfo?.zoneCompetence?.zoneIntegrationId.toString(),
            VariableContextUtilisateur.UTILISATEUR_ID.varName to userInfo?.utilisateur?.utilisateurId.toString(),
            VariableContextUtilisateur.ORGANISME_ID.varName to userInfo?.utilisateur?.utilisateurOrganismeId.toString(),
        )
        var requeteModifiee = requeteSql
        remplacementMap.forEach { cle, valeur ->
            if (requeteModifiee != null) {
                requeteModifiee = requeteModifiee!!.replace(cle, "'$valeur'")
            }
        }
        return requeteModifiee
    }
    private fun testParametreRequeteSql(userInfo: UserInfo?, parametreRequete: RapportPersonnaliseParametreData): List<IdLibelleRapportPersonnalise> {
        try {
            val requeteModifiee = formatParametreRequeteSql(userInfo, parametreRequete.rapportPersonnaliseParametreSourceSql)
            return rapportPersonnaliseRepository.executeSqlParametre(requeteModifiee!!)
        } catch (e: Exception) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_PARAMETRE_INVALID, "(paramètre :  ${parametreRequete.rapportPersonnaliseParametreCode}) : ${e.message}")
        }
    }

    fun checkContraintes(userInfo: UserInfo?, element: RapportPersonnaliseData) {
        // Le code doit être unique
        if (rapportPersonnaliseRepository.checkCodeExists(element.rapportPersonnaliseCode, element.rapportPersonnaliseId)) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_CODE_UNIQUE)
        }

        // Aucun paramètre ne doivent avoir le même code
        if (element.listeRapportPersonnaliseParametre.map { it.rapportPersonnaliseParametreCode }.distinct().size != element.listeRapportPersonnaliseParametre.size) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_PARAMETRE_CODE_UNIQUE)
        }

        // TODO vérifier qu'on n'est pas injection ?
        if (element.rapportPersonnaliseSourceSql.contains("CREATE", true) ||
            element.rapportPersonnaliseSourceSql.contains("UPDATE", true) ||
            element.rapportPersonnaliseSourceSql.contains("DROP", true) ||
            element.rapportPersonnaliseSourceSql.contains("DELETE", true) ||
            element.rapportPersonnaliseSourceSql.contains("TRUNCATE", true)
        ) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID, "Ne doit pas contenir de CREATE, UPDATE, DROP, DELETE ou TRUNCATE")
        }

        if (!element.rapportPersonnaliseSourceSql.startsWith("SELECT", ignoreCase = true) &&
            !element.rapportPersonnaliseSourceSql.startsWith("WITH", ignoreCase = true)
        ) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID, "doit commencer par un 'SELECT' ou un 'WITH'")
        }

        var requete = element.rapportPersonnaliseSourceSql

        // On doit aussi remplacer les paramètres pour pouvoir vérifier que la requête est correcte
        element.listeRapportPersonnaliseParametre.forEach {
            when (it.rapportPersonnaliseParametreType) {
                TypeParametreRapportPersonnalise.CHECKBOX_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, "true")
                TypeParametreRapportPersonnalise.DATE_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, it.rapportPersonnaliseParametreValeurDefaut.let { param -> if (param.isNullOrEmpty()) dateUtils.format(dateUtils.now()) else param })
                TypeParametreRapportPersonnalise.NUMBER_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, it.rapportPersonnaliseParametreValeurDefaut ?: "10")
                TypeParametreRapportPersonnalise.SELECT_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, testParametreRequeteSql(userInfo, it).firstOrNull()?.id ?: "null")
                TypeParametreRapportPersonnalise.TEXT_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, it.rapportPersonnaliseParametreValeurDefaut ?: "")
            }
        }

        // On vérifie ensuite la requête globale
        try {
            val requeteModifiee = formatParametreRequeteSql(userInfo, requete)
            requete = if (requeteModifiee != null) requeteModifiee else requete
            rapportPersonnaliseRepository.executeSqlRapport(requete)
        } catch (e: Exception) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID, e.message)
        }
    }
}
