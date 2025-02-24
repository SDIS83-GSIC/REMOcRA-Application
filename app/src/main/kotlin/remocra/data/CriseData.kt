package remocra.data

import remocra.db.jooq.remocra.enums.TypeCriseStatut
import java.time.ZonedDateTime
import java.util.UUID

data class CriseData(
    val criseId: UUID,
    val criseLibelle: String?,
    val criseDescription: String?,
    val criseDateDebut: ZonedDateTime?,
    val criseDateFin: ZonedDateTime?,
    val criseTypeCriseId: UUID?,
    val criseStatutType: TypeCriseStatut,
    val listeCommuneId: Collection<UUID>?,
    val listeToponymieId: Collection<UUID>?,
)
