package remocra.usecase.admin.diametre

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DiametreRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import java.time.ZonedDateTime

class UpdateDiametreUseCase @Inject constructor(
    private val diametreRepository: DiametreRepository,
) : AbstractCUDUseCase<Diametre>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_DIAMETRE_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: Diametre, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.diametreId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DIAMETRE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: Diametre): Diametre {
        diametreRepository.edit(element.diametreId, element.diametreCode, element.diametreLibelle, element.diametreActif)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Diametre) {
        if (element.diametreProtected) {
            throw RemocraResponseException(ErrorType.ADMIN_DIAMETRE_IS_PROTECTED)
        }
    }
}
