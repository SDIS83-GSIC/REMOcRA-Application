package remocra.data

import java.util.UUID

data class PeiAvecTournees(
    val peiId: UUID,
    val peiNumeroComplet: String?,
    val tournees: List<TourneeInfo>,
)

data class TourneeInfo(
    val idTournee: UUID,
    val libelleTournee: String?,
    val organismeTournee: String?,
)
