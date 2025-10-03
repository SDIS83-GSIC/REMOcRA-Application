package remocra.usecase.admin.couches

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheMetadata
import remocra.data.Params
import remocra.data.ResponseCouche
import remocra.db.CoucheMetadataRepository
import remocra.usecase.AbstractUseCase

class GetCoucheMetadataUseCase : AbstractUseCase() {
    @Inject lateinit var coucheMetadataRepository: CoucheMetadataRepository

    fun getCouchesMetadataForTableau(params: Params<CoucheMetadataRepository.FilterCoucheMetadata, CoucheMetadataRepository.SortCouche>): List<ResponseCouche> {
        return coucheMetadataRepository.getCouchesMetadataForTableau(params)
    }

    fun getAllCoucheMetadata(userInfo: WrappedUserInfo): List<CoucheMetadata> {
        if (userInfo.userInfo == null) {
            // non connecté
            return coucheMetadataRepository.getPublicCoucheMetadata()
        }
        return userInfo.userInfo
            ?.groupeFonctionnalites
            ?.groupeFonctionnalitesId
            ?.let { coucheMetadataRepository.getAllCoucheMetadataByUserId(it) }
            ?: emptyList()
    }
}
