package remocra.usecase.tournee

import jakarta.inject.Inject
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.WrappedUserInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.data.enums.DeltaDate
import remocra.db.TourneeRepository
import remocra.db.TourneeRepository.Filter
import remocra.db.TourneeRepository.Sort
import remocra.usecase.AbstractUseCase
import kotlin.collections.sortBy

class FetchTourneeDataUseCase @Inject constructor(
    private val tourneeRepository: TourneeRepository,
    private val incomingRepository: IncomingRepository,
) : AbstractUseCase() {

    fun fetchTourneeData(
        params: Params<Filter, Sort>,
        userInfo: WrappedUserInfo,
    ): DataTableau<TourneeRepository.TourneeComplete>? {
        val listTourneeComplete = tourneeRepository.getAllTourneeComplete(
            filter = params.filterBy,
            userInfo.isSuperAdmin,
            userInfo.affiliatedOrganismeIds!!,
        )

        val filterTourneeDeltaDate = params.filterBy?.tourneeDeltaDate
        var filteredList = listTourneeComplete
        if (!filterTourneeDeltaDate.isNullOrEmpty()) {
            val today = dateUtils.now()
            val dateLimite = when (filterTourneeDeltaDate) {
                DeltaDate.INF_1_MOIS.toString() -> today.plusMonths(1)
                DeltaDate.INF_2_MOIS.toString() -> today.plusMonths(2)
                DeltaDate.INF_6_MOIS.toString() -> today.plusMonths(6)
                DeltaDate.INF_12_MOIS.toString() -> today.plusMonths(12)
                DeltaDate.INF_24_MOIS.toString() -> today.plusMonths(24)
                else -> null
            }
            filteredList = if (dateLimite != null) {
                listTourneeComplete.filterNot {
                    it.tourneeNextRopDate == null ||
                        it.tourneeNextRopDate!!.isAfter(dateLimite) ||
                        it.tourneeNextRopDate!!.isBefore(today)
                }
            } else {
                listTourneeComplete.filterNot {
                    it.tourneeNextRopDate == null ||
                        it.tourneeNextRopDate!!.isAfter(today)
                }
            }
        }

        // Calcul size pour DataTableau
        val count = filteredList.size
        // Tri en fonction de sortBy
        val sortBy = params.sortBy
        val effectiveSortBy = if (
            sortBy == null ||
            (
                sortBy.tourneeLibelle == null &&
                    sortBy.tourneeNbPei == null &&
                    sortBy.organismeLibelle == null &&
                    sortBy.tourneePourcentageAvancement == null &&
                    sortBy.tourneeUtilisateurReservationLibelle == null &&
                    sortBy.tourneeActif == null &&
                    sortBy.tourneeNextRopDate == null
                )
        ) {
            Sort(
                tourneeLibelle = 1,
                tourneeNbPei = null,
                organismeLibelle = null,
                tourneePourcentageAvancement = null,
                tourneeUtilisateurReservationLibelle = null,
                tourneeActif = null,
                tourneeNextRopDate = null,
            )
        } else {
            sortBy
        }

        val filteredSortedList = effectiveSortBy?.toCondition(filteredList)
        // Application de limit et offset à notre liste
        val filteredShortedList = filteredSortedList?.drop(params.offset ?: 0)?.take(params.limit ?: count)

        val listeTourneeId = filteredShortedList?.map { it.tourneeId }
        val tourneeNonModifiable = tourneeRepository.getTourneeHorsZc(
            userInfo.isSuperAdmin,
            userInfo.zoneCompetence?.zoneIntegrationId,
            listeTourneeId ?: listOf(),
        )

        val tourneeIncomingTerminee = incomingRepository.getTourneeTerminee(listeTourneeId ?: listOf())

        filteredShortedList?.forEach {
            it.isModifiable = !tourneeNonModifiable.contains(it.tourneeId)
            it.estDansIncoming = tourneeIncomingTerminee.contains(it.tourneeId)
        }
        return filteredShortedList?.let { DataTableau(it, count) }
    }
}
