package remocra.usecase.admin.couches

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheFormDataWithImage
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.db.CoucheRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.SourceCarto
import remocra.db.jooq.remocra.tables.pojos.Couche
import remocra.eventbus.datacache.DataCacheModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateCoucheUseCase : AbstractCUDUseCase<CoucheFormDataWithImage>(TypeOperation.INSERT) {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var objectMapper: ObjectMapper

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_COUCHE_CARTOGRAPHIQUE)) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES)
        }
    }

    override fun postEvent(element: CoucheFormDataWithImage, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.coucheFormData,
                pojoId = element.coucheFormData.coucheId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.COUCHE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
        eventBus.post(DataCacheModifiedEvent(TypeDataCache.COUCHE))
    }

    override fun execute(userInfo: WrappedUserInfo, element: CoucheFormDataWithImage): CoucheFormDataWithImage {
        coucheRepository.upsertCouche(
            Couche(
                coucheId = element.coucheFormData.coucheId,
                coucheGroupeCoucheId = element.coucheFormData.groupeCoucheId,
                coucheCode = element.coucheFormData.coucheCode,
                coucheLibelle = element.coucheFormData.coucheLibelle,
                coucheOrdre = coucheRepository.getLastOrdre(element.coucheFormData.groupeCoucheId)?.plus(1) ?: 1,
                coucheSource = element.coucheFormData.coucheSource,
                coucheProjection = element.coucheFormData.coucheProjection,
                coucheUrl = element.coucheFormData.coucheUrl,
                coucheNom = element.coucheFormData.coucheNom,
                coucheFormat = element.coucheFormData.coucheFormat,
                couchePublic = element.coucheFormData.couchePublic,
                coucheActive = element.coucheFormData.coucheActive,
                coucheIcone = null,
                coucheLegende = null,
                coucheProxy = element.coucheFormData.coucheProxy,
                coucheCrossOrigin = element.coucheFormData.coucheCrossOrigin,
                coucheProtected = element.coucheFormData.coucheProtected,
                coucheTuilage = element.coucheFormData.coucheTuilage.takeIf { element.coucheFormData.coucheSource == SourceCarto.WMS } ?: false,
            ),
        )

        // On stocke le byteArray de l'icone et de la légende en base, donc on ne passe pas par les facilitateurs de fichiers
        if (element.icone != null) {
            coucheRepository.updateIcone(
                element.coucheFormData.coucheId,
                element.icone.inputStream.use { it.readBytes() },
            )
        }
        if (element.legende != null) {
            coucheRepository.updateLegende(
                element.coucheFormData.coucheId,
                element.legende.inputStream.use { it.readBytes() },
            )
        }

        element.coucheFormData.groupeFonctionnalitesHorsZcList.forEach { groupeFonctionnalitesId ->
            coucheRepository.insertGroupeFonctionnalites(element.coucheFormData.coucheId, groupeFonctionnalitesId, false)
        }

        element.coucheFormData.groupeFonctionnalitesZcList.forEach { groupeFonctionnalitesId ->
            coucheRepository.insertGroupeFonctionnalites(element.coucheFormData.coucheId, groupeFonctionnalitesId, true)
        }

        element.coucheFormData.moduleList.forEach { moduleType ->
            coucheRepository.insertModule(element.coucheFormData.coucheId, moduleType)
        }

        return element.copy(icone = null, legende = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CoucheFormDataWithImage) {
        if (coucheRepository.checkCodeExists(element.coucheFormData.coucheCode, element.coucheFormData.coucheId)) {
            throw RemocraResponseException(
                ErrorType.ADMIN_COUCHES_CODE_UNIQUE,
            )
        }
    }
}
