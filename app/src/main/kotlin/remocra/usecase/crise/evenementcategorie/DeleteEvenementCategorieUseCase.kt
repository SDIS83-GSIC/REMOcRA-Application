package remocra.usecase.crise.evenementcategorie

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.EvenementCategorieWithTypeCriseData
import remocra.data.enums.ErrorType
import remocra.db.EvenementCategorieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteEvenementCategorieUseCase
@Inject
constructor(
    private val evenementCategorieRepository: EvenementCategorieRepository,
) :
    AbstractCUDUseCase<EvenementCategorieWithTypeCriseData>(TypeOperation.DELETE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_FORBIDDEN_REMOVAL)
        }
    }

    override fun postEvent(
        element: EvenementCategorieWithTypeCriseData,
        userInfo: WrappedUserInfo,
    ) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.evenementCategorieId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.EVENEMENT_CATEGORIE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: EvenementCategorieWithTypeCriseData): EvenementCategorieWithTypeCriseData {
        evenementCategorieRepository.deleteTypeCriseAssociations(element.evenementCategorieId)
        evenementCategorieRepository.delete(element.evenementCategorieId)

        return element
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: EvenementCategorieWithTypeCriseData,
    ) {
    }
}
