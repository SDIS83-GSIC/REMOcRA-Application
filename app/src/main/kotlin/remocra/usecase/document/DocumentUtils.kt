package remocra.usecase.document

import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import remocra.utils.DateUtils
import remocra.utils.notFound
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.FileTime
import java.time.ZonedDateTime
import kotlin.io.path.pathString

class DocumentUtils {

    @Inject lateinit var dateUtils: DateUtils

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

        if (!repertoire.canWrite()) {
            throw IOException("Impossible d'écrire dans le répertoire $chemin")
        }

        FileOutputStream(targetFilePath).use { outStream -> outStream.write(fileBytes) }
    }

    /**
     * Permet de créer un fichier à partir d'un contenu et de le sauvegarder sur le serveur
     * @param content le contenu du fichier à sauvegarder
     * @param nomFichier nom du fichier à sauvegarder
     * @param chemin chemin sur le serveur
     */
    fun createFile(content: String, nomFichier: String, chemin: String) {
        val targetFilePath = Paths.get(chemin, nomFichier)
        try {
            // Création et écriture dans le fichier avec un encodage (UTF-8)
            Files.newBufferedWriter(targetFilePath, StandardCharsets.UTF_8).use { writer ->
                writer.write(content)
            }
        } catch (e: IOException) {
            throw RuntimeException("Erreur lors de la création ou de l'écriture dans le fichier $targetFilePath", e)
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
        val fichierPath = Paths.get(repertoire, nomFichier)
        val repertoireFile = File(repertoire)

        if (Files.exists(fichierPath)) {
            if (repertoireFile.canWrite()) {
                // Suppression du fichier s'il existe
                File(fichierPath.toUri()).delete()
            } else {
                throw SecurityException("Impossible de supprimer le fichier ${fichierPath.pathString}")
            }
        } else {
            logger.warn("Fichier ${fichierPath.pathString} non présent sur le disque")
        }
    }

    /**
     * Déplace un fichier sur disque.
     *
     * @param nomFichier : Nom du fichier
     * @param repertoireSource : Nom du répertoire source
     * @param repertoireDestination : Nom du répertoire destination
     * @throws Exception
     */
    @Throws(Exception::class)
    fun moveFile(nomFichier: String, repertoireSource: String, repertoireDestination: String) {
        val fichierPath = Paths.get(repertoireSource, nomFichier)

        val repertoire = ensureDirectory(repertoireDestination)
        if (Files.exists(fichierPath)) {
            Files.move(fichierPath, Paths.get(repertoireDestination, nomFichier))
        } else {
            logger.warn("Fichier ${fichierPath.pathString} non présent sur le disque")
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

        if (!repertoireFile.exists()) {
            return
        }

        if (repertoireFile.canWrite()) {
            repertoireFile.deleteRecursively()
        } else {
            throw SecurityException("Impossible de supprimer le répertoire $repertoireFile")
        }
    }

    fun cleanDirectoryFileOlderThan(repertoire: String, fileOlderThan: ZonedDateTime) {
        val repertoireFile = File(repertoire)
        if (!repertoireFile.exists()) {
            throw IllegalArgumentException("Le répertoire n'existe pas : ${repertoireFile.absolutePath}")
        }
        if (!repertoireFile.isDirectory) {
            throw IllegalArgumentException("Le fichier n'est pas un répertoire : ${repertoireFile.absolutePath}")
        }
        repertoireFile.listFiles()?.forEach { file ->
            val fileLastModificationTime: FileTime = Files.getLastModifiedTime(file.toPath())
            if (fileLastModificationTime.toInstant().isBefore(fileOlderThan.toInstant())) {
                if (!file.delete()) {
                    throw IOException("Impossible de supprimer le fichier ! ${file.absolutePath}")
                }
            }
        } ?: throw IOException("Impossible de lister le contenu du répertoire : ${repertoireFile.absolutePath}")
    }
}
