package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import jakarta.ws.rs.core.StreamingOutput
import remocra.csv.CsvWriter
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.enums.TypeParametreRapportPersonnalise
import remocra.usecase.AbstractUseCase
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExportConfRapportPersonnaliseUseCase : AbstractUseCase() {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var csvWriter: CsvWriter

    fun execute(rapportPersonnaliseId: UUID): StreamingOutput {
        val output =
            StreamingOutput { output ->
                val out = ZipOutputStream(output)

                val baosRapportPersonnalise = getFileRapportPersonnalise(rapportPersonnaliseId)
                out.putNextEntry(ZipEntry("remocra-rapport-personnalise.csv"))
                baosRapportPersonnalise.writeTo(out)
                baosRapportPersonnalise.close()
                out.closeEntry()

                val baosRapportPersonnaliseParametre =
                    getFileRapportPersonnaliseParametre(rapportPersonnaliseId)
                if (baosRapportPersonnaliseParametre != null) {
                    out.putNextEntry(ZipEntry("remocra-rapport-personnalise-parametre.csv"))
                    baosRapportPersonnaliseParametre.writeTo(out)
                    baosRapportPersonnaliseParametre.close()
                    out.closeEntry()
                }

                out.flush()
                out.close()
            }

        return output
    }

    private fun getFileRapportPersonnalise(rapportPersonnaliseId: UUID): ByteArrayOutputStream {
        // On va chercher le rapport avec ses paramètres
        val rapportPersonnalise = rapportPersonnaliseRepository.getRapportPersonnalisePojo(rapportPersonnaliseId)

        return csvWriter.writeCsvStream(
            listOf(
                ImportExportRapportPersonnalise(
                    rapportPersonnaliseCode = rapportPersonnalise.rapportPersonnaliseCode,
                    rapportPersonnaliseLibelle = rapportPersonnalise.rapportPersonnaliseLibelle,
                    rapportPersonnaliseChampGeometrie = rapportPersonnalise.rapportPersonnaliseChampGeometrie,
                    rapportPersonnaliseDescription = rapportPersonnalise.rapportPersonnaliseDescription,
                    rapportPersonnaliseSourceSql = rapportPersonnalise.rapportPersonnaliseSourceSql,
                    rapportPersonnaliseModule = rapportPersonnalise.rapportPersonnaliseModule,
                    rapportPersonnaliseActif = rapportPersonnalise.rapportPersonnaliseActif,
                ),
            ),
        )
    }

    private fun getFileRapportPersonnaliseParametre(rapportPersonnaliseId: UUID): ByteArrayOutputStream? {
        // On va chercher le rapport avec ses paramètres
        val rapportPersonnaliseParametre = rapportPersonnaliseRepository.getRapportPersonnaliseParametrePojo(rapportPersonnaliseId)

        if (rapportPersonnaliseParametre.isEmpty()) {
            return null
        }

        return csvWriter.writeCsvStream(
            rapportPersonnaliseParametre.map {
                ImportExportRapportPersonnaliseParametre(
                    rapportPersonnaliseParametreCode = it.rapportPersonnaliseParametreCode,
                    rapportPersonnaliseParametreLibelle = it.rapportPersonnaliseParametreLibelle,
                    rapportPersonnaliseParametreSourceSql = it.rapportPersonnaliseParametreSourceSql,
                    rapportPersonnaliseParametreDescription = it.rapportPersonnaliseParametreDescription,
                    rapportPersonnaliseParametreSourceSqlId = it.rapportPersonnaliseParametreSourceSqlId,
                    rapportPersonnaliseParametreSourceSqlLibelle = it.rapportPersonnaliseParametreSourceSqlLibelle,
                    rapportPersonnaliseParametreIsRequired = it.rapportPersonnaliseParametreIsRequired,
                    rapportPersonnaliseParametreType = it.rapportPersonnaliseParametreType,
                    rapportPersonnaliseParametreOrdre = it.rapportPersonnaliseParametreOrdre,
                )
            },
        )
    }

    data class ImportExportRapportPersonnalise(
        val rapportPersonnaliseActif: Boolean,
        val rapportPersonnaliseCode: String,
        val rapportPersonnaliseLibelle: String,
        val rapportPersonnaliseChampGeometrie: String?,
        val rapportPersonnaliseDescription: String?,
        val rapportPersonnaliseSourceSql: String,
        val rapportPersonnaliseModule: TypeModule,
    )

    data class ImportExportRapportPersonnaliseParametre(
        val rapportPersonnaliseParametreCode: String,
        val rapportPersonnaliseParametreLibelle: String,
        val rapportPersonnaliseParametreSourceSql: String?,
        val rapportPersonnaliseParametreDescription: String?,
        val rapportPersonnaliseParametreSourceSqlId: String?,
        val rapportPersonnaliseParametreSourceSqlLibelle: String?,
        val rapportPersonnaliseParametreIsRequired: Boolean,
        val rapportPersonnaliseParametreType: TypeParametreRapportPersonnalise,
        val rapportPersonnaliseParametreOrdre: Int,
    )
}
