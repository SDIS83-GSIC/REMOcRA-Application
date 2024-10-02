package remocra.data.couverturehydraulique

import java.io.InputStream
import java.util.UUID

data class ReseauBatimentPeiProjet(
    val etudeId: UUID,
    val fileReseau: InputStream?,
    val fileBatiment: InputStream?,
    val filePeiProjet: InputStream?,
)
