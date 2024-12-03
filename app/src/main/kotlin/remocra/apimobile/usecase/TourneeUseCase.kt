package remocra.apimobile.usecase

import com.google.inject.Inject
import jakarta.ws.rs.core.Response
import remocra.apimobile.data.TourneeForApiMobileData
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.TourneeRepository
import remocra.db.jooq.remocra.tables.pojos.Tournee
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import java.util.UUID

class TourneeUseCase : AbstractUseCase() {
    @Inject
    lateinit var tourneeRepository: TourneeRepository

    fun getTourneesDisponibles(userInfo: UserInfo) =
        tourneeRepository.getTourneesActives(userInfo.isSuperAdmin, userInfo.affiliatedOrganismeIds, null, true, true)

    fun reserveTournees(listIdTournees: List<UUID>, idUser: UUID): ReservationTourneesResponse {
        val tournees: MutableList<Tournee> = tourneeRepository.getTourneesByIds(listIdTournees).toMutableList()

        val dejaReservees: List<Tournee> = tournees.filter { it.tourneeReservationUtilisateurId != null }

        // On supprime les tournées déjà réservées
        tournees.removeAll(dejaReservees)

        // Puis, on réserve celles qu'on peut
        val result: Int =
            tourneeRepository.reserveTournees(
                tournees.map { it.tourneeId },
                idUser,
            )

        // Si on n'a pas réussi à réserver toutes les tournées
        if (result != tournees.size) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_ERREUR_RESERVATION, tournees.joinToString(", ") { it.tourneeId.toString() + it.tourneeLibelle })
        }

        val mapPeiIdByTournee = tourneeRepository.getListPeiByListTournee(tournees.map { it.tourneeId })

        val listeTourneeAvecPei = tournees.map {
            TourneeForApiMobileData(
                tourneeId = it.tourneeId,
                tourneeOrganismeId = it.tourneeOrganismeId,
                tourneeLibelle = it.tourneeLibelle,
                tourneeReservationUtilisateurId = idUser,
                mapPeiIdByTournee[it.tourneeId]!!.filterNotNull(),
            )
        }

        // On retourne les tournées réservées et celles qu'on n'a pas pu réserver
        return ReservationTourneesResponse(tourneesReservees = listeTourneeAvecPei, tourneesNonReservees = dejaReservees)
    }

    data class ReservationTourneesResponse(val tourneesReservees: List<TourneeForApiMobileData>, val tourneesNonReservees: List<Tournee>)

    fun annuleReservation(idTournee: UUID, idUtilisateur: UUID): Response {
        val estAnnulee: Boolean = tourneeRepository.annuleReservation(idTournee, idUtilisateur)

        return if (estAnnulee) {
            Response.ok().entity("Annulation de la réservation de la tournée réussie").build()
        } else {
            Response.serverError().entity("Impossible d'annuler la réservation").build()
        }
    }
}
