package remocra.usecase.oldebproprietaire

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.data.oldeb.OldebProprietaireForm
import remocra.db.ProprietaireRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.OldebProprietaire
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateProprietaireUseCase @Inject constructor(private val proprietaireRepository: ProprietaireRepository) : AbstractCUDUseCase<OldebProprietaireForm>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.OLDEB_U)) {
            throw RemocraResponseException(ErrorType.OLDEB_PROPRIETAIRE_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: OldebProprietaireForm, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.oldebProprietaireId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.OLDEB_PROPRIETAIRE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: OldebProprietaireForm): OldebProprietaireForm {
        proprietaireRepository.updateProprietaire(
            OldebProprietaire(
                oldebProprietaireId = element.oldebProprietaireId,
                oldebProprietaireOrganisme = element.oldebProprietaireOrganisme,
                oldebProprietaireRaisonSociale = element.oldebProprietaireRaisonSociale.takeIf { element.oldebProprietaireOrganisme },
                oldebProprietaireCivilite = element.oldebProprietaireCivilite,
                oldebProprietaireNom = element.oldebProprietaireNom,
                oldebProprietairePrenom = element.oldebProprietairePrenom,
                oldebProprietaireTelephone = element.oldebProprietaireTelephone,
                oldebProprietaireEmail = element.oldebProprietaireEmail,
                oldebProprietaireNumVoie = element.oldebProprietaireNumVoie,
                oldebProprietaireVoie = element.oldebProprietaireVoie,
                oldebProprietaireLieuDit = element.oldebProprietaireLieuDit,
                oldebProprietaireCodePostal = element.oldebProprietaireCodePostal,
                oldebProprietaireVille = element.oldebProprietaireVille,
                oldebProprietairePays = element.oldebProprietairePays,
            ),
        )
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: OldebProprietaireForm) {
        // no-op
    }
}
