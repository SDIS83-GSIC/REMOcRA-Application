package remocra.usecase.visites

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.VisiteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.AnomalieRepository
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.LPeiAnomalie
import remocra.db.jooq.remocra.tables.pojos.LVisiteAnomalie
import remocra.db.jooq.remocra.tables.pojos.Visite
import remocra.db.jooq.remocra.tables.pojos.VisiteCtrlDebitPression
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.pei.UpdatePeiUseCase
import java.time.ZonedDateTime

class CreateVisiteUseCase @Inject constructor(
    private val visiteRepository: VisiteRepository,
    private val anomalieRepository: AnomalieRepository,
    private val updatePeiUseCase: UpdatePeiUseCase,
    private val peiRepository: PeiRepository,
    private val pibiRepository: PibiRepository,
    private val penaRepository: PenaRepository,

) : AbstractCUDUseCase<VisiteData>(TypeOperation.INSERT) {

    override fun checkDroits(userInfo: UserInfo) {
    }

    override fun postEvent(element: VisiteData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.visiteId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.VISITE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(clock),
            ),
        )
        // TODO : Gestion "notification changement état" et autres jobs
    }

    override fun checkContraintes(userInfo: UserInfo?, element: VisiteData) {
        if (userInfo == null) {
            throw RemocraResponseException(errorType = ErrorType.VISITE_C_FORBIDDEN)
        }

        // Vérification des droits de création
        when (element.visiteTypeVisite) {
            TypeVisite.CTP -> if (!userInfo.droits.contains(Droit.VISITE_CONTROLE_TECHNIQUE_C)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_C_CTP_FORBIDDEN)
            }
            TypeVisite.RECEPTION -> if (!userInfo.droits.contains(Droit.VISITE_RECEP_C)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_C_RECEPTION_FORBIDDEN)
            }
            TypeVisite.RECO_INIT -> if (!userInfo.droits.contains(Droit.VISITE_RECO_INIT_C)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_C_RECO_INIT_FORBIDDEN)
            }
            TypeVisite.RECOP -> if (!userInfo.droits.contains(Droit.VISITE_RECO_C)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_C_RECOP_FORBIDDEN)
            }
            TypeVisite.NP -> if (!userInfo.droits.contains(Droit.VISITE_NON_PROGRAMME_C)) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_C_NP_FORBIDDEN)
            }
        }

        // La visite n'est pas dans le future
        if (element.visiteDate.isAfter(ZonedDateTime.now())) {
            throw RemocraResponseException(ErrorType.VISITE_AFTER_NOW)
        }

        // La visite n'est pas antérieur à la dernière visite du pei
        val lastVisite = visiteRepository.getLastVisite(element.visitePeiId)
        if (lastVisite != null) {
            if (element.visiteDate.isBefore(lastVisite.visiteDate)) {
                throw RemocraResponseException(ErrorType.VISITE_CREATE_NOT_LAST)
            }
        }

        // Vérification de la validité du CDP
        if (element.isCtrlDebitPression && // Si la case CDP est coché
            (
                (element.ctrlDebitPression == null) || // Et que l'objet est null
                    (
                        element.ctrlDebitPression?.ctrlDebit == null && // Ou ne contient que des valeurs null
                            element.ctrlDebitPression?.ctrlPression == null &&
                            element.ctrlDebitPression?.ctrlPressionDyn == null
                        )
                )
        ) {
            throw RemocraResponseException(ErrorType.VISITE_CDP_INVALIDE)
        }
    }

    override fun execute(userInfo: UserInfo?, element: VisiteData): VisiteData {
        // Insertion de la visite : remocra.visite
        visiteRepository.insertVisite(
            Visite(
                visiteId = element.visiteId,
                visitePeiId = element.visitePeiId,
                visiteDate = element.visiteDate,
                visiteTypeVisite = element.visiteTypeVisite,
                visiteAgent1 = element.visiteAgent1,
                visiteAgent2 = element.visiteAgent2,
                visiteObservation = element.visiteObservation,
            ),
        )

        // Insertion du CDP, si nécessaire : remocra.visite_ctrl_debit_pression
        if (element.isCtrlDebitPression && element.ctrlDebitPression != null) {
            visiteRepository.insertCDP(
                VisiteCtrlDebitPression(
                    visiteCtrlDebitPressionVisiteId = element.visiteId,
                    visiteCtrlDebitPressionDebit = element.ctrlDebitPression!!.ctrlDebit,
                    visiteCtrlDebitPressionPression = element.ctrlDebitPression!!.ctrlPression,
                    visiteCtrlDebitPressionPressionDyn = element.ctrlDebitPression!!.ctrlPressionDyn,
                ),
            )
        }

        // Gestion des anomalies
        // 1- Suppression de toutes les anomalies non-protégées du pei : remocra.l_pei_anomalie
        anomalieRepository.deleteAnomalieNonSystemByPeiId(element.visitePeiId)
        // 2- Si la visite contient des anomalies :
        element.listeAnomalie.let { anomalies ->
            // 2.1- remocra.l_pei_anomalie
            // 2.1.1- Création d'une liste d'objets l_pei_anomalie à insérer
            val peiAnomalieToInsert = anomalies.map { anomalieId ->
                LPeiAnomalie(element.visitePeiId, anomalieId)
            }
            // 2.1.2- Insertion en masse
            anomalieRepository.batchInsertLPeiAnomalie(peiAnomalieToInsert)
            // 2.2- remocra.l_visite_anomalie
            // 2.2.1- Création d'une liste d'objets l_visite_anomalie à insérer
            val visiteAnomalieToInsert = anomalies.map { anomalieId ->
                LVisiteAnomalie(element.visiteId, anomalieId)
            }
            // 2.2.2- Insertion en masse
            anomalieRepository.batchInsertLVisiteAnomalie(visiteAnomalieToInsert)
        }

        // On prévient d'une modification du PEI : la dispo peut se retrouver changée par la dernière visite
        val typePei = peiRepository.getTypePei(element.visitePeiId)
        val peiData = if (TypePei.PIBI == typePei) pibiRepository.getInfoPibi(element.visitePeiId) else penaRepository.getInfoPena(element.visitePeiId)

        updatePeiUseCase.execute(
            userInfo = userInfo,
            element = peiData,
            mainTransactionManager = transactionManager,
        )

        return element
    }
}
