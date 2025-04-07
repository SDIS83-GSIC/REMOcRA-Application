package remocra.usecase.tournee

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.data.enums.DeltaDate
import remocra.db.TourneeRepository
import remocra.db.TourneeRepository.Filter
import remocra.db.TourneeRepository.Sort
import remocra.usecase.AbstractUseCase

class FetchTourneeDataUseCase : AbstractUseCase() {
    @Inject lateinit var tourneeRepository: TourneeRepository

    fun fetchTourneeData(
        params: Params<Filter, Sort>,
        userInfo: UserInfo,
    ): DataTableau<TourneeRepository.TourneeComplete>? {
        val listTourneeComplete = tourneeRepository.getAllTourneeComplete(filter = params.filterBy, userInfo.isSuperAdmin, userInfo.affiliatedOrganismeIds)

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
                    it.tourneeNextRecopDate == null ||
                        it.tourneeNextRecopDate!!.isAfter(dateLimite) ||
                        it.tourneeNextRecopDate!!.isBefore(today)
                }
            } else {
                listTourneeComplete.filterNot {
                    it.tourneeNextRecopDate == null ||
                        it.tourneeNextRecopDate!!.isAfter(today)
                }
            }
        }

        // Calcul size pour DataTableau
        val count = filteredList.size
        // Tri en fonction de sortBy
        val filteredSortedList = params.sortBy?.toCondition(filteredList)
        // Application de limit et offset à notre liste
        val filteredShortedList = filteredSortedList?.drop(params.offset ?: 0)?.take(params.limit ?: count)

        val tourneeNonModifiable = tourneeRepository.getTourneeHorsZc(
            userInfo.isSuperAdmin,
            userInfo.zoneCompetence?.zoneIntegrationId,
            filteredShortedList?.map { it.tourneeId } ?: listOf(),
        )
        if (tourneeNonModifiable.isNotEmpty()) {
            filteredShortedList?.forEach {
                it.isModifiable = !tourneeNonModifiable.contains(it.tourneeId)
            }
        }
        return filteredShortedList?.let { DataTableau(it, count) }
    }
}
