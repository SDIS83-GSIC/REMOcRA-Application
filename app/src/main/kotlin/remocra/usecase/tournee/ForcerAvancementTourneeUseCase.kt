package remocra.usecase.tournee

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.TourneeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Tournee
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class ForcerAvancementTourneeUseCase @Inject constructor(
    private val tourneeRepository: TourneeRepository,
) : AbstractCUDUseCase<Tournee>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.TOURNEE_FORCER_POURCENTAGE_E)) {
            throw RemocraResponseException(ErrorType.TOURNEE_FORCER_AVANCEMENT_FORBIDDEN)
        }
    }

    override fun postEvent(element: Tournee, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.tourneeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.TOURNEE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Tournee) {
        if (element.tourneeReservationUtilisateurId != null) {
            throw RemocraResponseException(ErrorType.TOURNEE_LECTURE_SEULE)
        }
    }

    override fun execute(userInfo: UserInfo?, element: Tournee): Tournee {
        tourneeRepository.setAvancementTournee(element.tourneeId, element.tourneePourcentageAvancement!!)
        return element
    }
}
