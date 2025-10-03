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

class UpdateCoucheMetadataUseCase : AbstractCUDUseCase<CoucheMetadata>(TypeOperation.UPDATE) {

    @Inject lateinit var coucheMetadataRepository: CoucheMetadataRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.CARTO_METADATA_A)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES)
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
            // supprimer tout id dans L_PROFIL_DROIT_COUCHE
            coucheMetadataRepository.deleteLienGroupeFonctionnalites(element.coucheMetadataId)
            coucheMetadataRepository.upsertCoucheMetadata(element)

            if (element.groupeFonctionnaliteIds != null) {
                for (id in element.groupeFonctionnaliteIds) {
                    coucheMetadataRepository.addLienGroupeFonctionnalites(element.coucheMetadataId, id)
                }
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CoucheMetadata) {}
}
