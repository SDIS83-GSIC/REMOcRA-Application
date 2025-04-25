package remocra.usecase.adresseSousTypeElement

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.AdresseRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.AdresseSousTypeElement
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateAdresseSousTypeElementUseCase @Inject constructor(private val adresseRepository: AdresseRepository) : AbstractCUDUseCase<AdresseSousTypeElement>(TypeOperation.INSERT) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NATURE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: AdresseSousTypeElement, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.adresseSousTypeElementId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ADRESSE_SOUS_TYPE_ELEMENT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: AdresseSousTypeElement): AdresseSousTypeElement {
        adresseRepository.insert(element)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: AdresseSousTypeElement) {
        if (adresseRepository.checkCodeExists(element.adresseSousTypeElementCode, null)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_CODE_EXISTS)
        }
    }
}
