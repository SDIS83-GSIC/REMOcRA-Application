package remocra.usecase.typeorganisme

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
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

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_API)) {
            throw RemocraResponseException(ErrorType.DROIT_API_FORBIDDEN)
        }
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: Collection<TypeOrganisme>,
    ) {
        // no-op
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: Collection<TypeOrganisme>,
    ): Collection<TypeOrganisme> {
        element.forEach {
            typeOrganismeRepository.updateTypeOrganismeDroitApi(it.typeOrganismeId, it.typeOrganismeDroitApi)
        }

        return element
    }

    override fun postEvent(
        element: Collection<TypeOrganisme>,
        userInfo: WrappedUserInfo,
    ) {
        // On ne trace pas comme pour les liens profil droit
    }
}
