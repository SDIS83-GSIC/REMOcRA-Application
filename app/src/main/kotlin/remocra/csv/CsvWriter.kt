package remocra.csv

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.google.inject.Inject
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * Classe injectable permettant d'écrire un flux CSV et de le retourner pour incorporation dans un
 * objet Response par exemple
 */
class CsvWriter {
    @Inject lateinit var csvMapper: CsvMapper

    inline fun <reified T> writeCsvStream(
        data: Collection<T>,
        delimiter: Char = ';',
    ): ByteArrayOutputStream {
        val os = ByteArrayOutputStream()
        OutputStreamWriter(os, StandardCharsets.UTF_8).use { writer ->
            csvMapper
                .writer(
                    csvMapper.schemaFor(T::class.java)
                        .withColumnSeparator(delimiter)
                        .withHeader(),
                )
                .writeValues(writer)
                .writeAll(data)
                .close()
        }
        return os
    }

    fun writeCsvStream(
        data: MutableList<Map<String, Any?>>,
    ): ByteArrayOutputStream {
        val os = ByteArrayOutputStream()
        OutputStreamWriter(os, StandardCharsets.UTF_8).use { writer ->
            csvMapper
                .writer(
                    CsvSchema.builder()
                        .setUseHeader(true)
                        .addColumns(data[0].keys, CsvSchema.ColumnType.STRING)
                        .build(),
                )
                .writeValue(writer, data)
        }
        return os
    }
}
