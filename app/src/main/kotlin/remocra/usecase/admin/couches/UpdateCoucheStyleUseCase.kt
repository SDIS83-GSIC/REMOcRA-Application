package remocra.usecase.admin.couches

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheStyleInput
import remocra.data.enums.ErrorType
import remocra.db.CoucheRepository
import remocra.db.GroupeFonctionnalitesRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateCoucheStyleUseCase : AbstractCUDUseCase<CoucheStyleInput>(TypeOperation.UPDATE) {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var groupeFonctionnalitesRepository: GroupeFonctionnalitesRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.CARTO_METADATA_A)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES)
        }
    }

    override fun postEvent(element: CoucheStyleInput, userInfo: WrappedUserInfo) {
    }

    override fun execute(userInfo: WrappedUserInfo, element: CoucheStyleInput): CoucheStyleInput {
        if (element.layerStyleId != null) {
            // supprimer tout id dans L_PROFIL_DROIT_COUCHE
            groupeFonctionnalitesRepository.deleteLGroupeFonctionnalitesCouche(element.layerStyleId)
            coucheRepository.upsertCoucheStyle(element)

            if (element.layerProfilId != null) {
                for (id in element.layerProfilId) {
                    groupeFonctionnalitesRepository.addGroupeFonctionnalitesCoucheStyle(element.layerStyleId, id)
                }
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CoucheStyleInput) {}
}
