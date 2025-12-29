package remocra.usecase.admin.couches

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheFormDataWithImage
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.db.CoucheRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.datacache.DataCacheModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteCoucheUseCase : AbstractCUDUseCase<CoucheFormDataWithImage>(TypeOperation.DELETE) {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var objectMapper: ObjectMapper

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_COUCHE_CARTOGRAPHIQUE)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES)
        }
    }

    override fun postEvent(element: CoucheFormDataWithImage, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.coucheFormData,
                pojoId = element.coucheFormData.coucheId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.COUCHE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
        eventBus.post(DataCacheModifiedEvent(TypeDataCache.COUCHE))
    }

    override fun execute(userInfo: WrappedUserInfo, element: CoucheFormDataWithImage): CoucheFormDataWithImage {
        coucheRepository.clearGroupeFonctionnalites(element.coucheFormData.coucheId)
        coucheRepository.clearModule(element.coucheFormData.coucheId)

        coucheRepository.deleteCouche(element.coucheFormData.coucheId)

        return element.copy(icone = null, legende = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CoucheFormDataWithImage) {
        if (coucheRepository.checkCodeExists(element.coucheFormData.coucheCode, element.coucheFormData.coucheId)) {
            throw RemocraResponseException(
                ErrorType.ADMIN_COUCHES_CODE_UNIQUE,
            )
        }

        if (element.coucheFormData.coucheProtected) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHE_IS_PROTECTED)
        }
    }
}
