package remocra.usecase.document.blocdocument

import com.google.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.BlocDocumentData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.BlocDocumentRepository
import remocra.db.DocumentRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.BlocDocument
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.LProfilDroitBlocDocument
import remocra.db.jooq.remocra.tables.pojos.LThematiqueBlocDocument
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID

class CreateBlocDocumentUseCase : AbstractCUDUseCase<BlocDocumentData>(TypeOperation.INSERT) {

    @Inject lateinit var documentRepository: DocumentRepository

    @Inject lateinit var blocDocumentRepository: BlocDocumentRepository

    @Inject lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.DOCUMENTS_A)) {
            throw RemocraResponseException(ErrorType.BLOC_DOCUMENT_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: BlocDocumentData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(
                    document = null,
                ),
                pojoId = element.blocDocumentId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.BLOC_DOCUMENT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: BlocDocumentData): BlocDocumentData {
        val documentId = UUID.randomUUID()

        // On sauvegarde le document sur le disque
        val repertoire = GlobalConstants.DOSSIER_BLOC_DOCUMENT + "/${element.blocDocumentId}"
        documentUtils.saveFile(element.document!!.inputStream.readAllBytes(), element.document.submittedFileName, repertoire)

        // On récupère le document et on l'enregistre
        documentRepository.insertDocument(
            Document(
                documentId = documentId,
                documentDate = dateUtils.now(),
                documentNomFichier = element.document.submittedFileName,
                documentRepertoire = repertoire,
            ),
        )

        blocDocumentRepository.insertBlocDocument(
            BlocDocument(
                blocDocumentId = element.blocDocumentId,
                documentId = documentId,
                blocDocumentLibelle = element.blocDocumentLibelle,
                blocDocumentDescription = element.blocDocumentDecription,
                blocDocumentDateMaj = null,
            ),
        )

        element.listeThematiqueId?.forEach {
            blocDocumentRepository.insertThematiqueBlocDocument(
                LThematiqueBlocDocument(
                    blocDocumentId = element.blocDocumentId,
                    thematiqueId = it,
                ),
            )
        }

        element.listeProfilDroitId?.forEach {
            blocDocumentRepository.insertProfilDroitBlocDocument(
                LProfilDroitBlocDocument(
                    blocDocumentId = element.blocDocumentId,
                    profilDroitId = it,
                ),
            )
        }

        return element.copy(document = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: BlocDocumentData) {
    }
}
