package remocra.csv

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import jakarta.inject.Inject
import remocra.GlobalConstants
import java.io.File
import java.io.InputStream

/** Classe injectable permettant de lire un flux CSV et de le retourner */
class CsvReader {
    @Inject lateinit var csvMapper: CsvMapper

    inline fun <reified T> readCsvFile(
        file: File,
    ): MutableList<T>? {
        return csvMapper
            .readerWithTypedSchemaFor(T::class.java)
            .with(csvMapper.typedSchemaFor(T::class.java).withHeader())
            .readValues<T>(file)
            .readAll()
    }

    inline fun <reified T> readCsvFile(
        inputStream: InputStream,
        delimiter: Char = GlobalConstants.DELIMITER_CSV,
    ): MutableList<T>? {
        try {
            return csvMapper
                .readerWithTypedSchemaFor(T::class.java)
                .with(
                    csvMapper.typedSchemaFor(T::class.java).withHeader()
                        .withColumnSeparator(delimiter),
                )
                .readValues<T>(inputStream)
                .readAll()
        } catch (e: Exception) {
            throw IllegalArgumentException("Le format du fichier renseigné est incorrect.")
        }
    }

    inline fun <reified T> readCsvString(
        content: String,
    ): MutableList<T>? {
        return csvMapper
            .readerWithTypedSchemaFor(T::class.java)
            .with(csvMapper.typedSchemaFor(T::class.java).withHeader())
            .readValues<T>(content)
            .readAll()
    }
}
