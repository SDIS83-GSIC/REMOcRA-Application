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
            element.rapportPersonnaliseSourceSql.contains("DELETE", true)
        ) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID)
        }

        // On vérifie déjà s'il y a des requêtes dans les paramètres
        val parametresRequetes = element.listeRapportPersonnaliseParametre.filter { it.rapportPersonnaliseParametreType == TypeParametreRapportPersonnalise.SELECT_INPUT }
        var requete = element.rapportPersonnaliseSourceSql
        if (parametresRequetes.isNotEmpty()) {
            // On vérifie chaque requête
            parametresRequetes.forEach {
                testParametreRequeteSql(it).firstOrNull()
                requete = requete.replace(it.rapportPersonnaliseParametreCode, testParametreRequeteSql(it).firstOrNull()?.id ?: "null")
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
