package remocra.api.usecase

import jakarta.inject.Inject
import remocra.api.PeiUtils
import remocra.auth.WrappedUserInfo
import remocra.data.ApiVisiteFormData
import remocra.data.ApiVisiteSpecifiqueData
import remocra.data.CreationVisiteCtrl
import remocra.data.VisiteData
import remocra.data.enums.ErrorType
import remocra.db.AnomalieRepository
import remocra.db.PeiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.exception.RemocraResponseException
import remocra.usecase.visites.CreateVisiteUseCase
import remocra.usecase.visites.DeleteVisiteUseCase
import java.util.UUID

class ApiVisitesUseCase @Inject
constructor(
    override val peiRepository: PeiRepository,
    private val visiteRepository: VisiteRepository,
    private val anomalieRepository: AnomalieRepository,
    private val createVisiteUseCase: CreateVisiteUseCase,
    private val deleteVisiteUseCase: DeleteVisiteUseCase,
) : AbstractApiPeiUseCase(peiRepository) {

    fun getAll(numeroComplet: String, typeVisiteString: String?, momentString: String?, derniereOnly: Boolean?, limit: Int?, offset: Int?): Result.Success =
        Result.Success(
            visiteRepository.getAllForApi(
                numeroComplet = numeroComplet,
                typeVisite = typeVisiteString?.let { getTypeVisiteFromString(it) },
                moment = momentString?.let { dateUtils.getMomentForResponse(it) },
                derniereOnly = derniereOnly ?: false,
                limit = limit,
                offset = offset,
            ),
        )

    /**
     * Retourne le [TypeVisite] associé à la chaîne passée en paramètre, ou déclenche une [RemocraResponseException] si la conversion n'est pas possible
     *
     * @param typeVisiteString: String?
     *
     * @return [TypeVisite]
     */
    @Throws(RemocraResponseException::class)
    fun getTypeVisiteFromString(typeVisiteString: String?): TypeVisite {
        try {
            // Une valeur inconnue déclenchera une IllegalArgumentException. On en fait autant si le type de visite est null...
            return typeVisiteString?.let { TypeVisite.valueOf(typeVisiteString) } ?: throw IllegalArgumentException()
        } catch (iae: IllegalArgumentException) {
            // ... et on transforme dans le format qui nous intéresse
            throw RemocraResponseException(ErrorType.CODE_TYPE_VISITE_INEXISTANT)
        }
    }

    /**
     * Permet d'ajouter une visite. Délègue le traitement à [CreateVisiteUseCase], en ayant transformé les données d'entrée
     * @param numeroComplet: String
     * @param form: ApiVisiteFormData
     *
     * @return [Result]
     *
     */
    fun addVisite(numeroComplet: String, form: ApiVisiteFormData, userInfo: WrappedUserInfo): Result {
        val pei = getPeiSpecifique(numeroComplet, userInfo)
        try {
            checkDroits(pei, userInfo)
        } catch (rre: RemocraResponseException) {
            return Result.Error(rre.message)
        }

        val typeVisite = getTypeVisiteFromString(form.typeVisite)

        if (!isTypeVisiteAllowed(pei.peiServicePublicDeciId, pei.peiMaintenanceDeciId, typeVisite, userInfo)) {
            return Result.Forbidden(ErrorType.API_TYPE_VISITE_FORBIDDEN.name)
        }

        checkAnomalies(typeVisite, pei.peiNatureId, form.anomaliesControlees, form.anomaliesConstatees)

        // Conversion anomalies contrôlées et constatées Liste de code => liste d'id
        val anomaliesControleesIds = anomalieRepository.getIdsByCodes(form.anomaliesControlees)
        val anomaliesConstateesIds = anomalieRepository.getIdsByCodes(form.anomaliesConstatees)

        // Récupération des anomalies de la visite précédente
        val anomaliesLastVisite = visiteRepository.getLastVisite(pei.peiId)?.let {
            visiteRepository.getAnomaliesFromVisite(it.visiteId)
        } ?: listOf()

        val anomaliesIds = mutableListOf<UUID>()

        // On reprend les anomalies précédentes qui n'ont pas été contrôlées
        anomaliesIds += anomaliesLastVisite.filterNot { it in anomaliesControleesIds }

        // On ajoute les anomalies contrôlées et constatées pour obtenir la liste des anomalies de cette visite
        anomaliesIds += anomaliesConstateesIds

        val isCtrlDebitPression = form.debit != null || form.pression != null || form.pressionDynamique != null

        val visiteData = VisiteData(
            visiteId = UUID.randomUUID(),
            visitePeiId = pei.peiId,
            visiteDate = dateUtils.getMomentForResponse(form.date),
            visiteTypeVisite = getTypeVisiteFromString(form.typeVisite),
            visiteAgent1 = form.agent1,
            visiteAgent2 = form.agent2,
            visiteObservation = form.observations,
            listeAnomalie = anomaliesIds,
            isCtrlDebitPression = isCtrlDebitPression,
            ctrlDebitPression = if (isCtrlDebitPression) CreationVisiteCtrl(form.debit, form.pression?.toBigDecimal(), form.pressionDynamique?.toBigDecimal()) else null,
        )

        return createVisiteUseCase.execute(userInfo, visiteData)
    }

    fun getVisiteSpecifique(numeroComplet: String, visiteIdString: String, userInfo: WrappedUserInfo): Result {
        try {
            val pei = getPeiSpecifique(numeroComplet, userInfo)
            checkDroits(pei, userInfo)

            val visiteId = UUID.fromString(visiteIdString)
            val visite: ApiVisiteSpecifiqueData = visiteRepository.getVisiteForApi(visiteId)
            return Result.Success(visite)
        } catch (rre: RemocraResponseException) {
            return Result.Error(rre.message)
        } catch (iae: IllegalArgumentException) {
            return Result.Error(ErrorType.BAD_UUID.toString())
        }
    }

    /**
     * Permet la suppression d'une visite. Transforme les paramètres en entrée pour déléguer le traitement à [DeleteVisiteUseCase]
     *
     * @return [Result]
     */
    fun deleteVisite(numeroComplet: String, visiteIdString: String, userInfo: WrappedUserInfo): Result {
        try {
            val visiteComplete = visiteRepository.getVisiteCompleteByVisiteId(UUID.fromString(visiteIdString))
            val pei = getPeiSpecifique(numeroComplet, userInfo)

            if (visiteComplete.visitePeiId != pei.peiId) {
                return Result.Error(ErrorType.VISITE_INEXISTANTE.toString())
            }

            val visiteId = visiteComplete.visiteId

            val ctrl = visiteRepository.getCtrlByVisiteId(visiteId)
            val listAnomalies = visiteRepository.getAnomaliesFromVisite(visiteId)

            val visiteDataToDelete = VisiteData(
                visiteId = visiteId,
                visitePeiId = visiteComplete.visitePeiId,
                visiteDate = visiteComplete.visiteDate,
                visiteTypeVisite = visiteComplete.visiteTypeVisite,
                visiteAgent1 = visiteComplete.visiteAgent1,
                visiteAgent2 = visiteComplete.visiteAgent2,
                visiteObservation = visiteComplete.visiteObservation,
                listeAnomalie = listAnomalies.toList(),
                isCtrlDebitPression = ctrl != null,
                ctrlDebitPression = CreationVisiteCtrl(
                    visiteCtrlDebitPressionDebit = ctrl?.visiteCtrlDebitPressionDebit,
                    visiteCtrlDebitPressionPression = ctrl?.visiteCtrlDebitPressionPression,
                    visiteCtrlDebitPressionPressionDyn = ctrl?.visiteCtrlDebitPressionPressionDyn,
                ),
            )
            // TODO vérifier 2201, 2110 au moins
            return deleteVisiteUseCase.execute(
                userInfo = userInfo,
                element = visiteDataToDelete,
            )
        } catch (iae: IllegalArgumentException) {
            return Result.Error(ErrorType.BAD_UUID.toString())
        }
    }

    // TODO pas possible depuis l'IHM, mais possible depuis l'API. Que veut-on faire en V3 ? cf #225131
    fun updateVisite(numeroComplet: String, idVisite: String, form: ApiVisiteFormData): Result {
        TODO("pas encore implémenté $numeroComplet, $idVisite, $form")
    }

    /**
     * Indique si l'utilisateur a les droits d'ajout/édition/suppression sur la visite d'un PEI.
     * La règle est la suivante:
     * - Si utilisateur est le service des eaux (seulement) du PEI => Visites NP uniquement
     * - Si l'utilisateur est la maintenance DECI OU le service public => Visites NP, CTRL et CREA uniquement
     *
     * À ce stade, on considère que la vérification de l'accessibilité du PEI a déjà été faite
     *
     * @param peiServicePublicDeciId UUID?
     * @param peiMaintenanceDeciId UUID?
     * @param typeVisite TypeVisite
     * @param userInfo WrappedUserInfo
     * @return boolean
     */
    fun isTypeVisiteAllowed(peiServicePublicDeciId: UUID?, peiMaintenanceDeciId: UUID?, typeVisite: TypeVisite, userInfo: WrappedUserInfo): Boolean {
        if (!listOf(TypeVisite.NP, TypeVisite.CTP, TypeVisite.RECEPTION).contains(typeVisite)) {
            return false
        }
        if (PeiUtils.OrganismeIdType(userInfo).isApiAdmin) {
            return true
        }

        if (listOf(TypeVisite.CTP, TypeVisite.RECEPTION).contains(typeVisite)) {
            return PeiUtils.isServicePublicDECI(peiServicePublicDeciId, PeiUtils.OrganismeIdType(userInfo)) || PeiUtils.isMaintenanceDECI(peiMaintenanceDeciId, PeiUtils.OrganismeIdType(userInfo))
        }
        return true
    }

    /**
     * Vérifie que les anomalies contrôlées et constatées sont bien compatibles avec le type de visite
     * spécifié, ainsi que la cohérence entre les anomalies constatées et contrôlées
     *
     * @param peiNatureId Identifiant du type de saisie
     * @param typeVisite Code du type de visite
     * @param listControlees Liste de code des anomalies contrôlées
     * @param listConstatees Liste de code des anomalies constatées
     * @throws ResponseException
     */
    fun checkAnomalies(
        typeVisite: TypeVisite,
        peiNatureId: UUID,
        listControlees: Collection<String>,
        listConstatees: Collection<String>,
    ) {
        val nbAnomaliesChecked = anomalieRepository.getNbAnomaliesChecked(
            peiNatureId = peiNatureId,
            typeVisite = typeVisite,
            listControlees = listControlees,
        )
        if (nbAnomaliesChecked != listControlees.size) {
            throw RemocraResponseException(ErrorType.API_ERROR_NB_ANOMALIE_CONTROLEE)
        }
        if (!listControlees.containsAll(listConstatees)) {
            throw RemocraResponseException(ErrorType.API_ANOMALIE_CONSTATEE_NOT_CONTROLEE)
        }
    }
}
