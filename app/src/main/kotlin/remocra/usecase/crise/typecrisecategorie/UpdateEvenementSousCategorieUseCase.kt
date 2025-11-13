package remocra.usecase.crise.typecrisecategorie

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.EvenementSousCategorieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.EvenementSousCategorie
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateEvenementSousCategorieUseCase @Inject constructor(
    private val evenementSousCategorieRepository: EvenementSousCategorieRepository,
) : AbstractCUDUseCase<EvenementSousCategorie>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_FORBIDDEN_UPDATE)
        }
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: EvenementSousCategorie,
    ) {
        // Pas de contraintes
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: EvenementSousCategorie,
    ): EvenementSousCategorie {
        evenementSousCategorieRepository.update(element)
        return element
    }

    override fun postEvent(
        element: EvenementSousCategorie,
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
