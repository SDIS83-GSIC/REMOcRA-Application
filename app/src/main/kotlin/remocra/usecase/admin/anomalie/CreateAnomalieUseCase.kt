package remocra.usecase.admin.anomalie

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.AnomalieData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.db.AnomalieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.eventbus.datacache.DataCacheModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateAnomalieUseCase : AbstractCUDUseCase<AnomalieData>(TypeOperation.INSERT) {

    @Inject lateinit var anomalieRepository: AnomalieRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_ANOMALIES)) {
            throw RemocraResponseException(ErrorType.ADMIN_ANOMALIE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: AnomalieData, userInfo: WrappedUserInfo) {
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

    override fun execute(userInfo: WrappedUserInfo, element: AnomalieData): AnomalieData {
        anomalieRepository.insertAnomalie(
            Anomalie(
                anomalieId = element.anomalieId,
                anomalieCode = element.anomalieCode,
                anomalieLibelle = element.anomalieLibelle,
                anomalieCommentaire = element.anomalieCommentaire,
                anomalieAnomalieCategorieId = element.anomalieAnomalieCategorieId,
                anomalieActif = element.anomalieActif,
                anomalieProtected = element.anomalieProtected,
                anomalieRendNonConforme = element.anomalieRendNonConforme,
                anomaliePoidsAnomalieSystemeValIndispoTerrestre = element.poidsAnomalieSystemeValIndispoTerrestre,
                anomaliePoidsAnomalieSystemeValIndispoHbe = element.poidsAnomalieSystemeValIndispoHbe,
                anomalieOrdre = 0, // par défaut
            ),
        )
        element.poidsAnomalieList?.filter { p -> !p.isEmpty }?.forEach {
                poidsAnomalie ->
            anomalieRepository.upsertPoidsAnomalie(
                PoidsAnomalie(
                    poidsAnomalieId = poidsAnomalie.poidsAnomalieId,
                    poidsAnomalieAnomalieId = element.anomalieId,
                    poidsAnomalieNatureId = poidsAnomalie.poidsAnomalieNatureId,
                    poidsAnomalieTypeVisite = poidsAnomalie.poidsAnomalieTypeVisite,
                    poidsAnomalieValIndispoHbe = poidsAnomalie.poidsAnomalieValIndispoHbe,
                    poidsAnomalieValIndispoTerrestre = poidsAnomalie.poidsAnomalieValIndispoTerrestre,
                ),
            )
        }

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: AnomalieData) {}
}
