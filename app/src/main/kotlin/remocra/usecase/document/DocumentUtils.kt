package remocra.usecase.document

import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import remocra.GlobalConstants.DOSSIER_DATA
import remocra.utils.notFound
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.moveTo
import kotlin.io.path.name
import kotlin.io.path.outputStream
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes

class DocumentUtils {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * S'assure que le répertoire existe en le créant si besoin.
     */
    fun ensureDirectory(repertoire: Path): Path = repertoire.createDirectories()

    /**
     * Permet de sauvegarder un fichier sur le serveur
     * @param fileBytes fichier à sauvegarder
     * @param nomFichier nom du fichier à sauvegarder
     * @param chemin chemin sur le serveur
     */
    fun saveFile(fileBytes: ByteArray, nomFichier: String, chemin: Path) {
        val filePath = ensureDirectory(chemin).resolve(nomFichier)
        saveFile(fileBytes, filePath)
    }

    fun saveFile(fileBytes: ByteArray, path: Path) {
        path.writeBytes(fileBytes)
    }

    /**
     * Permet de créer un fichier à partir d'un contenu et de le sauvegarder sur le serveur
     * @param content le contenu du fichier à sauvegarder
     * @param nomFichier nom du fichier à sauvegarder
     * @param chemin chemin sur le serveur
     */
    fun createFile(content: String, nomFichier: String, chemin: Path) {
        val targetFilePath = ensureDirectory(chemin).resolve(nomFichier)
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
     * Permet de sauvegarder un fichier sur le serveur
     * @param inputStream fichier à sauvegarder
     * @param nomFichier nom du fichier à sauvegarder
     * @param chemin chemin sur le serveur
     */
    fun saveFile(inputStream: InputStream, nomFichier: String, chemin: Path) {
        ensureDirectory(chemin).resolve(nomFichier).outputStream().use {
            inputStream.copyTo(it)
        }
    }

    /**
     * Suppression fichier sur disque du document.
     *
     * @param nomFichier : Nom du fichier
     * @param repertoire : Nom du répertoire
     */
    fun deleteFile(nomFichier: String, repertoire: Path) = repertoire.resolve(nomFichier).deleteExisting()

    /**
     * Déplace un fichier sur disque.
     *
     * @param nomFichier : Nom du fichier
     * @param repertoireSource : Nom du répertoire source
     * @param repertoireDestination : Nom du répertoire destination
     * @throws Exception
     */
    @Throws(Exception::class)
    fun moveFile(nomFichier: String, repertoireSource: Path, repertoireDestination: Path) {
        val fichierPath = repertoireSource.resolve(nomFichier)

        if (fichierPath.exists()) {
            fichierPath.moveTo(ensureDirectory(repertoireDestination).resolve(nomFichier))
        } else {
            logger.warn("Fichier ${fichierPath.pathString} non présent sur le disque")
        }
    }

    fun checkFile(file: Path): Response {
        // Si l'utilisateur cherche à remonter l'arborescence, même combat que si le fichier n'existe pas
        if (!file.exists() || !file.startsWith(DOSSIER_DATA)) {
            logger.error("Le document ${file.pathString} est introuvable.")
            return notFound().build()
        }
        return Response.ok(file.toFile())
            .header("Content-Disposition", "attachment; filename=\"${file.name}\"")
            .build()
    }

    /**
     * Suppression d'un répertoire sur disque.
     *
     * @param repertoire : Nom du répertoire
     * @throws Exception
     */
    fun deleteDirectory(repertoire: Path) {
        repertoire.toFile().deleteRecursively()
    }

    fun cleanDirectoryFileOlderThan(repertoireFile: Path, fileOlderThan: ZonedDateTime) {
        val olderThan = fileOlderThan.toInstant()
        repertoireFile.forEachDirectoryEntry {
            if (it.getLastModifiedTime().toInstant().isBefore(olderThan)) {
                it.deleteIfExists()
            }
        }
    }
}
