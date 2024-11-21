package remocra.usecase.debitsimultane

import com.google.inject.Inject
import org.locationtech.jts.algorithm.Centroid
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.PrecisionModel
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.DebitSimultaneData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DebitSimultaneRepository
import remocra.db.DocumentRepository
import remocra.db.PeiRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.DebitSimultaneMesure
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.LDebitSimultaneMesurePei
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID

class UpdateDebitSimultaneUseCase : AbstractCUDUseCase<DebitSimultaneData>(TypeOperation.UPDATE) {

    @Inject
    private lateinit var debitSimultaneRepository: DebitSimultaneRepository

    @Inject
    private lateinit var documentRepository: DocumentRepository

    @Inject
    private lateinit var peiRepository: PeiRepository

    @Inject
    private lateinit var appSettings: AppSettings

    @Inject
    private lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.DEBITS_SIMULTANES_A)) {
            throw RemocraResponseException(ErrorType.DEBIT_SIMULTANE_FORBIDDEN)
        }
    }

    override fun postEvent(element: DebitSimultaneData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(listeDocument = null),
                pojoId = element.debitSimultaneId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DEBIT_SIMULTANE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: DebitSimultaneData): DebitSimultaneData {
        val mapDocumentByDebitMesure = debitSimultaneRepository.getDocumentByDebitSimultaneMesureId(element.debitSimultaneId)

        // Ce qu'on a déjà en base
        val listeDebitSimultaneMesure = debitSimultaneRepository.getListDebitSimultaneMesure(element.debitSimultaneId)

        listeDebitSimultaneMesure
            .filter { !element.listeDebitSimultaneMesure.map { it.debitSimultaneMesureId }.contains(it.debitSimultaneMesureId) }.forEach {
                // On supprime ce qu'il faut
                debitSimultaneRepository.deleteLDebitSimultaneMesurePei(it.debitSimultaneMesureId)

                if (it.debitSimultaneMesureDocumentId != null) {
                    val doc = mapDocumentByDebitMesure[it.debitSimultaneMesureId]!!
                    documentUtils.deleteFile(doc.documentNomFichier, doc.documentRepertoire)
                }

                debitSimultaneRepository.deleteDebitSimultaneMesure(it.debitSimultaneMesureId)
            }

        // On supprime ensuite en base
        documentRepository.deleteDocumentByIds(listeDebitSimultaneMesure.map { it.debitSimultaneMesureDocumentId }.filterNotNull())

        element.listeDebitSimultaneMesure.forEach {
            val debitSimultaneMesureId = it.debitSimultaneMesureId ?: UUID.randomUUID()
            var documentId: UUID? = null
            val repertoire = GlobalConstants.DOSSIER_DEBIT_SIMULTANE + "/$debitSimultaneMesureId"

            // Si c'est une mise à jour
            if (it.debitSimultaneMesureId != null) {
                // Si on avait déjà un document en base mais qu'on a changé de fichier (donc que le documentId = null)
                val document = mapDocumentByDebitMesure[it.debitSimultaneMesureId]
                if (document != null && it.documentId == null && !element.listeDocument.isNullOrEmpty()) {
                    // On supprime document actuel en base et sur le disque
                    documentUtils.deleteFile(document.documentNomFichier, document.documentRepertoire)
                    val newDoc = element.listeDocument.find { t -> t.name == "document_${it.documentNomFichier}" }
                    documentUtils.saveFile(
                        newDoc!!.inputStream.readAllBytes(),
                        newDoc.submittedFileName,
                        repertoire,
                    )
                    // On met à jour le nom du fichier
                    documentRepository.updateDocument(newDoc.submittedFileName, repertoire, document.documentId)
                }
            }

            // Sinon si c'est un ajout de document
            if (it.documentNomFichier != null && it.documentId == null && !element.listeDocument.isNullOrEmpty()) {
                documentId = UUID.randomUUID()
                val newDoc = element.listeDocument.find { t -> t.name == "document_${it.documentNomFichier}" }
                documentUtils.saveFile(
                    newDoc!!.inputStream.readAllBytes(),
                    newDoc.submittedFileName,
                    repertoire,
                )

                // création en base
                documentRepository.insertDocument(
                    Document(
                        documentId = documentId,
                        documentDate = dateUtils.now(),
                        documentNomFichier = it.documentNomFichier,
                        documentRepertoire = repertoire,
                    ),
                )
            }

            debitSimultaneRepository.upsertDebitSimultaneMesure(
                DebitSimultaneMesure(
                    debitSimultaneMesureId = debitSimultaneMesureId,
                    debitSimultaneId = element.debitSimultaneId,
                    debitSimultaneMesureDebitRequis = it.debitSimultaneMesureDebitRequis,
                    debitSimultaneMesureDebitMesure = it.debitSimultaneMesureDebitMesure.takeUnless { _ -> it.debitSimultaneMesureIdentiqueReseauVille == true },
                    debitSimultaneMesureDebitRetenu = it.debitSimultaneMesureDebitRetenu.takeUnless { _ -> it.debitSimultaneMesureIdentiqueReseauVille == true },
                    debitSimultaneMesureDateMesure = it.debitSimultaneMesureDateMesure,
                    debitSimultaneMesureCommentaire = it.debitSimultaneMesureCommentaire,
                    debitSimultaneMesureIdentiqueReseauVille = it.debitSimultaneMesureIdentiqueReseauVille,
                    debitSimultaneMesureDocumentId = it.documentId ?: documentId,
                ),
            )
            debitSimultaneRepository.deleteLDebitSimultaneMesurePei(debitSimultaneMesureId)
            it.listePeiId.forEach { peiId ->
                debitSimultaneRepository.insertLDebitSimultaneMesurePei(
                    LDebitSimultaneMesurePei(
                        debitSimultaneMesureId = debitSimultaneMesureId,
                        peiId = peiId,
                    ),
                )
            }
        }

        // Mettre à jour le débit simultané
        // Ce sont les PEI de la dernière mesure qui comptent
        val listePeiDerniereMesure = element.listeDebitSimultaneMesure.sortedBy { it.debitSimultaneMesureDateMesure }.last().listePeiId
        val listePoint = peiRepository.getGeometriesPei(listePeiDerniereMesure)

        val centroid = Centroid(
            MultiPoint(
                listePoint.toTypedArray(),
                GeometryFactory(
                    PrecisionModel(),
                    appSettings.srid,
                ),
            ),
        ).centroid

        val pointDebitSimultane = GeometryFactory(PrecisionModel(), appSettings.srid).createPoint(
            Coordinate(
                centroid.x,
                centroid.y,
            ),
        )

        debitSimultaneRepository.updateDebitSimultane(
            debitSimultaneId = element.debitSimultaneId,
            pointGeometrie = pointDebitSimultane,
            siteId = peiRepository.getSiteId(listePeiDerniereMesure),
            numeroDossier = element.debitSimultaneNumeroDossier,
        )

        return element.copy(listeDocument = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: DebitSimultaneData) {
        // On doit avoir au moins une mesure
        if (element.listeDebitSimultaneMesure.isEmpty()) {
            throw RemocraResponseException(ErrorType.DEBIT_SIMULTANE_MESURE)
        }

        if (element.listeDebitSimultaneMesure.map { it.listePeiId }.any { it.size < 2 }) {
            throw RemocraResponseException(ErrorType.DEBIT_SIMULTANE_MESURE_PEI)
        }
    }
}
