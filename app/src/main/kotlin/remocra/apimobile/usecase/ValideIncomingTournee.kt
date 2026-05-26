package remocra.apimobile.usecase

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.apimobile.repository.IncomingRepository
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.CreationVisiteCtrl
import remocra.data.VisiteData
import remocra.db.DocumentRepository
import remocra.db.TourneeRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.log.LogManager
import remocra.usecase.AbstractUseCase
import remocra.usecase.pei.MovePeiUseCase
import remocra.usecase.pei.UpdatePeiUseCase
import remocra.usecase.visites.CreateVisiteUseCase
import java.util.UUID

class ValideIncomingTournee @Inject constructor(
    private val incomingRepository: IncomingRepository,
    private val documentRepository: DocumentRepository,
    private val tourneeRepository: TourneeRepository,
    private val createVisiteUseCase: CreateVisiteUseCase,
    private val movePeiUseCase: MovePeiUseCase,
    private val updatePeiUseCase: UpdatePeiUseCase,
    private val getCommuneVoieByGeomUseCase: GetCommuneVoieByGeomUseCase,
    private val parametresProvider: ParametresProvider,
) : AbstractUseCase() {

    fun execute(tourneeId: UUID, userInfo: WrappedUserInfo, logManager: LogManager, mainTransaction: TransactionManager) {
        mainTransaction.transactionResult {
            logManager.info("Déplacement des PEI")
            gestionPeiDeplacement(userInfo, tourneeId, logManager, mainTransaction)

            logManager.info("Gestion des photos")
            gestionPhoto(tourneeId, logManager)

            logManager.info("Gestion des visites")
            gestionVisites(tourneeId, userInfo, logManager, mainTransaction)

            logManager.info("Mise à jour de la tournée $tourneeId")
            tourneeRepository.setAvancementTournee(tourneeId, 100)
            tourneeRepository.desaffectationTournee(tourneeId)

            tourneeRepository.updateDateSynchronisation(dateUtils.now(), tourneeId)

            // On supprime les tournées qui n'ont plus de visites associées
            val listeIdTournee = incomingRepository.getTourneeSansVisite()
            logManager.info(
                "Suppression des tournées dont toutes les visites ont été intégrées : ${
                    listeIdTournee.joinToString(
                        ", ",
                    )
                }",
            )
            incomingRepository.deleteTournee(listeIdTournee)
        }
    }

    private fun gestionPeiDeplacement(userInfo: WrappedUserInfo, tourneeId: UUID, logManager: LogManager, mainTransactionManager: TransactionManager) {
        val listePeiDeplacement = incomingRepository.getPeiDeplacement(tourneeId)
        logManager.info("Liste des PEI à déplacer : $listePeiDeplacement")

        val toleranceVoie = parametresProvider.getParametreInt(GlobalConstants.TOLERANCE_VOIES_METRES)

        // Si on n'a pas défini la tolérance, on ne déplace pas les PEI et on met un log de error sans pour autant faire échouer la synchro
        if (toleranceVoie == null) {
            logManager.warn("Le paramètre ${GlobalConstants.TOLERANCE_VOIES_METRES} n'est pas défini, les PEI ne seront pas déplacés")
            incomingRepository.deletePeiDeplacement(tourneeId)
            return
        }
        listePeiDeplacement.forEach {
            logManager.info("Déplacement d'un PEI ${it.peiDeplacementPeiId} (peiId : ${it.peiDeplacementPeiId})")
            val communeVoie = getCommuneVoieByGeomUseCase.execute(
                geometriePei = it.peiDeplacementGeometrie,
                toleranceVoie = toleranceVoie,
            )

            val peiData = movePeiUseCase.execute(
                it.peiDeplacementGeometrie,
                it.peiDeplacementPeiId,
                voieId = communeVoie.voieId,
            )

            val result = updatePeiUseCase.execute(userInfo, peiData, mainTransactionManager)

            if (result !is Result.Success) {
                if (result is Result.Error) {
                    // On ne fait pas planter la synchronisation mais on ne déplace pas le PEI
                    logManager.error("Erreur lors du déplacement du PEI ${it.peiDeplacementPeiId} : ${result.message}")
                }
                logManager.error("Erreur lors du déplacement du PEI ${it.peiDeplacementPeiId}")
            }
        }

        // Suppression des peiDeplacement
        logManager.info("Suppression des PEI déplacés dans incoming")
        incomingRepository.deletePeiDeplacement(tourneeId)
    }

    private fun gestionVisites(tourneeId: UUID, userInfo: WrappedUserInfo, logManager: LogManager, mainTransactionManager: TransactionManager) {
        val visites = incomingRepository.getVisites(tourneeId)
        val visitesCtrlDebitPression = incomingRepository.getVisitesCtrlDebitPression(tourneeId)
        val visiteAnomalie = incomingRepository.getVisitesAnomalie(tourneeId)

        val visiteIdInseres = mutableListOf<UUID>()

        visites.forEach {
            val ctrl = visitesCtrlDebitPression
                .firstOrNull { v -> v.visiteCtrlDebitPressionVisiteId == it.visiteId }

            val result = createVisiteUseCase.execute(
                userInfo,
                VisiteData(
                    visiteId = it.visiteId,
                    visitePeiId = it.visitePeiId,
                    visiteDate = it.visiteDate,
                    visiteTypeVisite = it.visiteTypeVisite,
                    visiteAgent1 = it.visiteAgent1,
                    visiteAgent2 = it.visiteAgent2,
                    visiteObservation = it.visiteObservation,
                    listeAnomalie = visiteAnomalie.filter { va -> va.visiteId == it.visiteId }.map { it.anomalieId },
                    isCtrlDebitPression = visitesCtrlDebitPression.map { it.visiteCtrlDebitPressionVisiteId }
                        .contains(it.visiteId),
                    ctrlDebitPression = CreationVisiteCtrl(
                        ctrl?.visiteCtrlDebitPressionDebit,
                        ctrl?.visiteCtrlDebitPressionPression,
                        ctrl?.visiteCtrlDebitPressionPressionDyn,
                    ),
                ),
                mainTransactionManager,
            )

            if (result !is Result.Success && result !is Result.Created) {
                if (result is Result.Error) {
                    logManager.error("Erreur lors de l'insertion de la visite ${it.visiteId} : ${result.message}")
                }
                logManager.error("Erreur lors de l'insertion de la visite ${it.visiteId}")
            } else {
                visiteIdInseres.add(it.visiteId)
            }
        }

        // Suppression des visites
        logManager.info("Suppression des visites")
        incomingRepository.deleteVisiteAnomalie(visiteIdInseres)
        incomingRepository.deleteVisiteCtrlDebitPression(visiteIdInseres)
        incomingRepository.deleteVisite(visiteIdInseres)
    }

    private fun gestionPhoto(tourneeId: UUID, logManager: LogManager) {
        val listePhotoPei = incomingRepository.getPhotoPei(tourneeId)

        listePhotoPei.forEach {
            logManager.info("CREATION document ${it.photoId} pour le PEI ${it.peiId}")
            documentRepository.insertDocument(
                Document(
                    documentId = it.photoId,
                    documentDate = it.photoDate,
                    documentNomFichier = it.photoLibelle,
                    documentRepertoire = it.photoPath,
                ),
            )

            // On insère ensuite le lien avec isPhotoPei
            documentRepository.insertDocumentPei(
                peiId = it.peiId,
                documentId = it.photoId,
                isPhotoPei = true,
            )
        }

        logManager.info("Suppression des photos")
        incomingRepository.deletePhotoPei(listePhotoPei.map { it.photoId })
    }
}
