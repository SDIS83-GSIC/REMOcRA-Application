package remocra.usecase.nature

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.NatureRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import java.time.ZonedDateTime

class CreateNatureUseCase @Inject constructor(private val natureRepository: NatureRepository) :
    AbstractCUDUseCase<Nature>(TypeOperation.INSERT) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_NATURE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: Nature, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.natureId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.NATURE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: Nature): Nature {
        natureRepository.add(element)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Nature) {
        // rien à faire
    }
}
