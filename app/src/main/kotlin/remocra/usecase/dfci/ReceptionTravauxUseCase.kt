package remocra.usecase.dfci

import jakarta.inject.Inject
import jakarta.inject.Provider
import jakarta.servlet.http.Part
import remocra.GlobalConstants
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.NotificationMailData
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.db.DocumentRepository
import remocra.db.OrganismeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.eventbus.EventBus
import remocra.eventbus.notification.NotificationEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID

class ReceptionTravauxUseCase @Inject constructor(
    private val documentUtils: DocumentUtils,
    private val documentRepository: DocumentRepository,
    private val eventBus: EventBus,
    private val parametresProvider: Provider<ParametresProvider>,
    private val organismeRepository: OrganismeRepository,
) : AbstractUseCase() {

    private fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DFCI_RECEPTRAVAUX_C)) {
            throw RemocraResponseException(ErrorType.DFCI_FORBIDDEN_RECEPTION_TRAVAUX)
        }
    }

    private fun enregistrementDocument(element: Part): Document {
        /** Variable générale servant à l'enregistrement du document */
        val documentId = UUID.randomUUID()
        val repertoire = GlobalConstants.DOSSIER_DOCUMENT_DFCI_TRAVAUX + "$documentId"

        /** Enregistrement sur le disque */
        documentUtils.saveFile(element.inputStream.readAllBytes(), element.submittedFileName, repertoire)

        /** Enregistrement en base de données */
        val documentToInsert =
            Document(
                documentId = documentId,
                documentDate = dateUtils.now(),
                documentNomFichier = element.submittedFileName,
                documentRepertoire = repertoire,
            )
        documentRepository.insertDocument(documentToInsert)
        return documentToInsert
    }

    private fun postEvents(document: Document, userInfo: WrappedUserInfo) {
        /** Insertion dans la traçabilité */
        eventBus.post(
            TracabiliteEvent(
                pojo = document,
                pojoId = document.documentId,
                typeOperation = TypeOperation.INSERT,
                typeObjet = TypeObjet.DOCUMENT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )

        /** Création de l'objet à notifier */
        eventBus.post(
            NotificationEvent(
                notificationData =
                NotificationMailData(
                    destinataires = setOf(parametresProvider.get().getParametreString(ParametreEnum.DFCI_TRAVAUX_DESTINATAIRE_EMAIL.name)!!),
                    objet = parametresProvider.get().getParametreString(ParametreEnum.DFCI_TRAVAUX_OBJET_EMAIL.name)!!,
                    corps = parametresProvider.get().getParametreString(ParametreEnum.DFCI_TRAVAUX_CORPS_EMAIL.name)!!
                        .replace("#[ORGANISME_UTILISATEUR]#", organismeRepository.getLibelleById(userInfo.organismeId!!)),
                    documentId = document.documentId,
                ),
                idJob = null,
            ),
        )
    }

    fun execute(userInfo: WrappedUserInfo, element: Part): Result? {
        checkDroits(userInfo)

        var result: Result? = null
        try {
            postEvents(document = enregistrementDocument(element), userInfo = userInfo)
        } catch (e: Exception) {
            result = Result.Error(e.message)
        }
        return result
    }
}
