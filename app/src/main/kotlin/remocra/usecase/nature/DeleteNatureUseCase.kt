package remocra.usecase.nature

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.db.NatureRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.eventbus.datacache.DataCacheModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteNatureUseCase @Inject constructor(private val natureRepository: NatureRepository) :
    AbstractCUDUseCase<Nature>(TypeOperation.DELETE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NATURE_FORBIDDEN_REMOVAL)
        }
    }

    override fun postEvent(element: Nature, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.natureId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.NATURE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
        // Si la nomenclature modifiée fait partie du DataCache
        // Alors MiseAJour du Cache en question
        eventBus.post(DataCacheModifiedEvent(TypeDataCache.NATURE))
    }

    override fun execute(userInfo: WrappedUserInfo, element: Nature): Nature {
        natureRepository.remove(element.natureId)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Nature) {
        if (element.natureProtected!!) {
            throw RemocraResponseException(ErrorType.ADMIN_NATURE_IS_PROTECTED)
        }
    }
}
