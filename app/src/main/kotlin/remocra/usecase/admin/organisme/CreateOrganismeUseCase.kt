package remocra.usecase.admin.organisme

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.OrganismeData
import remocra.data.enums.ErrorType
import remocra.db.OrganismeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateOrganismeUseCase @Inject constructor(private val organismeRepository: OrganismeRepository) : AbstractCUDUseCase<OrganismeData>(TypeOperation.INSERT) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A)) {
            throw RemocraResponseException(ErrorType.ADMIN_ORGANISME_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: OrganismeData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.organismeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ORGANISME,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: OrganismeData): OrganismeData {
        organismeRepository.add(element)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: OrganismeData) {}
}
