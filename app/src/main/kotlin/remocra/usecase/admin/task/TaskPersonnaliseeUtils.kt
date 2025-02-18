package remocra.usecase.admin.task

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.data.TaskPersonnaliseeInputData
import remocra.tasks.ApacheHopTask.ApacheHopParametre
import remocra.usecase.admin.task.UpdateTaskPersonnaliseeUseCase.ConfigApacheHop
import remocra.usecase.document.DocumentUtils
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class TaskPersonnaliseeUtils {

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Inject
    lateinit var objectMapper: ObjectMapper

    fun saveTask(element: TaskPersonnaliseeInputData) {
        val directory = GlobalConstants.DOSSIER_APACHE_HOP_TASK + "${element.taskId}/"
        var nameHwf: String? = null
        // On supprime le contenu du répertoire
        documentUtils.deleteDirectory(directory)

        ZipInputStream(element.zip!!).use { zipInputStream ->
            var zipEntry: ZipEntry? = zipInputStream.nextEntry

            while (zipEntry != null) {
                documentUtils.ensureDirectory(directory)

                val file = File(directory + zipEntry.name)

                zipInputStream.readBytes().inputStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                if (zipEntry.name.contains(".hwf")) {
                    nameHwf = file.name
                }
                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
        }

        // puis on crée un json qui devra avoir la configuration du job avec son name, enabled et sa position
        val parametre = objectMapper.readValue(element.taskParametres.toString(), ApacheHopParametre::class.java)
        val config = ConfigApacheHop(
            name = parametre.taskCode,
            filename = "\${PROJECT_HOME}/${element.taskId}/$nameHwf",
        )

        File(GlobalConstants.DOSSIER_APACHE_HOP_CONFIG + "${parametre.taskCode}.json").printWriter().use { out ->
            out.println(objectMapper.writeValueAsString(config))
        }
    }
}
