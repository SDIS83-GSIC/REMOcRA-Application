package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import remocra.data.IdLibelleRapportPersonnalise
import remocra.data.RapportPersonnaliseData
import remocra.data.RapportPersonnaliseParametreData
import remocra.data.enums.ErrorType
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.remocra.enums.TypeParametreRapportPersonnalise
import remocra.exception.RemocraResponseException

class RapportPersonnaliseUtils {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    private fun testParametreRequeteSql(parametreRequete: RapportPersonnaliseParametreData): List<IdLibelleRapportPersonnalise> {
        try {
            return rapportPersonnaliseRepository.executeSqlParametre(parametreRequete.rapportPersonnaliseParametreSourceSql!!)
        } catch (e: Exception) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_PARAMETRE_INVALID, "(paramètre :  ${parametreRequete.rapportPersonnaliseParametreCode}) : ${e.message}")
        }
    }

    fun checkContraintes(element: RapportPersonnaliseData) {
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
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID)
        }

        if (element.rapportPersonnaliseSourceSql.startsWith("SELECT", ignoreCase = true) ||
            element.rapportPersonnaliseSourceSql.startsWith("WITH", ignoreCase = true)
        ) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID)
        }

        var requete = element.rapportPersonnaliseSourceSql

        // On doit aussi remplacer les paramètres pour pouvoir vérifier que la requête est correcte
        element.listeRapportPersonnaliseParametre.forEach {
            when (it.rapportPersonnaliseParametreType) {
                TypeParametreRapportPersonnalise.CHECKBOX_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, "true")
                TypeParametreRapportPersonnalise.DATE_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, it.rapportPersonnaliseParametreValeurDefaut ?: "2024-01-01 00:00:00")
                TypeParametreRapportPersonnalise.NUMBER_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, it.rapportPersonnaliseParametreValeurDefaut ?: "10")
                TypeParametreRapportPersonnalise.SELECT_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, testParametreRequeteSql(it).firstOrNull()?.id ?: "null")
                TypeParametreRapportPersonnalise.TEXT_INPUT ->
                    requete = requete.replace(it.rapportPersonnaliseParametreCode, it.rapportPersonnaliseParametreValeurDefaut ?: "")
            }
        }

        // On vérifie ensuite la requête globale
        try {
            rapportPersonnaliseRepository.executeSqlRapport(requete)
        } catch (e: Exception) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID, e.message)
        }
    }
}
