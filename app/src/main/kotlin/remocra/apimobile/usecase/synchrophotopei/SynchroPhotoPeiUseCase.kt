package remocra.apimobile.usecase.synchrophotopei

import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import remocra.GlobalConstants
import remocra.apimobile.data.PhotoPeiForApiMobileData
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class SynchroPhotoPeiUseCase : AbstractCUDUseCase<PhotoPeiForApiMobileData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    @Inject
    private lateinit var documentUtils: DocumentUtils

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SynchroPhotoPeiUseCase::class.java)
    }

    override fun checkDroits(userInfo: UserInfo) {
        // Pas de droits particulier aujourd'hui pour faire la synchro !
    }

    override fun postEvent(element: PhotoPeiForApiMobileData, userInfo: UserInfo) {
        // On ne poste pas d'évènement comme c'est une insertion dans le schéma incoming
    }

    override fun execute(userInfo: UserInfo?, element: PhotoPeiForApiMobileData): PhotoPeiForApiMobileData {
        val repertoire = GlobalConstants.DOSSIER_DOCUMENT_PEI + "${element.peiId}/${element.photoId}"
        val result = incomingRepository.insertPhotoPei(
            peiId = element.peiId,
            photoId = element.photoId,
            photoDate = dateUtils.getMoment(element.photoDate),
            photoPath = repertoire,
            photoLibelle = element.photoLibelle,
        )

        when (result) {
            0 -> {
                logger.error("La photo ${element.photoId} est déjà dans le schéma incoming")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_PHOTO_EXISTE, element.photoId.toString())
            }
            1 -> {
                // On le sauvegarde sur le disque
                documentUtils.saveFile(element.photoInputStream, element.photoLibelle, repertoire)
            }
            else -> {
                logger.error("Impossible d'insérer la photo ${element.photoId} dans le schéma incoming")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_PHOTO_ERROR, element.photoId.toString())
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: PhotoPeiForApiMobileData) {
        // Check la date
        dateUtils.getMomentForResponse(element.photoDate)
    }
}
