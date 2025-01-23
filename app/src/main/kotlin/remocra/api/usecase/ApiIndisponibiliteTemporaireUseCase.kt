package remocra.api.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import remocra.data.ApiIndispoTempFormData
import remocra.data.ApiIndispoTemporaireData
import remocra.data.AuteurTracabiliteData
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.Params
import remocra.data.enums.ErrorType
import remocra.data.enums.StatutIndisponibiliteTemporaireEnum
import remocra.data.enums.TypeSourceModification
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.PeiRepository
import remocra.db.TracabiliteRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.indisponibilitetemporaire.CreateIndisponibiliteTemporaireUseCase
import remocra.usecase.indisponibilitetemporaire.UpdateIndisponibiliteTemporaireUseCase
import java.util.UUID

class ApiIndisponibiliteTemporaireUseCase : AbstractUseCase() {

    @Inject
    lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    @Inject
    lateinit var tracabiliteRepository: TracabiliteRepository

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var createIndisponibiliteTemporaireUseCase: CreateIndisponibiliteTemporaireUseCase

    @Inject
    lateinit var updateIndisponibiliteTemporaireUseCase: UpdateIndisponibiliteTemporaireUseCase

    @Inject
    lateinit var objectMapper: ObjectMapper

    fun getAll(codeOrganisme: String?, numeroComplet: String?, statut: String?, limit: Int?, offset: Int?): Collection<ApiIndispoTemporaireData> {
        // On vérifie que le statut de l'indisponibilité est conforme
        val statutIndisponibiliteTemporaireEnum = statut?.let {
            try {
                StatutIndisponibiliteTemporaireEnum.valueOf(it)
            } catch (e: Exception) {
                throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_STATUT)
            }
        }

        val params: Params<IndisponibiliteTemporaireRepository.Filter, IndisponibiliteTemporaireRepository.Sort> = Params(
            filterBy = IndisponibiliteTemporaireRepository.Filter(
                indisponibiliteTemporaireMotif = null,
                indisponibiliteTemporaireObservation = null,
                indisponibiliteTemporaireStatut = statutIndisponibiliteTemporaireEnum,
                indisponibiliteTemporaireBasculeAutoIndisponible = null,
                indisponibiliteTemporaireBasculeAutoDisponible = null,
                indisponibiliteTemporaireMailAvantIndisponibilite = null,
                indisponibiliteTemporaireMailApresIndisponibilite = null,
                listePeiId = numeroComplet?.let {
                    listOf(peiRepository.getIdByNumeroComplet(it) ?: throw RemocraResponseException(ErrorType.PEI_INEXISTANT))
                },
            ),
            sortBy = null,
            limit = limit,
            offset = offset,
        )

        // TODO envoyer la zone de compétence de l'organisme connecté quand on aura le userInfo
        val listIndispoTemp = indisponibiliteTemporaireRepository.getAllWithListPei(params, isSuperAdmin = false, zoneCompetenceId = null) // TODO

        // On va ensuite chercher toutes les infos de traçabilité sur ces indipos
        val tracabilitesIndispoTemp = tracabiliteRepository.getIndispoTempTracabilite(listIndispoTemp.map { it.indisponibiliteTemporaireId })

        val liste = listIndispoTemp.map {
            // On va chercher la dernière modification de l'indispo
            val derniereMiseAJour =
                tracabilitesIndispoTemp.filter { t -> t.tracabiliteObjetId == it.indisponibiliteTemporaireId }
                    .maxByOrNull { it.tracabiliteDate }

            var organismeMaj: String? = null

            if (derniereMiseAJour != null) {
                val modificateur =
                    objectMapper.readValue<AuteurTracabiliteData>(derniereMiseAJour.tracabiliteObjetData.toString())
                if (modificateur.typeSourceModification == TypeSourceModification.API) {
                    organismeMaj = modificateur.nom
                }
            }

            ApiIndispoTemporaireData(
                indisponibiliteTemporaireId = it.indisponibiliteTemporaireId,
                indisponibiliteTemporaireDateDebut = it.indisponibiliteTemporaireDateDebut,
                indisponibiliteTemporaireDateFin = it.indisponibiliteTemporaireDateFin,
                indisponibiliteTemporaireMotif = it.indisponibiliteTemporaireMotif,
                indisponibiliteTemporaireObservation = it.indisponibiliteTemporaireObservation,
                indisponibiliteTemporaireBasculeAutoIndisponible = it.indisponibiliteTemporaireBasculeAutoIndisponible,
                indisponibiliteTemporaireBasculeAutoDisponible = it.indisponibiliteTemporaireBasculeAutoDisponible,
                indisponibiliteTemporaireMailAvantIndisponibilite = it.indisponibiliteTemporaireMailAvantIndisponibilite,
                indisponibiliteTemporaireMailApresIndisponibilite = it.indisponibiliteTemporaireMailApresIndisponibilite,
                listeNumeroPei = it.listeNumeroPei,
                organismeApiMaj = organismeMaj,
            )
        }

        return codeOrganisme?.let { liste.filter { it.organismeApiMaj != null && it.organismeApiMaj.equals(it) } } ?: liste
    }

    fun addIndispoTemp(apiIndispoTempFormData: ApiIndispoTempFormData): Result {
        return createIndisponibiliteTemporaireUseCase.execute(
            null, // TODO userInfo
            getIndisponibiliteTemporaiteData(apiIndispoTempFormData),
        )
    }

    fun updateIndispoTemp(apiIndispoTempFormData: ApiIndispoTempFormData, indispoTemporaireId: UUID): Result {
        return updateIndisponibiliteTemporaireUseCase.execute(
            null, // TODO userInfo
            getIndisponibiliteTemporaiteData(apiIndispoTempFormData, indispoTemporaireId),
        )
    }

    private fun getIndisponibiliteTemporaiteData(apiIndispoTempFormData: ApiIndispoTempFormData, indispoTemporaireId: UUID? = null): IndisponibiliteTemporaireData {
        val listePeiId = peiRepository.getIdByNumeroComplet(apiIndispoTempFormData.listeNumeroPei)
        return IndisponibiliteTemporaireData(
            indisponibiliteTemporaireId = indispoTemporaireId ?: UUID.randomUUID(),
            indisponibiliteTemporaireMotif = apiIndispoTempFormData.motif,
            indisponibiliteTemporaireObservation = apiIndispoTempFormData.observation,
            indisponibiliteTemporaireDateDebut = apiIndispoTempFormData.dateDebut,
            indisponibiliteTemporaireMailAvantIndisponibilite = apiIndispoTempFormData.mailAvantIndisponibilite,
            indisponibiliteTemporaireMailApresIndisponibilite = apiIndispoTempFormData.mailApresIndisponibilite,
            indisponibiliteTemporaireBasculeAutoDisponible = apiIndispoTempFormData.basculeAutoDisponible,
            indisponibiliteTemporaireBasculeAutoIndisponible = apiIndispoTempFormData.basculeAutoDisponible,
            indisponibiliteTemporaireNotificationDebut = apiIndispoTempFormData.notificationDebut,
            indisponibiliteTemporaireNotificationFin = apiIndispoTempFormData.notificationFin,
            indisponibiliteTemporaireNotificationResteIndispo = apiIndispoTempFormData.notificationResteIndispo,
            indisponibiliteTemporaireBasculeDebut = false,
            indisponibiliteTemporaireBasculeFin = false,
            indisponibiliteTemporaireDateFin = apiIndispoTempFormData.dateFin,
            indisponibiliteTemporaireListePeiId = listePeiId,
        )
    }
}
