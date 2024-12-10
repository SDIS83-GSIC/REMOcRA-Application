package remocra.usecase.oldebproprietaire

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.ProprietaireRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.OldebProprietaire
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteProprietaireUseCase @Inject constructor(private val proprietaireRepository: ProprietaireRepository) : AbstractCUDUseCase<OldebProprietaire>(TypeOperation.DELETE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.OLDEB_D)) {
            throw RemocraResponseException(ErrorType.OLDEB_PROPRIETAIRE_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: OldebProprietaire, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.oldebProprietaireId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.OLDEB_PROPRIETAIRE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: OldebProprietaire): OldebProprietaire {
        proprietaireRepository.deleteProprietaire(element.oldebProprietaireId)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: OldebProprietaire) {
        if (proprietaireRepository.isProprietaireInUse(element.oldebProprietaireId)) {
            throw RemocraResponseException(ErrorType.OLDEB_PROPRIETAIRE_IN_USE)
        }
    }
}
