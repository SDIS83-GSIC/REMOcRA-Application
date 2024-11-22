package remocra.data

import java.io.InputStream

/**
 * Data class permettant l'import de N géométries au travers d'un fichier SHAPE
 */
data class ImportGeometriesCodeLibelleData(val fileGeometries: InputStream)
