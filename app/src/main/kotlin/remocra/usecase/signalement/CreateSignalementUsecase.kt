package remocra.usecase.signalement

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.WrappedUserInfo
import remocra.data.SignalementData
import remocra.data.enums.ErrorType
import remocra.db.SignalementRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.EtatSignalement
import remocra.db.jooq.remocra.tables.pojos.Signalement
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import remocra.utils.calculerCentroide

class CreateSignalementUsecase @Inject constructor(
    private val createSignalementElementUsecase: CreateSignalementElementUsecase,
    private val signalementRepository: SignalementRepository,
) :
    AbstractCUDGeometrieUseCase<SignalementData>(TypeOperation.INSERT) {

    override fun getListGeometrie(element: SignalementData): Collection<Geometry> {
        return element.listSignalementElement.map { e -> e.geometry }
    }

    override fun ensureSrid(element: SignalementData): SignalementData {
        if (element.listSignalementElement.any { g -> g.geometry.srid != appSettings.srid }) {
            return element.copy(
                listSignalementElement = element.listSignalementElement.map {
                        signalement ->
                    signalement.copy(geometry = transform(signalement.geometry))
                },
            )
        }
        return element
    }

    override fun execute(userInfo: WrappedUserInfo, element: SignalementData): SignalementData {
        signalementRepository.insertSignalement(
            Signalement(
                signalementDescription = element.description,
                signalementId = element.signalementId,
                signalementUtilisateur = userInfo.utilisateurId!!,
                signalementType = EtatSignalement.EN_COURS,
                signalementDateConstat = dateUtils.now(),
                signalementDateModification = null,
                signalementGeometrie = calculerCentroide(element.listSignalementElement.map { it.geometry })!!,
            ),
        )

        element.listSignalementElement.forEach { e ->
            e.apply { e.signalementElementSignalementId = element.signalementId }
            createSignalementElementUsecase.execute(userInfo, e, transactionManager)
        }

        return element
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.SIGNALEMENTS_C)) {
            throw RemocraResponseException(ErrorType.SIGNALEMENT_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: SignalementData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.signalementId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.SIGNALEMENT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: SignalementData) {
        // no-op pas de contrainte
    }
}
