package remocra.api.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.ApiIndispoTempFormData
import remocra.data.ApiIndispoTemporaireData
import remocra.data.AuteurTracabiliteData
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.enums.ErrorType
import remocra.data.enums.StatutIndisponibiliteTemporaireEnum
import remocra.data.enums.TypeSourceModification
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.OrganismeRepository
import remocra.db.PeiRepository
import remocra.db.TracabiliteRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.indisponibilitetemporaire.CreateIndisponibiliteTemporaireUseCase
import remocra.usecase.indisponibilitetemporaire.UpdateIndisponibiliteTemporaireUseCase
import remocra.utils.limitOffset
import java.util.UUID

class ApiIndisponibiliteTemporaireUseCase @Inject constructor(
    private val indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository,
    private val tracabiliteRepository: TracabiliteRepository,
    override val peiRepository: PeiRepository,
    private val organismeRepository: OrganismeRepository,
    private val createIndisponibiliteTemporaireUseCase: CreateIndisponibiliteTemporaireUseCase,
    private val updateIndisponibiliteTemporaireUseCase: UpdateIndisponibiliteTemporaireUseCase,
    private val objectMapper: ObjectMapper,
) : AbstractApiPeiUseCase(peiRepository) {

    fun getAll(codeOrganisme: String?, numeroComplet: String?, statut: String?, limit: Int?, offset: Int?, userInfo: WrappedUserInfo): Collection<ApiIndispoTemporaireData> {
        // On vérifie que le statut de l'indisponibilité est conforme
        val statutIndisponibiliteTemporaireEnum = statut?.let {
            try {
                StatutIndisponibiliteTemporaireEnum.valueOf(it)
            } catch (_: Exception) {
                throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_STATUT)
            }
        }
        // Vérification si le numéro fourni en paramètre existe
        numeroComplet?.let { peiRepository.getIdByNumeroComplet(it) ?: throw RemocraResponseException(ErrorType.PEI_INEXISTANT) }
        // Vérification si l'organisme fourni en paramètre existe
        val organismeFiltre = codeOrganisme?.let { organismeRepository.getByCode(it) ?: throw RemocraResponseException(ErrorType.ORGANISME_INEXISTANT) }

        // On remonte toutes les indispo dont au moins un PEI fait partie de la liste de gestion de l'organisme
        val listeIndispoTemp = indisponibiliteTemporaireRepository.getAllForApi(userInfo.organismeId!!, numeroComplet, statutIndisponibiliteTemporaireEnum)

        // On va ensuite chercher toutes les infos de traçabilité sur ces indipos
        val tracabilitesIndispoTemp = tracabiliteRepository.getIndispoTempTracabilite(listeIndispoTemp.map { it.indisponibiliteTemporaireId })
        val liste = listeIndispoTemp.map { indispoTemporaire ->
            // On va chercher la dernière modification de l'indispo
            val derniereMiseAJour =
                tracabilitesIndispoTemp.filter { t -> t.tracabiliteObjetId == indispoTemporaire.indisponibiliteTemporaireId }
                    .maxByOrNull { it.tracabiliteDate }
            var organismeMaj: String? = null
            if (derniereMiseAJour != null) {
                val modificateur = objectMapper.readValue<AuteurTracabiliteData>(derniereMiseAJour.tracabiliteAuteurData.toString())
                if (modificateur.typeSourceModification == TypeSourceModification.API) {
                    organismeMaj = modificateur.idAuteur.toString()
                }
            }
            indispoTemporaire.copy(organismeApiMaj = organismeMaj)
        }
        // On filtre sur l'organisme
        val filteredList = organismeFiltre?.let { liste.filter { it.organismeApiMaj != null && it.organismeApiMaj == organismeFiltre.organismeId.toString() } } ?: liste
        return filteredList.limitOffset(limit?.toLong(), offset?.toLong()) ?: emptyList()
    }

    fun addIndispoTemp(apiIndispoTempFormData: ApiIndispoTempFormData, userInfo: WrappedUserInfo): Result {
        return createIndisponibiliteTemporaireUseCase.execute(
            userInfo,
            getIndisponibiliteTemporaiteData(apiIndispoTempFormData, userInfo),
        )
    }

    fun updateIndispoTemp(apiIndispoTempFormData: ApiIndispoTempFormData, indispoTemporaireId: UUID, userInfo: WrappedUserInfo): Result {
        return updateIndisponibiliteTemporaireUseCase.execute(
            userInfo,
            getIndisponibiliteTemporaiteData(apiIndispoTempFormData, userInfo, indispoTemporaireId),
        )
    }

    private fun getIndisponibiliteTemporaiteData(apiIndispoTempFormData: ApiIndispoTempFormData, userInfo: WrappedUserInfo, indispoTemporaireId: UUID? = null): IndisponibiliteTemporaireData {
        val listePeiId = peiRepository.getIdByNumeroComplet(apiIndispoTempFormData.listeNumeroPei)
        listePeiId.forEach {
            if (!isPeiAccessible(it, userInfo)) {
                throw RemocraResponseException(
                    ErrorType.FORBIDDEN,
                )
            }
        }

        return IndisponibiliteTemporaireData(
            indisponibiliteTemporaireId = indispoTemporaireId ?: UUID.randomUUID(),
            indisponibiliteTemporaireMotif = apiIndispoTempFormData.motif,
            indisponibiliteTemporaireObservation = apiIndispoTempFormData.observation,
            indisponibiliteTemporaireDateDebut = apiIndispoTempFormData.dateDebut,
            indisponibiliteTemporaireMailAvantIndisponibilite = apiIndispoTempFormData.mailAvantIndisponibilite,
            indisponibiliteTemporaireMailApresIndisponibilite = apiIndispoTempFormData.mailApresIndisponibilite,
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
