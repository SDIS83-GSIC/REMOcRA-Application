package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.csv.CsvWriter
import remocra.data.GenererRapportPersonnaliseData
import remocra.db.RapportPersonnaliseRepository
import remocra.usecase.AbstractUseCase
import java.io.ByteArrayOutputStream

/**
 * Permet d'exporter les données de la requêtes en csv
 */
class ExportDataRapportPersonnaliseUseCase : AbstractUseCase() {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var rapportPersonnaliseUtils: RapportPersonnaliseUtils

    @Inject
    private lateinit var csvWriter: CsvWriter

    fun execute(userInfo: UserInfo?, genererRapportPersonnaliseData: GenererRapportPersonnaliseData): ByteArrayOutputStream {
        var requete = rapportPersonnaliseRepository.getSqlRequete(genererRapportPersonnaliseData.rapportPersonnaliseId)

        // On remplace avec les données paramètres fournies
        genererRapportPersonnaliseData.listeParametre.forEach {
            requete = requete.replace(it.rapportPersonnaliseParametreCode, it.value.toString())
        }

        // On remplace les variables utilisateur de la requête par les données userinfo
        val requeteModifiee = rapportPersonnaliseUtils.formatParametreRequeteSql(userInfo, requete)
        requete = if (requeteModifiee != null) requeteModifiee else requete

        val result = rapportPersonnaliseRepository.executeSqlRapport(requete)

        // On construit l'objet qui contient une liste de clé valeur
        val data: MutableList<Map<String, Any?>> = mutableListOf()
        for (record in result) {
            val row: MutableMap<String, Any?> = mutableMapOf()
            for (field in record.fields()) {
                row[field.name] = record.getValue(field)
            }
            data.add(row)
        }

        return csvWriter.writeCsvStream(data)
    }
}
