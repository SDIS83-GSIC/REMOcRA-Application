package remocra.data

import remocra.db.jooq.remocra.tables.pojos.Permis
import java.time.ZonedDateTime
import java.util.UUID

data class PermisData(
    val permis: Permis,
    val permisCadastreParcelle: List<UUID>,
    val permisDocuments: DocumentsData.DocumentsPermis?,
)

data class PermisDataToFront(
    val permis: Permis,
    val permisDocument: List<DocumentsData.DocumentPermisData>,
    val permisCadastreParcelle: List<UUID>,
    val permisLastUpdateDate: ZonedDateTime?,
    val permisInstructeurUsername: String,
)
