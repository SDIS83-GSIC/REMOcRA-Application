package remocra.usecase.admin.couches

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheMetadataWithLibelle
import remocra.data.GroupeFonctionnalites
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

    // Chaque couche non publique reçoit ses GroupeFonctionnalites,
    // et chaque couche publique reçoit une liste vide.
    fun getCouchesMetadataForTableau(
        params: Params<CoucheMetadataRepository.FilterCoucheMetadata, CoucheMetadataRepository.SortCouche>,
    ): List<ResponseCouche> {
        val couches = coucheMetadataRepository.getCouchesMetadata(params)

        val groupesParCouche = coucheMetadataRepository.enrichirCouchesWithGroup(
            couches.filter { !it.coucheMetadataPublic }.map { it.coucheMetadataId },
        ).groupBy { it.coucheMetadataId }

        return couches.map { couche ->
            couche.copy(
                groupeFonctionnaliteList = if (couche.coucheMetadataPublic) {
                    emptyList()
                } else {
                    groupesParCouche[couche.coucheMetadataId]
                        ?.map {
                            GroupeFonctionnalites(
                                groupeFonctionnalitesId = it.groupeFonctionnaliteId,
                                groupeFonctionnalitesCode = it.groupeFonctionnaliteCode,
                                groupeFonctionnalitesLibelle = it.groupeFonctionnaliteLibelle,
                            )
                        } ?: emptyList()
                },
            )
        }
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
