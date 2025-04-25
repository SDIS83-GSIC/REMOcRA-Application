package remocra.usecase.admin.profildroit

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.ProfilDroitData
import remocra.data.enums.ErrorType
import remocra.db.ProfilDroitRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.ProfilDroit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateProfilDroitUseCase @Inject constructor(private val profilDroitRepository: ProfilDroitRepository) :
    AbstractCUDUseCase<ProfilDroitData>(
        TypeOperation.UPDATE,
    ) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A)) {
            throw RemocraResponseException(ErrorType.PROFIL_DROIT_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: ProfilDroitData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.profilDroitId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PROFIL_DROIT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: ProfilDroitData): ProfilDroitData {
        profilDroitRepository.update(
            ProfilDroit(
                profilDroitId = element.profilDroitId,
                profilDroitLibelle = element.profilDroitLibelle,
                profilDroitCode = element.profilDroitCode,
                profilDroitActif = element.profilDroitActif,
                profilDroitDroits = element.profilDroitDroits,
            ),
        )
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ProfilDroitData) { }
}
