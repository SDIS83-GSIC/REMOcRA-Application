package remocra.usecases.pei

import remocra.auth.UserInfo
import remocra.data.PeiData
import remocra.db.jooq.historique.enums.TypeOperation

class UpdatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.UPDATE) {

    override fun executeSpecific(element: PeiData) {
        upsertPei(element)
    }

    override fun checkDroits(userInfo: UserInfo) {
        // TODO regarder les droits de l'utilisateur
        //  Dans la v2 "Créer, ouvrir la fiche PEI"
    }
    override fun checkContraintes(element: PeiData) {
        // TODO vérifier que la géométrie est bien dans la zone de compétence de l'utilisateur connecté
    }
}
