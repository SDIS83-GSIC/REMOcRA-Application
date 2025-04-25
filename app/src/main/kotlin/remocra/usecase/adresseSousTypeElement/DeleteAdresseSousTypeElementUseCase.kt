package remocra.usecase.adresseSousTypeElement

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.AdresseRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.AdresseSousTypeElement
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteAdresseSousTypeElementUseCase @Inject constructor(private val adresseRepository: AdresseRepository) : AbstractCUDUseCase<AdresseSousTypeElement>(TypeOperation.DELETE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NATURE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: AdresseSousTypeElement, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.adresseSousTypeElementId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ADRESSE_SOUS_TYPE_ELEMENT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: AdresseSousTypeElement): AdresseSousTypeElement {
        adresseRepository.deleteById(element.adresseSousTypeElementId)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: AdresseSousTypeElement) {
        // no-op
    }
}
