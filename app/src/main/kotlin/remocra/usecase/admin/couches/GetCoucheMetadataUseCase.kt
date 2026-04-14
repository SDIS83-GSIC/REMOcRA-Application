package remocra.usecase.admin.couches

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheMetadataWithLibelle
import remocra.data.Params
import remocra.data.ResponseCouche
import remocra.db.CoucheMetadataRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class GetCoucheMetadataUseCase
@Inject
constructor(
    private val coucheMetadataRepository: CoucheMetadataRepository,
) :
    AbstractUseCase() {

    fun getCouchesMetadataForTableau(params: Params<CoucheMetadataRepository.FilterCoucheMetadata, CoucheMetadataRepository.SortCouche>): List<ResponseCouche> {
        return coucheMetadataRepository.getCouchesMetadataForTableau(params)
    }

    fun getAvailableCoucheMetadata(couchesIds: Set<UUID>, userInfo: WrappedUserInfo): List<CoucheMetadataWithLibelle> {
        val publiques = coucheMetadataRepository.getPublicCoucheMetadata(couchesIds)
        return if (userInfo.userInfo == null) {
            publiques
        } else {
            val groupe = userInfo.userInfo
                ?.groupeFonctionnalites
                ?.groupeFonctionnalitesId
                ?.let { coucheMetadataRepository.getAvailableCoucheMetadataByUserId(it, couchesIds) }
                ?: emptyList()
            return (publiques + groupe).distinctBy { it.coucheId }
        }
    }
}
