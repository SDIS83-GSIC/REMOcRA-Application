package remocra.usecase.admin

import jakarta.inject.Inject
import jakarta.servlet.http.Part
import jakarta.ws.rs.ForbiddenException
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Usecase permettant de gérer les imports des différentes ressources (graphiques) via l'admin.
 * Certains "contrats" sont mis en place pour simplifier l'utilisation dans l'application, autant sur le répertoire
 * contenant la ressource que sur son nommage.
 */
class ImportRessourcesUseCase : AbstractUseCase() {

    @Inject
    lateinit var documentUtils: DocumentUtils

    fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_PARAM_APPLI)) {
            throw RemocraResponseException(ErrorType.ADMIN_IMPORT_RESSOURCE_FORBIDDEN)
        }
    }

    /**
     * On importe la bannière avec un nom figé pour simplifier le templating ultérieur
     */
    fun importBanniere(userInfo: UserInfo?, bannierePart: Part) {
        // TODO ne plus rendre nullable lorsque tous les cas d'utilisation seront développés !
        if (userInfo == null) {
            throw ForbiddenException()
        }
        checkDroits(userInfo)

        documentUtils.saveFile(bannierePart.inputStream.readAllBytes(), "banniere", GlobalConstants.DOSSIER_IMAGES_RESSOURCES)
    }

    /**
     * On importe le logo avec un nom figé pour simplifier le templating ultérieur
     */
    fun importLogo(userInfo: UserInfo?, logoPart: Part) {
        // TODO ne plus rendre nullable lorsque tous les cas d'utilisation seront développés !
        if (userInfo == null) {
            throw ForbiddenException()
        }
        checkDroits(userInfo)

        documentUtils.saveFile(logoPart.inputStream.readAllBytes(), "logo", GlobalConstants.DOSSIER_IMAGES_RESSOURCES)
    }

    fun importSymbologie(userInfo: UserInfo?, symbologiePart: Part) {
        // TODO ne plus rendre nullable lorsque tous les cas d'utilisation seront développés !
        if (userInfo == null) {
            throw ForbiddenException()
        }
        checkDroits(userInfo)

        ZipInputStream(symbologiePart.inputStream).use { zipInputStream ->
            var zipEntry: ZipEntry? = zipInputStream.nextEntry

            while (zipEntry != null) {
                documentUtils.ensureDirectory(GlobalConstants.DOSSIER_IMAGES_SYMBOLOGIE)

                val file = File(GlobalConstants.DOSSIER_IMAGES_SYMBOLOGIE + zipEntry.name)

                zipInputStream.readBytes().inputStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
        }
    }
}
