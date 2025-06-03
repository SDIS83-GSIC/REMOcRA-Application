package remocra.usecase.visites

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.VisiteData
import remocra.data.enums.ErrorType
import remocra.db.AnomalieRepository
import remocra.db.PeiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.DroitApi
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

class CreateVisiteUseCase @Inject constructor(
    private val visiteRepository: VisiteRepository,
    private val anomalieRepository: AnomalieRepository,
    private val updatePeiUseCase: UpdatePeiUseCase,
    private val peiRepository: PeiRepository,

) : AbstractCUDUseCase<VisiteData>(TypeOperation.INSERT) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
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
        // Si l'on se trouve ici en venant d'un Import CTP,
        // les vérifications faites en amont garantissent la véracité des données
        // On peut donc directement passer à la suite
        if (element.isFromImportCtp) {
            return
        }

        val nbVisite = visiteRepository.getCountVisite(element.visitePeiId)
        if (!arrayOf(TypeVisite.RECO_INIT, TypeVisite.RECEPTION).contains(element.visiteTypeVisite)) {
            if (nbVisite == 0) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_RECEPTION)
            } else if (nbVisite == 1) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_RECO_INIT)
            }
        }

        when (element.visiteTypeVisite) {
            TypeVisite.CTP ->
                if (!userInfo.hasDroits(
                        droitWeb = Droit.VISITE_CONTROLE_TECHNIQUE_C,
                        droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                    )
                ) {
                    throw RemocraResponseException(errorType = ErrorType.VISITE_C_CTP_FORBIDDEN)
                }
            TypeVisite.RECEPTION ->
                if (!userInfo.hasDroits(
                        droitWeb = Droit.VISITE_RECEP_C,
                        droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                    )
                ) {
                    throw RemocraResponseException(errorType = ErrorType.VISITE_C_RECEPTION_FORBIDDEN)
                } else if (visiteRepository.visiteAlreadyExists(element.visitePeiId, element.visiteTypeVisite)) {
                    throw RemocraResponseException(errorType = ErrorType.VISITE_MEME_TYPE_EXISTE)
                } else if (nbVisite != 0) {
                    throw RemocraResponseException(errorType = ErrorType.VISITE_RECEPTION_NOT_FIRST)
                }
            TypeVisite.RECO_INIT ->
                if (!userInfo.hasDroits(
                        droitWeb = Droit.VISITE_RECO_INIT_C,
                        droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                    )
                ) {
                    throw RemocraResponseException(errorType = ErrorType.VISITE_C_RECO_INIT_FORBIDDEN)
                } else if (visiteRepository.visiteAlreadyExists(element.visitePeiId, element.visiteTypeVisite)) {
                    throw RemocraResponseException(errorType = ErrorType.VISITE_MEME_TYPE_EXISTE)
                } else if (nbVisite != 1) {
                    throw RemocraResponseException(errorType = ErrorType.VISITE_RECO_INIT_NOT_FIRST)
                }
            TypeVisite.ROP -> if (!userInfo.hasDroits(
                    droitWeb = Droit.VISITE_RECO_C,
                    droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                )
            ) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_C_ROP_FORBIDDEN)
            }
            TypeVisite.NP -> if (!userInfo.hasDroits(
                    droitWeb = Droit.VISITE_NON_PROGRAMME_C,
                    droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
                )
            ) {
                throw RemocraResponseException(errorType = ErrorType.VISITE_C_NP_FORBIDDEN)
            }
        }

        // La visite n'est pas dans le future
        if (element.visiteDate.isAfter(dateUtils.now())) {
            throw RemocraResponseException(ErrorType.VISITE_AFTER_NOW)
        }

        val typePei = peiRepository.getTypePei(element.visitePeiId)

        // Vérification de la validité du CDP

        if (element.isCtrlDebitPression) {
            if (typePei == TypePei.PIBI && // Si je suis un PIBI
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
            } else if (typePei == TypePei.PENA && // Si je suis un PENA
                (
                    (element.ctrlDebitPression != null) && // Et que l'objet n'est pas null
                        (
                            element.ctrlDebitPression?.ctrlDebit != null || // Ou contient des valeurs non-nulle
                                element.ctrlDebitPression?.ctrlPression != null ||
                                element.ctrlDebitPression?.ctrlPressionDyn != null
                            )
                    )
            ) {
                throw RemocraResponseException(ErrorType.VISITE_CDP_PENA)
            }
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: VisiteData): VisiteData {
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
        if (element.isCtrlDebitPression && peiRepository.getTypePei(element.visitePeiId) == TypePei.PIBI) {
            visiteRepository.insertCDP(
                VisiteCtrlDebitPression(
                    visiteCtrlDebitPressionVisiteId = element.visiteId,
                    visiteCtrlDebitPressionDebit = element.ctrlDebitPression!!.ctrlDebit,
                    visiteCtrlDebitPressionPression = element.ctrlDebitPression!!.ctrlPression,
                    visiteCtrlDebitPressionPressionDyn = element.ctrlDebitPression!!.ctrlPressionDyn,
                ),
            )
        }

        // Si la visite que l'on insert n'est pas la dernière en date, ne pas mettre à jour les anomalies courantes du PEI
        val lastVisite = visiteRepository.getLastVisite(element.visitePeiId)
        val isLastVisiteToDate = !(lastVisite != null && element.visiteDate.isBefore(lastVisite.visiteDate))

        // Si dernière visite en date :
        //  - Suppression de toutes les anomalies non-protégées du pei : l_pei_anomalie
        //  - Ajout des anomalies de la visite au pei : l_pei_anomalie
        // Gestion des anomalies
        if (isLastVisiteToDate) {
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
            }
        }
        // Dans tous les cas, insérer dans l_visite_anomalie
        // 2- Si la visite contient des anomalies :
        element.listeAnomalie.let { anomalies ->
            // 2.2- remocra.l_visite_anomalie
            // 2.2.1- Création d'une liste d'objets l_visite_anomalie à insérer
            val visiteAnomalieToInsert = anomalies.map { anomalieId ->
                LVisiteAnomalie(element.visiteId, anomalieId)
            }
            // 2.2.2- Insertion en masse
            anomalieRepository.batchInsertLVisiteAnomalie(visiteAnomalieToInsert)
        }

        // On prévient d'une modification du PEI : la dispo peut se retrouver changée par la dernière visite
        updatePeiUseCase.updatePeiWithId(
            peiId = element.visitePeiId,
            userInfo = userInfo,
        )
        return element
    }
}
