package remocra.api.usecase

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.ApiVisiteFormData
import remocra.data.ApiVisiteSpecifiqueData
import remocra.data.CreationVisiteCtrl
import remocra.data.VisiteData
import remocra.data.enums.ErrorType
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

        val isCtrlDebitPression = form.debit != null || form.pression != null || form.pressionDynamique != null

        val visiteData = VisiteData(
            visiteId = UUID.randomUUID(),
            visitePeiId = pei.peiId,
            visiteDate = dateUtils.getMomentForResponse(form.date),
            visiteTypeVisite = getTypeVisiteFromString(form.typeVisite),
            visiteAgent1 = form.agent1,
            visiteAgent2 = form.agent2,
            visiteObservation = form.observations,
            listeAnomalie = listOf(), // TODO qu'est-ce qui est attendu, les anomalies contrôlées, constatées, ... ?
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
}
