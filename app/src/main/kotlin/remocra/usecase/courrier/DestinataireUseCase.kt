package remocra.usecase.courrier

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DataTableau
import remocra.data.DestinataireData
import remocra.db.CourrierRepository
import remocra.db.OrganismeRepository
import remocra.usecase.AbstractUseCase

class DestinataireUseCase @Inject constructor(
    private val courrierRepository: CourrierRepository,
    private val organismeRepository: OrganismeRepository,
) : AbstractUseCase() {

    fun getDestinataires(
        filterDestinataire: CourrierRepository.FilterDestinataire?,
        sortBy: CourrierRepository.SortDestinataire?,
        limit: Int?,
        offset: Int?,
        useZoneCompetence: Boolean,
        userInfo: WrappedUserInfo,
    ): DataTableau<DestinataireData> {
        // on va récupérer les organismes concernés si le booléen est à vrai :
        val filter = if (!useZoneCompetence || userInfo.isSuperAdmin) {
            filterDestinataire
        } else {
            filterDestinataire?.copy(listeIdOrganismeByZC = organismeRepository.getAllOrganismesByZoneCompetence(userInfo.zoneCompetence!!.zoneIntegrationId))
        }
        return DataTableau(
            list = courrierRepository.getAllDestinataires(filter, sortBy, limit, offset),
            count = courrierRepository.countDestinataire(filter),
        )
    }
}
