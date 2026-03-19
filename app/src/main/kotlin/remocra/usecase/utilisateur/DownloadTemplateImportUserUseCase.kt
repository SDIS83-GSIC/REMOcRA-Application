package remocra.usecase.utilisateur

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.inject.Inject
import remocra.csv.CsvWriter
import remocra.usecase.AbstractUseCase
import remocra.usecase.utilisateur.ImportUtilisateurUseCase.Companion.CSV_DELIMITER
import java.io.ByteArrayOutputStream

class DownloadTemplateImportUserUseCase @Inject constructor(
    private val csvWriter: CsvWriter,
) : AbstractUseCase() {

    data class UserTemplate(

        @param:JsonProperty("mail")
        val mail: String? = null,

        @param:JsonProperty("identifiant")
        val identifiant: String? = null,

        @param:JsonProperty("telephone")
        val telephone: String? = null,

        @param:JsonProperty("nom")
        val nom: String? = null,

        @param:JsonProperty("prenom")
        val prenom: String? = null,

        @param:JsonProperty("organisme")
        val organisme: String? = null,

        @param:JsonProperty("profil_utilisateur")
        val profil_utilisateur: String? = null,

        @param:JsonProperty("actif")
        val actif: String? = null,

        @param:JsonProperty("notifie")
        val notifie: String? = null,
    )

    fun execute(): ByteArrayOutputStream =
        csvWriter.writeCsvStream<UserTemplate>(
            emptyList(),
            CSV_DELIMITER,
        )
}
