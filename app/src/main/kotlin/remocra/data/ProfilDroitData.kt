package remocra.data

import remocra.db.jooq.remocra.enums.Droit
import java.util.UUID

data class ProfilDroitData(
    val profilDroitId: UUID = UUID.randomUUID(),
    val profilDroitCode: String,
    val profilDroitLibelle: String,
    val profilDroitActif: Boolean,
    val profilDroitDroits: Array<Droit?> = arrayOf(),
)
