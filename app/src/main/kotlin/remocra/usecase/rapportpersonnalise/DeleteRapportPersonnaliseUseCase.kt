package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.RapportPersonnaliseData
import remocra.data.enums.ErrorType
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteRapportPersonnaliseUseCase : AbstractCUDUseCase<RapportPersonnaliseData>(TypeOperation.DELETE) {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_RAPPORTS_PERSO)) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_FORBIDDEN)
        }
    }

    override fun postEvent(element: RapportPersonnaliseData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.rapportPersonnaliseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.RAPPORT_PERSONNALISE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: RapportPersonnaliseData): RapportPersonnaliseData {
        // On supprime les groupes de fonctionnalités
        rapportPersonnaliseRepository.deleteLRapportPersonnaliseGroupeFonctionnalites(element.rapportPersonnaliseId)

        rapportPersonnaliseRepository.deleteRapportPersonnaliseParametre(element.rapportPersonnaliseId)

        rapportPersonnaliseRepository.deleteRapportPersonnalise(element.rapportPersonnaliseId)

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: RapportPersonnaliseData) {
        if (element.rapportPersonnaliseProtected) {
            throw RemocraResponseException(ErrorType.RAPPORT_PERSO_IS_PROTECTED)
        }
    }
}
