package remocra.usecase.indisponibilitetemporaire

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.enums.ErrorType
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteIndisponibiliteTemporaireUseCase @Inject constructor(
    private val indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository,
) : AbstractCUDUseCase<IndisponibiliteTemporaireData>(TypeOperation.DELETE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.INDISPO_TEMP_D)) {
            throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: IndisponibiliteTemporaireData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.indisponibiliteTemporaireId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.INDISPONIBILITE_TEMPORAIRE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: IndisponibiliteTemporaireData): IndisponibiliteTemporaireData {
        indisponibiliteTemporaireRepository.deleteLiaisonByIndisponibiliteTemporaire(indisponibiliteTemporaireId = element.indisponibiliteTemporaireId)
        indisponibiliteTemporaireRepository.delete(element.indisponibiliteTemporaireId)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: IndisponibiliteTemporaireData) {
        if (element.indisponibiliteTemporaireDateDebut.isBefore(dateUtils.now()) &&
            element.indisponibiliteTemporaireDateFin?.isAfter(dateUtils.now()) != false
        ) {
            throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_EN_COURS)
        }
    }
}
