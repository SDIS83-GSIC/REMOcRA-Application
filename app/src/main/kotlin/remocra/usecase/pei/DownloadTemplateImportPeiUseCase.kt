package remocra.usecase.pei

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.inject.Inject
import remocra.csv.CsvWriter
import remocra.usecase.AbstractUseCase
import java.io.ByteArrayOutputStream

class DownloadTemplateImportPeiUseCase @Inject constructor(
    private val csvWriter: CsvWriter,
) : AbstractUseCase() {

    data class MajPositionsPeiTemplateCsv(

        @param:JsonProperty("EPSG")
        val epsg: String? = null,

        @param:JsonProperty("X")
        val x: String? = null,

        @param:JsonProperty("Y")
        val y: String? = null,

        @param:JsonProperty("NUMERO")
        val numero: String? = null,

        @param:JsonProperty("OBSERVATION")
        val observation: String? = null,

        @param:JsonProperty("DATE_GPS")
        val dateGps: String? = null,
    )

    fun execute(): ByteArrayOutputStream =
        csvWriter.writeCsvStream<MajPositionsPeiTemplateCsv>(
            emptyList(),
            ImportPeiUseCase.Companion.CSV_DELIMITER,
        )
}
