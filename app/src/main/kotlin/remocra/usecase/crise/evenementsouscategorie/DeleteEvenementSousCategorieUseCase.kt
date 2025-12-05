package remocra.usecase.crise.evenementsouscategorie

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.EvenementSousCategorieWithComplementData
import remocra.data.enums.ErrorType
import remocra.db.EvenementSousCategorieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteEvenementSousCategorieUseCase @Inject constructor(
    private val evenementSousCategorieRepository: EvenementSousCategorieRepository,
) : AbstractCUDUseCase<EvenementSousCategorieWithComplementData>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_FORBIDDEN_REMOVAL)
        }
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: EvenementSousCategorieWithComplementData,
    ) {
        if (evenementSousCategorieRepository.fetchExistsInEvenement(element.evenementSousCategorieId)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_IMPOSSIBLE_SUPPRIME)
        }
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: EvenementSousCategorieWithComplementData,
    ): EvenementSousCategorieWithComplementData {
        // Supprimer les paramètres associés au type
        evenementSousCategorieRepository.deleteComplementByEvenementSousCategorieId(element.evenementSousCategorieId)
        // supprimer la catégorie
        evenementSousCategorieRepository.delete(element.evenementSousCategorieId)
        return element
    }

    override fun postEvent(
        element: EvenementSousCategorieWithComplementData,
        userInfo: WrappedUserInfo,
    ) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.evenementSousCategorieId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.EVENEMENT_SOUS_CATEGORIE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
