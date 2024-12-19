package remocra.apimobile.data

import remocra.db.jooq.remocra.tables.pojos.LTourneePei
import java.util.UUID

data class TourneeForApiMobileData(
    val tourneeId: UUID,
    val tourneeOrganismeId: UUID,
    val tourneeLibelle: String,
    val tourneeReservationUtilisateurId: UUID,
    val listPeiIdWithOrdre: Collection<LTourneePei>,
)
