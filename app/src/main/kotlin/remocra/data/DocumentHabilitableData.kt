package remocra.data

import jakarta.servlet.http.Part
import java.util.UUID

data class DocumentHabilitableData(
    val documentHabilitableId: UUID,
    val documentHabilitableLibelle: String?,
    val documentHabilitableDescription: String?,
    val listeThematiqueId: Collection<UUID>?,
    val listeGroupeFonctionnalitesId: Collection<UUID>?,
    val document: Part? = null,
)
