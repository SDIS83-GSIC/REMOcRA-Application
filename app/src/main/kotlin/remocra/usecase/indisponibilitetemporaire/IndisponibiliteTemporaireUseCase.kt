package remocra.usecase.indisponibilitetemporaire

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.Params
import remocra.data.enums.ErrorType
import remocra.data.enums.StatutIndisponibiliteTemporaireEnum
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import java.util.UUID

class IndisponibiliteTemporaireUseCase : AbstractUseCase() {

    @Inject
    lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    fun getAllWithListPei(params: Params<IndisponibiliteTemporaireRepository.Filter, IndisponibiliteTemporaireRepository.Sort>, userInfo: WrappedUserInfo): Collection<IndisponibiliteTemporaireRepository.IndisponibiliteTemporaireWithPei> {
        val listeIndisponibiliteTemporaire = indisponibiliteTemporaireRepository.getAllWithListPei(params, userInfo.isSuperAdmin, userInfo.zoneCompetence?.zoneIntegrationId)

        val listeIndispoTempNonModifiable = indisponibiliteTemporaireRepository.getIndispoTemporaireHorsZC(
            userInfo.isSuperAdmin,
            userInfo.zoneCompetence?.zoneIntegrationId,
            listeIndisponibiliteTemporaire.map { it.indisponibiliteTemporaireId },
        )

        listeIndisponibiliteTemporaire.forEach {
            it.isModifiable = !listeIndispoTempNonModifiable.contains(it.indisponibiliteTemporaireId)
        }

        // Le statut est calculé en kotlin ce n'est pas une info stockée en base
        // On filtre côté back pour éviter de réimplémenter le calcul en BDD
        params.filterBy?.indisponibiliteTemporaireStatut?.let {
                statusSearch ->

            when (statusSearch) {
                StatutIndisponibiliteTemporaireEnum.EN_COURS_PLANIFIEE -> {
                    return listeIndisponibiliteTemporaire.filter {
                        it.indisponibiliteTemporaireStatut == StatutIndisponibiliteTemporaireEnum.EN_COURS ||
                            it.indisponibiliteTemporaireStatut == StatutIndisponibiliteTemporaireEnum.PLANIFIEE
                    }
                }
                else -> {
                    return listeIndisponibiliteTemporaire.filter { it.indisponibiliteTemporaireStatut == statusSearch }
                }
            }
        }

        return listeIndisponibiliteTemporaire
    }

    fun getDataFromId(idIndisponibiliteTemporaire: UUID): IndisponibiliteTemporaireData {
        val indisponibiliteTemporaireData = indisponibiliteTemporaireRepository.getWithListPeiById(idIndisponibiliteTemporaire)
            ?: throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_INEXISTANTE)

        return indisponibiliteTemporaireData
    }
}
