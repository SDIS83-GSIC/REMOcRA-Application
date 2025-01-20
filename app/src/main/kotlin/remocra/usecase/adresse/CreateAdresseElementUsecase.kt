package remocra.usecase.adresse

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AdresseElementInput
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.AdresseElementRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.AdresseElement
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateAdresseElementUsecase
@Inject constructor(
    private val adresseElementRepository: AdresseElementRepository,
) :
    AbstractCUDUseCase<AdresseElementInput>(TypeOperation.INSERT) {

    override fun execute(userInfo: UserInfo?, element: AdresseElementInput): AdresseElementInput {
        adresseElementRepository.insertAdresseElement(
            AdresseElement(
                adresseElementId = element.adresseElementId,
                adresseElementAdresseId = element.adresseElementAdresseId!!, // Vérification du non-null dans le checkContraintes
                adresseElementGeometrie = element.geometry,
                adresseElementDescription = element.description,
                adresseElementSousType = element.sousType,
            ),
        )
        adresseElementRepository.insertLiaisonAnomalie(element.adresseElementId, element.anomalies)

        return element
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADRESSES_C)) {
            throw RemocraResponseException(ErrorType.ADRESSE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: AdresseElementInput, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.adresseElementId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ADRESSE_ELEMENT,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        ) }

    override fun checkContraintes(userInfo: UserInfo?, element: AdresseElementInput) {
        if (element.adresseElementAdresseId == null) {
            throw RemocraResponseException(ErrorType.ADRESSE_ELEMENT_ADRESSE_NULL)
        }
    }
}
