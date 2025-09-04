package remocra.usecase.admin.couches

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheStyleInput
import remocra.data.enums.ErrorType
import remocra.db.CoucheRepository
import remocra.db.GroupeFonctionnalitesRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class StyleCoucheUseCase : AbstractCUDUseCase<CoucheStyleInput>(TypeOperation.INSERT) {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var groupeFonctionnalitesRepository: GroupeFonctionnalitesRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.CARTO_METADATA_A)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES)
        }
    }

    override fun postEvent(element: CoucheStyleInput, userInfo: WrappedUserInfo) {}

    override fun execute(userInfo: WrappedUserInfo, element: CoucheStyleInput): CoucheStyleInput {
        // insertion
        coucheRepository.upsertCoucheStyle(element)
        if (element.layerProfilId != null) {
            for (id in element.layerProfilId) {
                if (element.layerStyleId != null) {
                    groupeFonctionnalitesRepository.addGroupeFonctionnalitesCoucheStyle(element.layerStyleId, id)
                }
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CoucheStyleInput) {
        if (element.layerProfilId != null) {
            for (id in element.layerProfilId) {
                if (element.layerStyleId != null) {
                    if (groupeFonctionnalitesRepository.checkGroupeFonctionnaliteCoucheExist(element.layerStyleId, id)) {
                        throw RemocraResponseException(ErrorType.GROUPE_FONCTIONNALITES_COUCHE_UNIQUE)
                    }
                }
            }
        }
    }
}
