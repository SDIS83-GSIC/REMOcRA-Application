package remocra.usecase.pei

import jakarta.inject.Inject
import jakarta.inject.Provider
import jakarta.servlet.http.Part
import jakarta.ws.rs.ForbiddenException
import remocra.GlobalConstants
import remocra.app.ParametresProvider
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.NotificationMailData
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.data.enums.TypeSourceModification
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

class InsertDocumentUseCase : AbstractUseCase() {

    @Inject lateinit var documentUtils: DocumentUtils

    @Inject lateinit var documentRepository: DocumentRepository

    @Inject lateinit var organismeRepository: OrganismeRepository

    @Inject lateinit var eventBus: EventBus

    @Inject lateinit var parametresProvider: Provider<ParametresProvider>

    fun execute(userInfo: UserInfo?, element: Part): Result? {
        if (userInfo == null) {
            throw ForbiddenException()
        }
        checkDroits(userInfo)

        var result: Result? = null
        try {
            postEvents(document = enregistrementDocument(element), userInfo = userInfo)
        } catch (e: Exception) {
            result = Result.Error(e.message)
        }
        return result
    }

    private fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.DECLARATION_PEI)) {
            throw RemocraResponseException(ErrorType.DOCUMENT_FORBIDDEN_INSERT)
        }
    }

    private fun enregistrementDocument(element: Part): Document {
        /** Variable générale servant à l'enregistrement du document */
        val documentId = UUID.randomUUID()
        val repertoire = GlobalConstants.DOSSIER_DOCUMENT + "$documentId"

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

    private fun postEvents(document: Document, userInfo: UserInfo) {
        /** Insertion dans la traçabilité */
        eventBus.post(
            TracabiliteEvent(
                pojo = document,
                pojoId = document.documentId,
                typeOperation = TypeOperation.INSERT,
                typeObjet = TypeObjet.DOCUMENT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )

        /** Création de l'objet à notifier */
        eventBus.post(
            NotificationEvent(
                notificationData =
                NotificationMailData(
                    destinataires = setOf(parametresProvider.get().getParametreString(ParametreEnum.DECLARATION_PEI_DESTINATAIRE_EMAIL.toString())!!),
                    objet = parametresProvider.get().getParametreString(ParametreEnum.DECLARATION_PEI_OBJET_EMAIL.toString())!!,
                    corps = parametresProvider.get().getParametreString(ParametreEnum.DECLARATION_PEI_CORPS_EMAIL.toString())!!
                        .replace("#[ORGANISME_UTILISATEUR]#", organismeRepository.getLibelleById(userInfo.organismeId!!)),
                    documentId = document.documentId,
                ),
                idJob = null,
            ),
        )
    }
}
