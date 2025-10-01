package remocra.data.risque

import java.io.InputStream
import java.util.UUID

/**
 * Données pour l'import des risques depuis un fichier KML.
 */
data class ImportRisqueKmlData(
    // Ne sera renseigné qu'au retour de la méthode execute, pour alimenter la traçabilité
    val risqueId: UUID?,
    val risqueLibelle: String?,
    val fileKml: InputStream?,

)
