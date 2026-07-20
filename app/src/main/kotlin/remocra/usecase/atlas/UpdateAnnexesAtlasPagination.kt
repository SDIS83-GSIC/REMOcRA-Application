package remocra.usecase.atlas

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.AtlasRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.AtlasAnnexe
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateAnnexesAtlasPagination @Inject constructor(
    private val atlasRepository: AtlasRepository,
) : AbstractCUDUseCase<List<AtlasAnnexe>>(TypeOperation.UPDATE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroits(droitsWeb = listOf(Droit.ATLAS_A, Droit.ATLAS_C))) {
            throw RemocraResponseException(ErrorType.ATLAS_ANNEXES_FORBIDDEN_UPDATE)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: List<AtlasAnnexe>) {
        if (!atlasRepository.hasDocumentsOrAnnexes()) {
            throw RemocraResponseException(ErrorType.ATLAS_DOCUMENTS_NOT_FOUND)
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: List<AtlasAnnexe>): List<AtlasAnnexe> {
        val annexesParId = element.associateBy { it.atlasAnnexeId }
        val updates = atlasRepository.getAtlasAnnexes().map { annexe ->
            val updatedAnnexe = annexesParId[annexe.atlasAnnexeId]
            annexe.copy(
                atlasAnnexeOrder = updatedAnnexe?.atlasAnnexeOrder,
                atlasAnnexeIsVisible = updatedAnnexe?.atlasAnnexeOrder != null,
            )
        }
        atlasRepository.resetAnnexesOrder()
        atlasRepository.updateAnnexesOrder(updates)
        return element
    }

    override fun postEvent(element: List<AtlasAnnexe>, userInfo: WrappedUserInfo) {
        // no-op
    }
}
