package remocra.api.usecase

import jakarta.inject.Inject
import remocra.api.DateUtils
import remocra.data.ApiVisiteFormData
import remocra.data.ApiVisiteSpecifiqueData
import remocra.data.VisiteData
import remocra.data.enums.ErrorType
import remocra.db.VisiteRepository
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.exception.RemocraResponseException
import remocra.usecases.visites.CreateVisiteUseCase
import remocra.usecases.visites.DeleteVisiteUseCase
import remocra.web.AbstractEndpoint.Result
import remocra.web.visite.VisiteEndPoint
import java.time.ZonedDateTime
import java.util.UUID

class ApiVisitesUseCase : AbstractApiPeiUseCase() {
    @Inject
    lateinit var visiteRepository: VisiteRepository

    @Inject
    lateinit var createVisiteUseCase: CreateVisiteUseCase

    @Inject
    lateinit var deleteVisiteUseCase: DeleteVisiteUseCase

    fun getAll(numeroComplet: String, typeVisiteString: String?, momentString: String?, derniereOnly: Boolean?, limit: Int?, offset: Int?): Result.Success {
        var moment: ZonedDateTime? = null
        if (momentString != null) {
            moment = DateUtils.getMomentForResponse(momentString)
        }

        // TODO tester l'accessibilité, pas fait en V2 mais serait un plus.
        return Result.Success(
            visiteRepository.getAllForApi(
                numeroComplet,
                getTypeVisiteFromString(typeVisiteString),
                moment,
                derniereOnly
                    ?: false,
                limit,
                offset,
            ),
        )
    }

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
    fun addVisite(numeroComplet: String, form: ApiVisiteFormData): Result {
        val pei = getPeiSpecifique(numeroComplet)
        try {
            checkDroits(pei)
        } catch (rre: RemocraResponseException) {
            return Result.Error(rre.message)
        }

        val isCtrlDebitPression = form.debit != null || form.pression != null || form.pressionDynamique != null

        val visiteData = VisiteData(
            visiteId = UUID.randomUUID(),
            visitePeiId = pei.peiId,
            visiteDate = DateUtils.getMomentForResponse(form.date),
            visiteTypeVisite = getTypeVisiteFromString(form.typeVisite),
            visiteAgent1 = form.agent1,
            visiteAgent2 = form.agent2,
            visiteObservation = form.observations,
            listeAnomalie = listOf(), // TODO qu'est-ce qui est attendu, les anomalies contrôlées, constatées, ... ?
            isCtrlDebitPression = isCtrlDebitPression,
            ctrlDebitPression = if (isCtrlDebitPression) VisiteEndPoint.CreationVisiteCtrl(form.debit, form.pression?.toBigDecimal(), form.pressionDynamique?.toBigDecimal()) else null,

        )

        // TODO le user ne correspond pas encore
        return createVisiteUseCase.execute(null, visiteData)
    }

    fun getVisiteSpecifique(numeroComplet: String, visiteIdString: String): Result {
        try {
            val pei = getPeiSpecifique(numeroComplet)
            checkDroits(pei)

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
    fun deleteVisite(numeroComplet: String, visiteIdString: String): Result {
        try {
            val visiteId = UUID.fromString(visiteIdString)
            val visite = visiteRepository.getById(visiteId)
            val pei = getPeiSpecifique(numeroComplet)

            if (visite.visitePeiId != pei.peiId) {
                return Result.Error(ErrorType.VISITE_INEXISTANTE.toString())
            }

            // TODO vérifier 2201, 2110 au moins
            // TODO le userInfo !
            return deleteVisiteUseCase.execute(null, visiteId)
        } catch (iae: IllegalArgumentException) {
            return Result.Error(ErrorType.BAD_UUID.toString())
        }
    }

    // TODO pas possible depuis l'IHM, mais possible depuis l'API. Que veut-on faire en V3 ? cf #225131
    fun updateVisite(numeroComplet: String, idVisite: String, form: ApiVisiteFormData): Result {
        TODO("pas encore implémenté $numeroComplet, $idVisite, $form")
    }
}
