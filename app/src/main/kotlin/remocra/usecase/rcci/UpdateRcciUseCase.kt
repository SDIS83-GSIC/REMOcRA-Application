package remocra.usecase.rcci

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.RcciFormInput
import remocra.data.enums.ErrorType
import remocra.db.DocumentRepository
import remocra.db.RcciRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.Rcci
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID

class UpdateRcciUseCase : AbstractCUDGeometrieUseCase<RcciFormInput>(TypeOperation.UPDATE) {

    @Inject lateinit var documentUtils: DocumentUtils

    @Inject lateinit var rcciRepository: RcciRepository

    @Inject lateinit var documentRepository: DocumentRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.RCCI_A)) {
            throw RemocraResponseException(ErrorType.RCCI_UPDATE_FORBIDDEN)
        }
    }

    override fun getListGeometrie(element: RcciFormInput): Collection<Geometry> {
        return listOf(element.rcci.rcciGeometrie)
    }

    override fun ensureSrid(element: RcciFormInput): RcciFormInput {
        if (element.rcci.rcciGeometrie.srid != appSettings.srid) {
            return element.copy(
                rcci = element.rcci.copy(
                    rcciGeometrie = transform(element.rcci.rcciGeometrie),
                ),
            )
        }
        return element
    }

    override fun postEvent(element: RcciFormInput, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(documentList = null),
                pojoId = element.rcci.rcciId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.RCCI,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: RcciFormInput): RcciFormInput {
        rcciRepository.updateRcci(
            Rcci(
                rcciId = element.rcci.rcciId,
                rcciCommentaireConclusion = element.rcci.rcciCommentaireConclusion,
                rcciComplement = element.rcci.rcciComplement,
                rcciCarroyageDfci = element.rcci.rcciCarroyageDfci,
                rcciDateIncendie = element.rcci.rcciDateIncendie,
                rcciDateModification = dateUtils.now(),
                rcciDirectionVent = element.rcci.rcciDirectionVent,
                rcciForceVent = element.rcci.rcciForceVent,
                rcciForcesOrdre = element.rcci.rcciForcesOrdre,
                rcciGdh = element.rcci.rcciGdh,
                rcciGelLieux = TODO(),
                rcciGeometrie = element.rcci.rcciGeometrie,
                rcciHygrometrie = element.rcci.rcciHygrometrie,
                rcciRcciIndiceRothermelId = TODO(),
                rcciPointEclosion = element.rcci.rcciPointEclosion,
                rcciPremierCos = element.rcci.rcciPremierCos,
                rcciPremierEngin = element.rcci.rcciPremierEngin,
                rcciSuperficieFinale = element.rcci.rcciSuperficieFinale,
                rcciSuperficieReferent = element.rcci.rcciSuperficieReferent,
                rcciSuperficieSecours = element.rcci.rcciSuperficieSecours,
                rcciTemperature = element.rcci.rcciTemperature,
                rcciVentLocal = TODO(),
                rcciVoieTexte = element.rcci.rcciVoie,
                rcciVoieId = TODO(),
                rcciCommuneId = element.rcci.rcciCommuneId,
                rcciRcciTypePrometheeCategorieId = element.rcci.rcciRcciTypePrometheeCategorieId,
                rcciRcciTypeDegreCertitudeId = element.rcci.rcciRcciTypeDegreCertitudeId,
                rcciRcciTypeOrigineAlerteId = element.rcci.rcciRcciTypeOrigineAlerteId,
                rcciRcciArriveeDdtmOnfId = element.rcci.rcciRcciArriveeDdtmOnfId,
                rcciRcciArriveeSdisId = element.rcci.rcciRcciArriveeSdisId,
                rcciRcciArriveeGendarmerieId = element.rcci.rcciRcciArriveeGendarmerieId,
                rcciRcciArriveePoliceId = element.rcci.rcciRcciArriveePoliceId,
                rcciUtilisateurId = element.rcci.rcciUtilisateurId,
                rcciRisqueMeteo = TODO(),
            ),
        )

        // Suppression des documents absents
        rcciRepository.selectMissingDocument(element.rcci.rcciId, element.rcci.documentList?.map { it.documentId })
            .map { it.rcciDocumentDocumentId }.takeIf { it.isNotEmpty() }?.let {
                    list ->
                documentRepository.getDocumentByIds(list).forEach {
                        document ->
                    documentUtils.deleteDirectory(document.documentRepertoire)
                }
                rcciRepository.deleteMissingDocument(list)
                documentRepository.deleteDocumentByIds(list)
            }

        element.documentList?.forEach { file ->
            val documentId = UUID.randomUUID()
            val repertoire = "${GlobalConstants.DOSSIER_DOCUMENT_RCCI}/${element.rcci.rcciId}/$documentId"
            documentUtils.saveFile(file.inputStream.readAllBytes(), file.submittedFileName, repertoire)

            documentRepository.insertDocument(
                Document(
                    documentId = documentId,
                    documentDate = dateUtils.now(),
                    documentNomFichier = file.submittedFileName,
                    documentRepertoire = repertoire,
                ),
            )

            rcciRepository.insertDocument(UUID.randomUUID(), element.rcci.rcciId, documentId)
        }

        return element.copy(documentList = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: RcciFormInput) {
        // no-op
    }
}
