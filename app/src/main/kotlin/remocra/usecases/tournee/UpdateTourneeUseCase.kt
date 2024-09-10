package remocra.usecases.tournee

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
import remocra.usecases.AbstractCUDUseCase
import java.time.ZonedDateTime

class UpdateTourneeUseCase @Inject constructor(
    private val tourneeRepository: TourneeRepository,
) : AbstractCUDUseCase<Tournee>(TypeOperation.UPDATE) {

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.TOURNEE_A)) {
            throw RemocraResponseException(ErrorType.TOURNEE_GESTION_FORBIDDEN)
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
                date = ZonedDateTime.now(clock),
            ),
        )
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Tournee) {
        // La paire organisme_id et tournee_libelle doit etre unique
        if (tourneeRepository.tourneeAlreadyExists(tourneeLibelle = element.tourneeLibelle, tourneeOrganismeId = element.tourneeOrganismeId)) {
            throw RemocraResponseException(ErrorType.TOURNEE_ALREADY_EXISTS)
        }
        // Si la tournée est réservée, elle est en lecture seule => impossible de modifier ses informations
        if (element.tourneeReservationUtilisateurId != null) {
            throw RemocraResponseException(ErrorType.TOURNEE_LECTURE_SEULE)
        }
    }

    override fun execute(userInfo: UserInfo?, element: Tournee): Tournee {
        tourneeRepository.updateTourneeLibelle(element.tourneeId, element.tourneeLibelle)
        return element
    }
}
