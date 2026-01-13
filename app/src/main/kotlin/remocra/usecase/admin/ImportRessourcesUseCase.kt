package remocra.usecase.admin

import jakarta.inject.Inject
import jakarta.servlet.http.Part
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.io.path.Path
import kotlin.io.path.outputStream
import kotlin.io.path.relativeTo

/**
 * Usecase permettant de gérer les imports des différentes ressources (graphiques) via l'admin.
 * Certains "contrats" sont mis en place pour simplifier l'utilisation dans l'application, autant sur le répertoire
 * contenant la ressource que sur son nommage.
 */
class ImportRessourcesUseCase : AbstractUseCase() {

    @Inject
    lateinit var documentUtils: DocumentUtils

    fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_PARAM_APPLI)) {
            throw RemocraResponseException(ErrorType.ADMIN_IMPORT_RESSOURCE_FORBIDDEN)
        }
    }

    /**
     * On importe la bannière avec un nom figé pour simplifier le templating ultérieur
     */
    fun importBanniere(userInfo: WrappedUserInfo, bannierePart: Part) {
        checkDroits(userInfo)

        bannierePart.inputStream.use {
            documentUtils.saveFile(it, "banniere", GlobalConstants.DOSSIER_IMAGES_RESSOURCES)
        }
    }

    /**
     * On importe le logo avec un nom figé pour simplifier le templating ultérieur
     */
    fun importLogo(userInfo: WrappedUserInfo, logoPart: Part) {
        checkDroits(userInfo)

        logoPart.inputStream.use {
            documentUtils.saveFile(it, "logo", GlobalConstants.DOSSIER_IMAGES_RESSOURCES)
        }
    }

    fun importSymbologie(userInfo: WrappedUserInfo, symbologiePart: Part) {
        checkDroits(userInfo)

        ZipInputStream(symbologiePart.inputStream).use { zipInputStream ->
            var zipEntry: ZipEntry? = zipInputStream.nextEntry

            while (zipEntry != null) {
                documentUtils.ensureDirectory(GlobalConstants.DOSSIER_IMAGES_SYMBOLOGIE)

                // il y a un risque de "sortir" du répertoire donc on "sanitize" un peu zipEntry.name
                // FIXME: le reste du code ne supporte que des fichiers, donc on pourrait:
                //  - skip les zipEntry.isDirectory
                //  - s'assurer qu'on n'a pas de niveau intermédiaire
                //  - utiliser alors Path(zipEntry.name).name
                val file = GlobalConstants.DOSSIER_IMAGES_SYMBOLOGIE.resolve(Path(zipEntry.name).normalize().let { if (it.isAbsolute) it.relativeTo(Path("/")) else it })

                file.outputStream().use {
                    zipInputStream.copyTo(it)
                }

                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
        }
    }

    fun importTemplateExportCtp(userInfo: WrappedUserInfo, templateExportCtpPart: Part) {
        checkDroits(userInfo)
        /** Vérification type du fichier */
        if (!templateExportCtpPart.submittedFileName.lowercase().endsWith(".xlsx")) {
            throw RemocraResponseException(ErrorType.IMPORT_CTP_NOT_XLSX)
        }
        /** Enregistrement sur le disque */
        templateExportCtpPart.inputStream.use {
            documentUtils.saveFile(
                it,
                GlobalConstants.TEMPLATE_EXPORT_CTP_FILE_NAME,
                GlobalConstants.DOSSIER_MODELES_EXPORT_CTP,
            )
        }
    }
}
