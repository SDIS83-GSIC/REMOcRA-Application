package remocra.usecases.pei

import com.google.inject.Inject
import remocra.authn.UserInfo
import remocra.data.PeiData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.db.PeiRepository
import remocra.db.jooq.historique.enums.TypeOperation

class UpdatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.UPDATE) {

    @Inject lateinit var peiRepository: PeiRepository

    override fun executeSpecific(element: PeiData) {
        // On sauvegarde le PEI
        peiRepository.update(element)

        // Si c'est un pibi
        if (element is PibiData) {
            peiRepository.updatePibi(element)
        }

        if (element is PenaData) {
            // TODO update pena
        }
    }

    override fun checkDroits(userInfo: UserInfo) {
        // TODO regarder les droits de l'utilisateur
        //  Dans la v2 "Créer, ouvrir la fiche PEI"
    }
    override fun checkContraintes(element: PeiData) {
        // TODO il y a des contraintes ? Aujourd'hui les contraintes sont gérées depuis le front
    }
}
