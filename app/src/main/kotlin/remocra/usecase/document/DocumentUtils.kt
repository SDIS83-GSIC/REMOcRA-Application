package remocra.usecase.document

import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import remocra.web.notFound
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import kotlin.io.path.pathString

class DocumentUtils {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * S'assure que le répertoire existe en le créant si besoin.
     *
     * @param repertoire
     * @return le fichier
     * @throws SecurityException si le répertoire n'a pas pu être créé
     */
    @Throws(SecurityException::class)
    fun ensureDirectory(repertoire: String): File {
        val dir = File(repertoire)
        if (!dir.exists()) {
            // Créer le répertoire
            if (!dir.mkdirs()) {
                throw SecurityException("Impossible de créer le répertoire $repertoire")
            }
        }
        return dir
    }

    /**
     * Permet de sauvegarder un fichier sur le serveur
     * @param fileBytes fichier à sauvegarder
     * @param nomFichier nom du fichier à sauvegarder
     * @param chemin chemin sur le serveur
     */
    fun saveFile(fileBytes: ByteArray, nomFichier: String, chemin: String) {
        // Création du répertoire d'accueil si nécessaire
        val repertoire = ensureDirectory(chemin)

        val targetFilePath = Paths.get(chemin, nomFichier).toString()

        if (repertoire.canWrite()) {
            try {
                FileOutputStream(targetFilePath).use { outStream -> outStream.write(fileBytes) }
            } catch (e: IOException) {
                throw java.lang.RuntimeException(e)
            }
        } else {
            throw SecurityException("Impossible de créer le fichier $targetFilePath")
        }
    }

    /**
     * Suppression fichier sur disque du document.
     *
     * @param nomFichier : Nom du fichier
     * @param repertoire : Nom du répertoire
     * @throws Exception
     */
    @Throws(Exception::class)
    fun deleteFile(nomFichier: String, repertoire: String) {
        val fichierPath = Paths.get(repertoire, nomFichier).pathString
        val repertoireFile = File(repertoire)

        if (repertoireFile.canWrite()) {
            // Suppression du fichier
            File(fichierPath).delete()
        } else {
            throw SecurityException("Impossible de supprimer le fichier $fichierPath")
        }
    }

    fun checkFile(file: File): Response {
        if (!file.exists()) {
            logger.error("Le document ${file.path} est introuvable.")
            return notFound().build()
        }
        return Response.ok(file.readBytes())
            .header("Content-Disposition", "attachment; filename=\"${file.name}\"")
            .build()
    }

    /**
     * Suppression d'un répertoire sur disque.
     *
     * @param repertoire : Nom du répertoire
     * @throws Exception
     */
    @Throws(Exception::class)
    fun deleteDirectory(repertoire: String) {
        val repertoireFile = File(repertoire)

        if (repertoireFile.canWrite()) {
            // Suppression du fichier
            repertoireFile.delete()
        } else {
            throw SecurityException("Impossible de supprimer le répertoire $repertoireFile")
        }
    }
}
