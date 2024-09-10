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
import remocra.db.jooq.remocra.tables.pojos.LTourneePei
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecases.AbstractCUDUseCase
import java.time.ZonedDateTime
import java.util.UUID

class UpdateLTourneePeiUseCase @Inject constructor(
    private val tourneeRepository: TourneeRepository,
) : AbstractCUDUseCase<UpdateLTourneePeiUseCase.LTourneePeiToInsert>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.TOURNEE_A)) {
            throw RemocraResponseException(ErrorType.TOURNEE_GESTION_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: LTourneePeiToInsert) {
        // TODO vérifier que tous les pei qu'on souhaite insérer sont bien de meme nature deci,
        // a défaut public et privé sous convention ET privée et public sous convention
    }

    override fun postEvent(element: LTourneePeiToInsert, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.tourneeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.TOURNEE_PEI,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(clock),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: LTourneePeiToInsert): LTourneePeiToInsert {
        // Le plus simple ici est de vider les infos relatives à notre tournée pour tout réinsérer au propre
        tourneeRepository.deleteLTourneePeiByTourneeId(tourneeId = element.tourneeId)
        if (!element.listLTourneePei.isNullOrEmpty()) {
            tourneeRepository.batchInsertLTourneePei(listeTourneePei = element.listLTourneePei)
        }
        return element
    }

    data class LTourneePeiToInsert(
        val tourneeId: UUID,
        val listLTourneePei: List<LTourneePei>?,
    )
}
