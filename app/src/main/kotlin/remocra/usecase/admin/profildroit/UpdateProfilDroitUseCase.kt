package remocra.usecase.admin.profildroit

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.ProfilDroitData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
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
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.PROFIL_DROIT_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: ProfilDroitData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.profilDroitId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PROFIL_DROIT,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: ProfilDroitData): ProfilDroitData {
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

    override fun checkContraintes(userInfo: UserInfo?, element: ProfilDroitData) { }
}
