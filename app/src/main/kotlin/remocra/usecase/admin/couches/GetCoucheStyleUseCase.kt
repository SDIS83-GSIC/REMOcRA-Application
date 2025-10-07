package remocra.usecase.admin.couches

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheStyle
import remocra.data.Params
import remocra.data.ResponseCouche
import remocra.db.CoucheRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class GetCoucheStyleUseCase : AbstractUseCase() {
    @Inject lateinit var coucheRepository: CoucheRepository

    fun getCouchesParams(params: Params<CoucheRepository.FilterLayerStyle, CoucheRepository.SortLayer>): List<ResponseCouche> {
        return coucheRepository.getCouchesParams(params)
    }

    fun getAllStyles(userInfo: WrappedUserInfo): List<CoucheStyle> {
        if (userInfo.userInfo == null) {
            // non connecté
            return coucheRepository.getAllPublicStyles()
        }

        return userInfo.userInfo
            ?.groupeFonctionnalites
            ?.groupeFonctionnalitesId
            ?.let { coucheRepository.getAllStylesByUserId(it) }
            ?: emptyList()
    }

    fun getStyleById(styleId: UUID): CoucheStyle? {
        return coucheRepository.getStyleById(styleId)
    }

    fun getCountStyles(filterBy: CoucheRepository.FilterLayerStyle?) =
        coucheRepository.getCountStyles(filterBy)
}
