package remocra.apimobile.usecase

import jakarta.inject.Inject
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.WrappedUserInfo
import remocra.data.PeiData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.log.LogManager
import remocra.usecase.AbstractUseCase
import remocra.usecase.pei.CreatePeiUseCase
import java.util.UUID

class ValideIncomingNewPei @Inject constructor(
    private val incomingRepository: IncomingRepository,
    private val createPeiUseCase: CreatePeiUseCase,

) : AbstractUseCase() {

    fun execute(userInfo: WrappedUserInfo, logManager: LogManager, peiId: UUID, mainTransaction: TransactionManager) {
        mainTransaction.transactionResult {
            logManager.info("Récupération du nouveau PEI")
            val newPei = incomingRepository.getNewPei(peiId)

            if (newPei == null) {
                logManager.error("Impossible de trouver le PEI $peiId dans incoming")
                return@transactionResult
            }

            logManager.info("CREATION d'un PEI ${newPei.newPeiId}")
            val peiData: PeiData =
                if (newPei.newPeiTypePei == TypePei.PIBI) {
                    PibiData(
                        peiId = newPei.newPeiId,
                        peiNumeroComplet = null,
                        peiNumeroInterne = null,
                        peiDisponibiliteTerrestre = Disponibilite.INDISPONIBLE,
                        peiTypePei = newPei.newPeiTypePei,
                        peiGeometrie = newPei.newPeiGeometrie,
                        peiAutoriteDeciId = null,
                        peiServicePublicDeciId = null,
                        peiMaintenanceDeciId = null,
                        peiCommuneId = newPei.newPeiCommuneId,
                        peiVoieId = newPei.newPeiVoieId,
                        peiNumeroVoie = null,
                        peiSuffixeVoie = null,
                        peiVoieTexte = null,
                        peiLieuDitId = newPei.newPeiLieuDitId,
                        peiCroisementId = null,
                        peiComplementAdresse = null,
                        peiEnFace = false,
                        peiDomaineId = newPei.newPeiDomaineId,
                        peiNatureId = newPei.newPeiNatureId,
                        peiSiteId = null,
                        peiGestionnaireId = newPei.newPeiGestionnaireId,
                        peiNatureDeciId = newPei.newPeiNatureDeciId,
                        peiZoneSpecialeId = null,
                        peiAnneeFabrication = null,
                        peiNiveauId = null,
                        peiObservation = newPei.newPeiObservation,
                        peiPerenne = null,
                        peiRotation6Ccf = null,
                        peiNumeroInterneInitial = null,
                        peiCommuneIdInitial = null,
                        peiZoneSpecialeIdInitial = null,
                        peiNatureDeciIdInitial = null,
                        peiDomaineIdInitial = null,
                        pibiDiametreId = null,
                        pibiServiceEauId = null,
                        pibiIdentifiantGestionnaire = null,
                        pibiRenversable = false,
                        pibiDispositifInviolabilite = false,
                        pibiModeleId = null,
                        pibiMarqueId = null,
                        pibiReservoirId = null,
                        pibiDebitRenforce = false,
                        pibiTypeCanalisationId = null,
                        pibiTypeReseauId = null,
                        pibiDiametreCanalisation = null,
                        pibiSurpresse = false,
                        pibiAdditive = false,
                        pibiJumeleId = null,
                        peiDateChangementDispo = null,
                        peiDateReleveGps = null,
                    )
                } else {
                    PenaData(
                        peiId = newPei.newPeiId,
                        peiNumeroComplet = null,
                        peiNumeroInterne = null,
                        peiDisponibiliteTerrestre = Disponibilite.INDISPONIBLE,
                        peiTypePei = newPei.newPeiTypePei,
                        peiGeometrie = newPei.newPeiGeometrie,
                        peiAutoriteDeciId = null,
                        peiServicePublicDeciId = null,
                        peiMaintenanceDeciId = null,
                        peiCommuneId = newPei.newPeiCommuneId,
                        peiVoieId = newPei.newPeiVoieId,
                        peiNumeroVoie = null,
                        peiSuffixeVoie = null,
                        peiVoieTexte = null,
                        peiLieuDitId = newPei.newPeiLieuDitId,
                        peiCroisementId = null,
                        peiComplementAdresse = null,
                        peiEnFace = false,
                        peiDomaineId = newPei.newPeiDomaineId,
                        peiNatureId = newPei.newPeiNatureId,
                        peiSiteId = null,
                        peiGestionnaireId = newPei.newPeiGestionnaireId,
                        peiNatureDeciId = newPei.newPeiNatureDeciId,
                        peiZoneSpecialeId = null,
                        peiAnneeFabrication = null,
                        peiNiveauId = null,
                        peiObservation = newPei.newPeiObservation,
                        peiPerenne = null,
                        peiRotation6Ccf = null,
                        peiNumeroInterneInitial = null,
                        peiCommuneIdInitial = null,
                        peiZoneSpecialeIdInitial = null,
                        peiNatureDeciIdInitial = null,
                        peiDomaineIdInitial = null,
                        penaDisponibiliteHbe = Disponibilite.INDISPONIBLE,
                        penaCapacite = null,
                        penaCapaciteIllimitee = false,
                        penaCapaciteIncertaine = false,
                        penaQuantiteAppoint = null,
                        penaMateriauId = null,
                        peiDateChangementDispo = null,
                        peiDateReleveGps = null,
                    )
                }

            logManager.info("POJO - création d'un PEI : $peiData")
            // On délègue la création à notre superbe usecase
            val result = createPeiUseCase.execute(
                userInfo,
                peiData,
                mainTransaction,
            )

            if (result !is Result.Success && result !is Result.Created) {
                if (result is Result.Error) {
                    logManager.error("Erreur lors de l'insertion du PEI ${newPei.newPeiId} : ${result.message}")
                }
            }

            // Suppression des newPei
            logManager.info("Suppression des nouveaux PEI")
            incomingRepository.deleteNewPei(newPei.newPeiId)
        }
    }
}
