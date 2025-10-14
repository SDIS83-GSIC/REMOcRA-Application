package remocra.usecase.rapportpersonnalise

import RapportPersonnaliseUtils
import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.csv.CsvWriter
import remocra.data.GenererRapportPersonnaliseData
import remocra.usecase.AbstractUseCase
import java.io.ByteArrayOutputStream

/**
 * Permet d'exporter les données de la requêtes en csv
 */
class ExportDataRapportPersonnaliseUseCase : AbstractUseCase() {

    @Inject
    private lateinit var rapportPersonnaliseUtils: RapportPersonnaliseUtils

    @Inject
    private lateinit var csvWriter: CsvWriter

    fun execute(userInfo: WrappedUserInfo, genererRapportPersonnaliseData: GenererRapportPersonnaliseData): ByteArrayOutputStream {
        val result = rapportPersonnaliseUtils.buildRapportPersonnaliseData(genererRapportPersonnaliseData, userInfo)

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
