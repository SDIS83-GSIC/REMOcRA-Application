package remocra.data.risque

import java.io.InputStream

/**
 * Données pour l'import des risques depuis un fichier KML.
 */
data class ImportRisqueKmlData(
    val fileKml: InputStream?,

)
