package remocra.usecase.admin.organisme

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.OrganismeData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.OrganismeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateOrganismeUseCase @Inject constructor(private val organismeRepository: OrganismeRepository) :
    AbstractCUDUseCase<OrganismeData>(
        TypeOperation.UPDATE,
    ) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_ORGANISME_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: OrganismeData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.organismeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ORGANISME,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: OrganismeData): OrganismeData {
        organismeRepository.edit(element)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: OrganismeData) {
    }
}
