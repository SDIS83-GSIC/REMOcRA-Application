package remocra.usecase.admin.anomalie

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AnomalieData
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.data.enums.TypeSourceModification
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

class UpdateAnomalieUseCase : AbstractCUDUseCase<AnomalieData>(TypeOperation.UPDATE) {

    @Inject lateinit var anomalieRepository: AnomalieRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_ANOMALIE_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: AnomalieData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.anomalieId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ANOMALIE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
        // Si la nomenclature modifiée fait partie du DataCache
        // Alors MiseAJour du Cache en question
        eventBus.post(DataCacheModifiedEvent(TypeDataCache.ANOMALIE))
    }

    override fun execute(userInfo: UserInfo?, element: AnomalieData): AnomalieData {
        anomalieRepository.updateAnomalie(
            Anomalie(
                anomalieId = element.anomalieId,
                anomalieCode = element.anomalieCode,
                anomalieLibelle = element.anomalieLibelle,
                anomalieCommentaire = element.anomalieCommentaire,
                anomalieAnomalieCategorieId = element.anomalieAnomalieCategorieId,
                anomalieActif = element.anomalieActif,
                anomalieProtected = element.anomalieProtected,
                anomalieRendNonConforme = element.anomalieRendNonConforme,
            ),
        )

        anomalieRepository.deletePoidsAnomalieByAnomalieId(element.anomalieId, element.poidsAnomalieList?.map { it -> it.poidsAnomalieId })

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

    override fun checkContraintes(userInfo: UserInfo?, element: AnomalieData) {}
}
