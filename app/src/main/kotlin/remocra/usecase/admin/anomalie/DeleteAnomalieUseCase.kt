package remocra.usecase.admin.anomalie

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.db.AnomalieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.eventbus.datacache.DataCacheModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteAnomalieUseCase : AbstractCUDUseCase<Anomalie>(TypeOperation.DELETE) {

    @Inject
    lateinit var anomalieRepository: AnomalieRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_ANOMALIES)) {
            throw RemocraResponseException(ErrorType.ADMIN_ANOMALIE_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: Anomalie, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.anomalieId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ANOMALIE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
        // Si la nomenclature modifiée fait partie du DataCache
        // Alors MiseAJour du Cache en question
        eventBus.post(DataCacheModifiedEvent(TypeDataCache.ANOMALIE))
    }

    override fun execute(userInfo: WrappedUserInfo, element: Anomalie): Anomalie {
        anomalieRepository.deletePoidsAnomalieByAnomalieId(element.anomalieId)
        anomalieRepository.deleteAnomalie(element.anomalieId)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Anomalie) {
        if (element.anomalieProtected) throw RemocraResponseException(ErrorType.ADMIN_ANOMALIE_IS_PROTECTED)
        if (anomalieRepository.isAnomalieInUse(element.anomalieId)) throw RemocraResponseException(ErrorType.ADMIN_ANOMALIE_IN_USE)
    }
}
