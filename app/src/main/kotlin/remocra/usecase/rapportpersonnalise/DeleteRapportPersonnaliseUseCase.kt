package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.RapportPersonnaliseData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
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

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_RAPPORTS_PERSO)) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_FORBIDDEN)
        }
    }

    override fun postEvent(element: RapportPersonnaliseData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.rapportPersonnaliseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.RAPPORT_PERSONNALISE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: RapportPersonnaliseData): RapportPersonnaliseData {
        // On supprime les profils droit
        rapportPersonnaliseRepository.deleteLRapportPersonnaliseProfilDroit(element.rapportPersonnaliseId)

        rapportPersonnaliseRepository.deleteRapportPersonnaliseParametre(element.rapportPersonnaliseId)

        rapportPersonnaliseRepository.deleteRapportPersonnalise(element.rapportPersonnaliseId)

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: RapportPersonnaliseData) {
        if (element.rapportPersonnaliseProtected) {
            throw RemocraResponseException(ErrorType.RAPPORT_PERSO_IS_PROTECTED)
        }
    }
}
