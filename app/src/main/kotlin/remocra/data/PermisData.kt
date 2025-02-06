package remocra.data

import remocra.db.jooq.remocra.tables.pojos.Permis
import java.util.UUID

data class PermisData(
    val permis: Permis,
    val permisCadastreParcelle: List<UUID>,
)
