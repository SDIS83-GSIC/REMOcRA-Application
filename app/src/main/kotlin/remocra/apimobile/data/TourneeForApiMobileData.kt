package remocra.apimobile.data

import java.util.UUID

data class TourneeForApiMobileData(
    val tourneeId: UUID,
    val tourneeOrganismeId: UUID,
    val tourneeLibelle: String,
    val tourneeReservationUtilisateurId: UUID,
    val listPeiId: Collection<UUID>,
)
