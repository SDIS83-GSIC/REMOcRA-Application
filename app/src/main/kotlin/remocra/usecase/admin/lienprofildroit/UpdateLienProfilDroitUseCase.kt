package remocra.usecase.admin.lienprofildroit

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.ProfilDroitData
import remocra.data.enums.ErrorType
import remocra.db.ProfilDroitRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.ProfilDroit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateLienProfilDroitUseCase @Inject constructor(private val profilDroitRepository: ProfilDroitRepository) :
    AbstractCUDUseCase<Collection<ProfilDroitData>>(
        TypeOperation.UPDATE,
    ) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_GROUPE_UTILISATEUR)) {
            throw RemocraResponseException(ErrorType.PROFIL_DROIT_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: Collection<ProfilDroitData>, userInfo: UserInfo) { }

    override fun execute(userInfo: UserInfo?, element: Collection<ProfilDroitData>): Collection<ProfilDroitData> {
        element.forEach { profilDroit ->
            profilDroitRepository.updateDroits(
                ProfilDroit(
                    profilDroitId = profilDroit.profilDroitId,
                    profilDroitLibelle = profilDroit.profilDroitLibelle,
                    profilDroitCode = profilDroit.profilDroitCode,
                    profilDroitActif = profilDroit.profilDroitActif,
                    profilDroitDroits = profilDroit.profilDroitDroits,
                ),
            )
        }
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Collection<ProfilDroitData>) { }
}
