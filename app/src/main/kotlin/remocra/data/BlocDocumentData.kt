package remocra.data

import jakarta.servlet.http.Part
import java.util.UUID

data class BlocDocumentData(
    val blocDocumentId: UUID,
    val blocDocumentLibelle: String,
    val blocDocumentDecription: String,
    val listeThematiqueId: Collection<UUID>?,
    val listeProfilDroitId: Collection<UUID>?,
    val document: Part?,
)
