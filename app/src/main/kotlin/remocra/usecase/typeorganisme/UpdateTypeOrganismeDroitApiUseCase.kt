package remocra.usecase.typeorganisme

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.TypeOrganismeRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.TypeOrganisme
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateTypeOrganismeDroitApiUseCase : AbstractCUDUseCase<Collection<TypeOrganisme>>(TypeOperation.UPDATE) {

    @Inject
    lateinit var typeOrganismeRepository: TypeOrganismeRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_API)) {
            throw RemocraResponseException(ErrorType.DROIT_API_FORBIDDEN)
        }
    }

    override fun checkContraintes(
        userInfo: UserInfo?,
        element: Collection<TypeOrganisme>,
    ) {
        // no-op
    }

    override fun execute(
        userInfo: UserInfo?,
        element: Collection<TypeOrganisme>,
    ): Collection<TypeOrganisme> {
        element.forEach {
            typeOrganismeRepository.updateTypeOrganismeDroitApi(it.typeOrganismeId, it.typeOrganismeDroitApi)
        }

        return element
    }

    override fun postEvent(
        element: Collection<TypeOrganisme>,
        userInfo: UserInfo,
    ) {
        // On ne trace pas comme pour les liens profil droit
    }
}
