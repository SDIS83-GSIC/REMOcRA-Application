package remocra.usecase.crise

import jakarta.inject.Inject
import jakarta.ws.rs.core.StreamingOutput
import remocra.GlobalConstants
import remocra.data.EvenementData
import remocra.db.CommuneRepository
import remocra.db.CriseRepository
import remocra.db.EvenementRepository
import remocra.db.MessageRepository
import remocra.db.MessageRepository.Message
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import java.io.File
import java.io.FileInputStream
import java.time.ZonedDateTime
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExportCriseUseCase : AbstractUseCase() {

    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var evenementRepository: EvenementRepository

    @Inject lateinit var messageRepository: MessageRepository

    @Inject lateinit var communeRepository: CommuneRepository

    @Inject lateinit var documentUtils: DocumentUtils

    data class ElementHTML(
        val criseLibelle: String?,
        val dateDebExtraction: ZonedDateTime,
        val dateFinExtraction: ZonedDateTime,
        val dateExport: ZonedDateTime,
        val dateDebCrise: ZonedDateTime?,
        val dateClotureCrise: ZonedDateTime?,
        val evenement: Collection<EvenementData>?,
        val listeCommune: Collection<String>?,
        val listeMessage: Collection<Message>,
        val hasMessage: Boolean,
    )

    fun execute(criseId: UUID, dateDebExtraction: ZonedDateTime, dateFinExtraction: ZonedDateTime, hasMessage: Boolean, hasDoc: Boolean): StreamingOutput {
        // récupérer les éléments dont on a besoin
        val crise = criseRepository.getCrise(criseId)

        val elementHtml = ElementHTML(
            criseLibelle = crise.criseLibelle,
            dateDebExtraction = dateDebExtraction,
            dateFinExtraction = dateFinExtraction,
            dateExport = ZonedDateTime.now(),
            dateDebCrise = crise.criseDateDebut,
            dateClotureCrise = crise.criseDateFin,
            evenement = evenementRepository.getAllEvents(criseId = criseId, dateDebExtraction = dateDebExtraction, dateFinExtraction = dateFinExtraction),
            listeCommune = crise.listeCommuneId?.map { id -> communeRepository.getById(id).communeLibelle }?.distinct() ?: emptyList(),
            listeMessage = messageRepository.getAllMessages(),
            hasMessage = hasMessage,
        )

        documentUtils.createFile(generateHTML(elementHtml), "index.html", GlobalConstants.DOSSIER_DOCUMENT_CRISE)

        val docs = criseRepository.getAllDocumentsFromCrise(criseId = criseId, params = null)
        val files = mutableListOf(
            GlobalConstants.DOSSIER_DOCUMENT_CRISE.resolve("index.html").toFile() to "index.html",
        )

        if (hasDoc) {
            files.addAll(
                docs.map { (id, _, repertoire, nom) ->
                    File("$repertoire/$nom") to "$id-$nom"
                },
            )
        }

        val output = StreamingOutput { output ->
            val out = ZipOutputStream(output)
            files.forEach { (file, zipName) ->
                FileInputStream(file).use { fis ->
                    val zipEntry = ZipEntry(zipName)
                    out.putNextEntry(zipEntry)
                    fis.copyTo(out)
                    out.closeEntry()
                }
            }
            out.flush()
            out.close()
        }

        return output
    }

    private fun generateHTML(data: ElementHTML): String {
        val htmlContent = buildString {
            append(
                """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Rapport de Crise - ${data.criseLibelle ?: "Inconnu"}</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 40px;
                    padding: 20px;
                    background-color: #f4f4f4;
                    color: #333;
                }
                h1, h2, h3 {
                    color: #0056b3;
                }
                .container {
                    background: white;
                    padding: 20px;
                    border-radius: 8px;
                    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
                }
                .section {
                    margin-bottom: 20px;
                    padding: 10px;
                    border-left: 5px solid #0056b3;
                    background: #f9f9f9;
                }
                .event {
                    margin-bottom: 10px;
                    padding: 10px;
                    border-left: 5px solid #ff9800;
                    background: #fff3e0;
                }
                .message {
                    margin-left: 20px;
                    padding: 5px;
                    border-left: 3px solid #4caf50;
                    background: #e8f5e9;
                }
                .footer {
                    text-align: center;
                    font-size: 12px;
                    margin-top: 20px;
                    padding: 10px;
                    background: #ddd;
                    border-radius: 5px;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>Rapport de Crise</h1>
                <div class="section">
                    <h2>Informations générales</h2>
                    <p><strong>Nom de la crise :</strong> ${data.criseLibelle ?: "Non spécifié"}</p>
                    <p><strong>Début de la crise :</strong> ${data.dateDebCrise?.let { dateUtils.format(it) } ?: "Aucune"}</p>
                    <p><strong>Clôture de la crise :</strong> ${data.dateClotureCrise?.let { dateUtils.format(it) } ?: "Aucune"}</p>
                    <p><strong>Période d'extraction :</strong> ${dateUtils.format(data.dateDebExtraction)} - ${dateUtils.format(data.dateFinExtraction)}</p>
                    <p><strong>Date d'export :</strong> ${dateUtils.format(data.dateExport)}</p>
                    <p><strong>Communes concernées :</strong> ${data.listeCommune?.joinToString(", ") ?: "Aucune"}</p>
                </div>
   
                <div class="section">
                    <h2>Événements</h2>
                """.trimIndent(),
            )

            data.evenement?.forEach { event ->
                append(
                    """
                <div class="event">
                    <h3>${event.evenementLibelle}</h3>
                    <p><strong>Description :</strong> ${event.evenementDescription}</p>
                    <p><strong>Date constat :</strong> ${event.evenementDateConstat?.let { dateUtils.format(it) } ?: "Aucune"}</p>
                    <p><strong>Origine :</strong> ${event.evenementOrigine}</p>
                    <p><strong>Importance :</strong> ${event.evenementImportance?.let { "★".repeat(it) } ?: "Aucune"}</p>
                    <p><strong>Événement clos :</strong> ${if (event.evenementEstFerme == true) "Oui" else "Non"}</p>
                    ${if (event.evenementDateCloture != null) "<p><strong>Date de clôture :</strong> ${dateUtils.format(event.evenementDateCloture)}</p>" else ""}
                    
                    <h4>Messages associés :</h4>
                    """.trimIndent(),
                )
                // Filtrage des messages associés à l'évènement
                if (data.hasMessage) {
                    val associatedMessages = data.listeMessage.filter { it.messageEvenementId == event.evenementId }
                    associatedMessages.forEach { msg ->
                        append(
                            """
                    <div class="message">
                        <p><strong>Objet :</strong> ${msg.messageObjet}</p>
                        <p><strong>Date constat :</strong> ${msg.messageDateConstat?.let { dateUtils.format(it) } ?: "Aucune"}</p>
                        <p><strong>Description :</strong> ${msg.messageDescription}</p>
                        <p><strong>Origine :</strong> ${msg.messageOrigine}</p>
                        <p><strong>Auteur :</strong> ${msg.messageUtilisateur }</p>
                        <p><strong>Importance :</strong> ${msg.messageImportance?.let { "★".repeat(it) } ?: "Aucune"}</p>
                    </div>
                    <br>
                            """.trimIndent(),
                        )
                    }
                }
                // Fermeture de l'événement
                append("</div>")
            }

            append(
                """
                </div>
                <div class="footer">
                    <p>Généré automatiquement - ${dateUtils.now()}</p>
                </div>
            </div>
        </body>
        </html>
                """.trimIndent(),
            )
        }

        return htmlContent
    }
}
