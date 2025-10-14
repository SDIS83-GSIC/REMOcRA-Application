package remocra.usecase.debitsimultane

import jakarta.inject.Inject
import org.locationtech.jts.algorithm.Centroid
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.PrecisionModel
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.DebitSimultaneData
import remocra.data.enums.ErrorType
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

class CreateDebitSimultaneUseCase : AbstractCUDUseCase<DebitSimultaneData>(TypeOperation.INSERT) {

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

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DEBITS_SIMULTANES_A)) {
            throw RemocraResponseException(ErrorType.DEBIT_SIMULTANE_FORBIDDEN)
        }
    }

    override fun postEvent(element: DebitSimultaneData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(listeDocument = null),
                pojoId = element.debitSimultaneId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DEBIT_SIMULTANE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: DebitSimultaneData): DebitSimultaneData {
        // On insère le débit simultané
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

        debitSimultaneRepository.insertDebitSimultane(
            debitSimultaneId = element.debitSimultaneId,
            elementGeometrie = pointDebitSimultane,
            siteId = peiRepository.getSiteId(listePeiDerniereMesure),
            numeroDossier = element.debitSimultaneNumeroDossier,
        )

        element.listeDebitSimultaneMesure.forEach {
            val debitSimultaneMesureId = it.debitSimultaneMesureId ?: UUID.randomUUID()
            var documentId: UUID? = null
            val repertoire = GlobalConstants.DOSSIER_DEBIT_SIMULTANE + "/$debitSimultaneMesureId"

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
                    debitSimultaneMesureDebitMesure = it.debitSimultaneMesureDebitMesure.takeUnless { _ -> it.debitSimultaneMesureIdentiqueReseauVille },
                    debitSimultaneMesureDebitRetenu = it.debitSimultaneMesureDebitRetenu.takeUnless { _ -> it.debitSimultaneMesureIdentiqueReseauVille },
                    debitSimultaneMesureDateMesure = it.debitSimultaneMesureDateMesure,
                    debitSimultaneMesureCommentaire = it.debitSimultaneMesureCommentaire,
                    debitSimultaneMesureIdentiqueReseauVille = it.debitSimultaneMesureIdentiqueReseauVille,
                    debitSimultaneMesureDocumentId = it.documentId ?: documentId,
                ),
            )

            it.listePeiId.forEach { peiId ->
                debitSimultaneRepository.insertLDebitSimultaneMesurePei(
                    LDebitSimultaneMesurePei(
                        debitSimultaneMesureId = debitSimultaneMesureId,
                        peiId = peiId,
                    ),
                )
            }
        }

        return element.copy(listeDocument = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DebitSimultaneData) {
        // On doit avoir au moins une mesure
        if (element.listeDebitSimultaneMesure.isEmpty()) {
            throw RemocraResponseException(ErrorType.DEBIT_SIMULTANE_MESURE)
        }

        if (element.listeDebitSimultaneMesure.map { it.listePeiId }.any { it.size < 2 }) {
            throw RemocraResponseException(ErrorType.DEBIT_SIMULTANE_MESURE_PEI)
        }
    }
}
