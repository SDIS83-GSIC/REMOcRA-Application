package remocra.usecase.indisponibilitetemporaire

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.enums.ErrorType
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.IndisponibiliteTemporaire
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CloreIndisponibiliteTemporaireUseCase
@Inject constructor(
    private val indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository,
) : AbstractCUDUseCase<IndisponibiliteTemporaireData>(TypeOperation.UPDATE) {
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
        val indisponibiliteTemporaire = IndisponibiliteTemporaire(
            indisponibiliteTemporaireId = element.indisponibiliteTemporaireId,
            indisponibiliteTemporaireMotif = element.indisponibiliteTemporaireMotif,
            indisponibiliteTemporaireObservation = element.indisponibiliteTemporaireObservation,
            indisponibiliteTemporaireMailApresIndisponibilite = element.indisponibiliteTemporaireMailApresIndisponibilite,
            indisponibiliteTemporaireMailAvantIndisponibilite = element.indisponibiliteTemporaireMailAvantIndisponibilite,
            indisponibiliteTemporaireDateDebut = element.indisponibiliteTemporaireDateDebut,
            indisponibiliteTemporaireDateFin = dateUtils.now(), // date actuelle pour "clore" l'indispo
            indisponibiliteTemporaireNotificationDebut = element.indisponibiliteTemporaireNotificationDebut,
            indisponibiliteTemporaireNotificationFin = element.indisponibiliteTemporaireNotificationFin,
            indisponibiliteTemporaireNotificationResteIndispo = element.indisponibiliteTemporaireNotificationResteIndispo,
            indisponibiliteTemporaireBasculeDebut = element.indisponibiliteTemporaireBasculeDebut,
            indisponibiliteTemporaireBasculeFin = element.indisponibiliteTemporaireBasculeFin,
        )
        indisponibiliteTemporaireRepository.upsert(indisponibiliteTemporaire)

        indisponibiliteTemporaireRepository.deleteLiaisonByIndisponibiliteTemporaire(element.indisponibiliteTemporaireId)
        element.indisponibiliteTemporaireListePeiId.forEach { peiId ->
            indisponibiliteTemporaireRepository.insertLiaisonIndisponibiliteTemporairePei(
                indisponibiliteTemporaireId = indisponibiliteTemporaire.indisponibiliteTemporaireId,
                peiId,
            )
        }

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: IndisponibiliteTemporaireData) {
        element.indisponibiliteTemporaireDateFin?.let {
            if (element.indisponibiliteTemporaireDateDebut > element.indisponibiliteTemporaireDateFin) {
                throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_FIN_AVANT_DEBUT)
            }
        }
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.INDISPO_TEMP_U)) {
            throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_UPDATE)
        }
    }
}
