package remocra.usecase.crise.typecrisecategorie

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.TypeCriseCatagorieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.TypeCriseCategorie
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateTypeCriseCategorieUseCase @Inject constructor(
    private val typeCriseCatagorieRepository: TypeCriseCatagorieRepository,
) : AbstractCUDUseCase<TypeCriseCategorie>(TypeOperation.INSERT) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_FORBIDDEN_INSERT)
        }
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: TypeCriseCategorie,
    ) {
        // Pas de contraintes
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: TypeCriseCategorie,
    ): TypeCriseCategorie {
        typeCriseCatagorieRepository.insert(element)
        return element
    }

    override fun postEvent(
        element: TypeCriseCategorie,
        userInfo: WrappedUserInfo,
    ) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.typeCriseCategorieId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.TYPE_CRISE_CATEGORIE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
