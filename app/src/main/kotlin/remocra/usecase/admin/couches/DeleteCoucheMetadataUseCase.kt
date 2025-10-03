package remocra.usecase.admin.couches

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheMetadata
import remocra.data.enums.ErrorType
import remocra.db.CoucheMetadataRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteCoucheMetadataUseCase : AbstractCUDUseCase<CoucheMetadata>(TypeOperation.DELETE) {

    @Inject lateinit var coucheMetadataRepository: CoucheMetadataRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.CARTO_METADATA_A)) {
            throw RemocraResponseException(ErrorType.GROUPE_FONCTIONNALITES_COUCHE_DELETE)
        }
    }

    override fun postEvent(element: CoucheMetadata, userInfo: WrappedUserInfo) {
        if (element.coucheMetadataId != null) {
            eventBus.post(
                TracabiliteEvent(
                    pojo = element,
                    pojoId = element.coucheMetadataId,
                    typeOperation = typeOperation,
                    typeObjet = TypeObjet.COUCHE_METADATA,
                    auteurTracabilite = userInfo.getInfosTracabilite(),
                    date = dateUtils.now(),
                ),
            )
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: CoucheMetadata): CoucheMetadata {
        if (element.coucheMetadataId != null) {
            coucheMetadataRepository.deleteLienGroupeFonctionnalites(element.coucheMetadataId)

            coucheMetadataRepository.deleteCouchesMetadataByMetadataId(element.coucheMetadataId)
        }

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CoucheMetadata) {}
}
