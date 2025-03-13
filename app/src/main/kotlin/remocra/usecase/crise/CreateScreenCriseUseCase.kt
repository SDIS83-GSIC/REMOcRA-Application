package remocra.usecase.crise

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.CreateDoc
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.CriseRepository
import remocra.db.DocumentRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID

class CreateScreenCriseUseCase : AbstractCUDUseCase<CreateDoc>(TypeOperation.INSERT) {

    @Inject private lateinit var documentUtils: DocumentUtils

    @Inject private lateinit var documentRepository: DocumentRepository

    @Inject private lateinit var criseRepository: CriseRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.CRISE_C)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_C)
        }
    }

    override fun postEvent(element: CreateDoc, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.criseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.CRISE_DOCUMENT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: CreateDoc): CreateDoc {
        /** Variable générale servant à l'enregistrement du document */
        val documentId = UUID.randomUUID()
        val repertoire = GlobalConstants.DOSSIER_DOCUMENT_CRISE + "$documentId"
        val docName = "${element.criseDocName}.png"

        /** Enregistrement sur le disque */
        documentUtils.saveFile(element.criseDocument!!.inputStream.readAllBytes(), docName, repertoire)

        /** Enregistrement en base de données */
        val documentToInsert =
            Document(
                documentId = documentId,
                documentDate = dateUtils.now(),
                documentNomFichier = docName,
                documentRepertoire = repertoire,
            )
        documentRepository.insertDocument(documentToInsert)
        criseRepository.insertCriseDocument(documentId, element.criseId, element.criseDocumentGeometrie)

        return element.copy(criseDocument = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: CreateDoc) {
        // pas de contraintes
    }
}
