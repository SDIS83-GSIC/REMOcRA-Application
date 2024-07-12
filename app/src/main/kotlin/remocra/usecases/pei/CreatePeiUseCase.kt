package remocra.usecases.pei

import remocra.authn.UserInfo
import remocra.data.PeiData
import remocra.db.jooq.historique.enums.TypeOperation

class CreatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.INSERT) {

    override fun executeSpecific(element: PeiData) {
        upsertPei(element)
    }

    override fun checkDroits(userInfo: UserInfo) {
        // TODO check les droits
    }

    override fun checkContraintes(element: PeiData) {
        // Aucune contrainte
    }
}
