package remocra.usecase.oldeb

import jakarta.inject.Inject
import remocra.app.AppSettings
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.data.oldeb.OldebGeometryFormData
import remocra.db.OldebRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateOldebGeometryUseCase @Inject constructor(
    private val oldebRepository: OldebRepository,
    private val appSettings: AppSettings,
) : AbstractCUDUseCase<OldebGeometryFormData>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.OLDEB_U)) {
            throw RemocraResponseException(ErrorType.OLDEB_GEOMETRY_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: OldebGeometryFormData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.oldebId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.OLDEB,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: OldebGeometryFormData): OldebGeometryFormData {
        oldebRepository.updateOldebGeometry(element.oldebId, element.oldebGeometrie)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: OldebGeometryFormData) {}
}
