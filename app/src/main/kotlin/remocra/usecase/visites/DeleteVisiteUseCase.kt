package remocra.usecase.visites

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.VisiteData
import remocra.data.enums.ErrorType
import remocra.db.AnomalieRepository
import remocra.db.CourrierRepository
import remocra.db.DocumentRepository
import remocra.db.PeiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.LPeiAnomalie
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import remocra.usecase.pei.UpdatePeiUseCase
import java.nio.file.Paths
import java.util.UUID

class DeleteVisiteUseCase @Inject constructor(
    private val updatePeiUseCase: UpdatePeiUseCase,
    private val peiRepository: PeiRepository,
    private val anomalieRepository: AnomalieRepository,
    private val visiteRepository: VisiteRepository,
    private val documentRepository: DocumentRepository,
    private val courrierRepository: CourrierRepository,
    private val documentUtils: DocumentUtils,
) : AbstractCUDUseCase<VisiteData>(TypeOperation.DELETE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        // no op
    }

    override fun postEvent(element: VisiteData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.visiteId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.VISITE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: VisiteData) {
        // Vérification des droits de création
        when (element.visiteTypeVisite) {
            TypeVisite.CTP -> if (!userInfo.hasDroits(
                    droitWeb = Droit.VISITE_CTP_D,
                    droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                )
            ) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_CTP_FORBIDDEN)
            }
            TypeVisite.RECEPTION -> if (!userInfo.hasDroits(
                    droitWeb = Droit.VISITE_RECEP_D,
                    droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                )
            ) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_RECEPTION_FORBIDDEN)
            }
            TypeVisite.RECO_INIT -> if (!userInfo.hasDroits(
                    droitWeb = Droit.VISITE_RECO_INIT_D,
                    droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                )
            ) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_RECO_INIT_FORBIDDEN)
            }
            TypeVisite.ROP -> if (!userInfo.hasDroits(
                    droitWeb = Droit.VISITE_RECO_D,
                    droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                )
            ) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_ROP_FORBIDDEN)
            }
            TypeVisite.NP -> if (!userInfo.hasDroits(
                    droitWeb = Droit.VISITE_NP_D,
                    droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                )
            ) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_D_NP_FORBIDDEN)
            }
        }
        val lastPeiVisiteId = visiteRepository.getLastPeiVisiteId(peiId = element.visitePeiId)
        if (lastPeiVisiteId != element.visiteId) {
            throw RemocraResponseException(ErrorType.VISITE_DELETE_NOT_LAST)
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: VisiteData): VisiteData {
        // On supprime les anomalies du PEI (de la dernière visite)
        peiRepository.deleteAnomaliePei(element.visitePeiId, element.listeAnomalie)

        // On va ensuite chercher les anomalies de la nouvelle dernière visite
        val avantDernierVisiteId = visiteRepository.getAvantDerniereVisite(element.visitePeiId, element.visiteId)
        if (avantDernierVisiteId != null) {
            val listeAnomalie = visiteRepository.getLVisiteAnomalie(avantDernierVisiteId)

            anomalieRepository.batchInsertLPeiAnomalie(
                listeAnomalie.takeIf { it.isNotEmpty() }?.map {
                    LPeiAnomalie(
                        peiId = element.visitePeiId,
                        anomalieId = it,
                    )
                } ?: listOf(),
            )
        }

        // On supprime les autres données liées à la visite
        visiteRepository.deleteAllVisiteAnomalies(element.visiteId)
        visiteRepository.deleteVisiteCtrl(element.visiteId)
        // On supprime des courriers et associations documentaires
        deleteDocumentsByVisiteId(element.visiteId)
        visiteRepository.deleteVisite(element.visiteId)

        // On met à jour le PEI pour le calcul débit pression  / volume et le calcul d'insdispo
        updatePeiUseCase.updatePeiWithId(
            peiId = element.visitePeiId,
            userInfo = userInfo,
        )
        return element
    }

    private fun deleteDocumentsByVisiteId(visiteId: UUID) {
        // Récupère des documents associés à la visite
        val documents = visiteRepository.getDocumentsByVisiteId(visiteId)
        // Récupération des courriers associés aux documents
        val courrierIds = courrierRepository.getCourrierIdsByDocumentIds(documents.map { it.documentId })

        if (courrierIds.isNotEmpty()) {
            // Suppression des associations avec les thématiques
            courrierRepository.deleteLCourrierThematique(courrierIds)
            // Suppression des associations avec les contacts organismes
            courrierRepository.deleteLCourrierContactOrganisme(courrierIds)
            // Suppression des associations avec les organismes
            courrierRepository.deleteLCourrierOrganisme(courrierIds)
            // Suppression des associations avec les gestionnaires
            courrierRepository.deleteLCourrierGestionnaire(courrierIds)
            // Suppression des associations avec les utilisateurs
            courrierRepository.deleteLCourrierUtilisateur(courrierIds)
            // Suppression des courriers
            courrierRepository.deleteCourrierByIds(courrierIds)
        }

        // Suppression des associations avec la visite
        visiteRepository.deleteVisiteDocumentByVisiteId(visiteId)
        documentRepository.deleteDocumentPei(documents.map { it.documentId })

        if (courrierIds.isNotEmpty()) {
            documents.forEach {
                documentUtils.deleteFile(it.documentNomFichier, Paths.get(it.documentRepertoire))
            }
        }
    }
}
