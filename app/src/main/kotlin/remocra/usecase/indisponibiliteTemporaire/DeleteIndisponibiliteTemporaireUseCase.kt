package remocra.usecase.indisponibiliteTemporaire

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
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
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.INDISPO_TEMP_D)) {
            throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: IndisponibiliteTemporaireData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.indisponibiliteTemporaireId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.INDISPONIBILITE_TEMPORAIRE,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: IndisponibiliteTemporaireData): IndisponibiliteTemporaireData {
        indisponibiliteTemporaireRepository.deleteLiaisonByIndisponibiliteTemporaire(indisponibiliteTemporaireId = element.indisponibiliteTemporaireId)
        indisponibiliteTemporaireRepository.delete(element.indisponibiliteTemporaireId)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: IndisponibiliteTemporaireData) {
        if (element.indisponibiliteTemporaireDateDebut.isBefore(dateUtils.now()) &&
            element.indisponibiliteTemporaireDateFin?.isAfter(dateUtils.now()) != false
        ) {
            throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_EN_COURS)
        }
    }
}
