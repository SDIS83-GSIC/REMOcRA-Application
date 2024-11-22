package remocra.data

import java.io.InputStream

/**
 * Data class permettant l'import de N sites au travers d'un fichier SHAPE
 */
data class ImportSitesData(val fileSites: InputStream)
