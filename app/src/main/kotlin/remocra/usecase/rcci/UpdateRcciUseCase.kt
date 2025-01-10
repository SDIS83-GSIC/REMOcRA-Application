package remocra.usecase.rcci

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.RcciFormInput
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DocumentRepository
import remocra.db.RcciRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.Rcci
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID

class UpdateRcciUseCase : AbstractCUDUseCase<RcciFormInput>(TypeOperation.UPDATE) {

    @Inject lateinit var documentUtils: DocumentUtils

    @Inject lateinit var rcciRepository: RcciRepository

    @Inject lateinit var documentRepository: DocumentRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.RCCI_A)) {
            throw RemocraResponseException(ErrorType.RCCI_UPDATE_FORBIDDEN)
        }
    }

    override fun postEvent(element: RcciFormInput, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(documentList = null),
                pojoId = element.rcci.rcciId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.RCCI,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: RcciFormInput): RcciFormInput {
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
                rcciGelLieux = element.rcci.rcciGelLieux,
                rcciGeometrie = element.rcci.rcciGeometrie,
                rcciHygrometrie = element.rcci.rcciHygrometrie,
                rcciIndiceRothermel = element.rcci.rcciIndiceRothermel,
                rcciPointEclosion = element.rcci.rcciPointEclosion,
                rcciPremierCos = element.rcci.rcciPremierCos,
                rcciPremierEngin = element.rcci.rcciPremierEngin,
                rcciSuperficieFinale = element.rcci.rcciSuperficieFinale,
                rcciSuperficieReferent = element.rcci.rcciSuperficieReferent,
                rcciSuperficieSecours = element.rcci.rcciSuperficieSecours,
                rcciTemperature = element.rcci.rcciTemperature,
                rcciVentLocal = element.rcci.rcciVentLocal,
                rcciVoie = element.rcci.rcciVoie,
                rcciCommuneId = element.rcci.rcciCommuneId,
                rcciRcciTypePrometheeCategorieId = element.rcci.rcciRcciTypePrometheeCategorieId,
                rcciRcciTypeDegreCertitudeId = element.rcci.rcciRcciTypeDegreCertitudeId,
                rcciRcciTypeOrigineAlerteId = element.rcci.rcciRcciTypeOrigineAlerteId,
                rcciRcciArriveeDdtmOnfId = element.rcci.rcciRcciArriveeDdtmOnfId,
                rcciRcciArriveeSdisId = element.rcci.rcciRcciArriveeSdisId,
                rcciRcciArriveeGendarmerieId = element.rcci.rcciRcciArriveeGendarmerieId,
                rcciRcciArriveePoliceId = element.rcci.rcciRcciArriveePoliceId,
                rcciUtilisateurId = element.rcci.rcciUtilisateurId,
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

    override fun checkContraintes(userInfo: UserInfo?, element: RcciFormInput) {
        // no-op
    }
}
