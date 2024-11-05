package remocra.usecase.indisponibilitetemporaire

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.Params
import remocra.data.enums.ErrorType
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import java.util.UUID

class IndisponibiliteTemporaireUseCase : AbstractUseCase() {

    @Inject
    lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    fun getAllWithListPei(params: Params<IndisponibiliteTemporaireRepository.Filter, IndisponibiliteTemporaireRepository.Sort>, userInfo: UserInfo): Collection<IndisponibiliteTemporaireRepository.IndisponibiliteTemporaireWithPei> {
        val listeIndisponibiliteTemporaire = indisponibiliteTemporaireRepository.getAllWithListPei(params, userInfo.isSuperAdmin, userInfo.zoneCompetence?.zoneIntegrationId)

        // Le statut est calculé en kotlin ce n'est pas une info stockée en base
        // On filtre côté back pour éviter de réimplémenter le calcul en BDD
        params.filterBy?.indisponibiliteTemporaireStatut?.let {
                statusSearch ->
            return listeIndisponibiliteTemporaire.filter { it.indisponibiliteTemporaireStatut == statusSearch }
        }

        return listeIndisponibiliteTemporaire
    }

    fun getAllWithListPeiByPei(idPei: UUID): List<IndisponibiliteTemporaireData> {
        val listeIndisponibiliteTemporaire = indisponibiliteTemporaireRepository.getWithListPeiByPei(idPei)

        return listeIndisponibiliteTemporaire
    }
    fun getDataFromId(idIndisponibiliteTemporaire: UUID): IndisponibiliteTemporaireData {
        val indisponibiliteTemporaireData = indisponibiliteTemporaireRepository.getWithListPeiById(idIndisponibiliteTemporaire)
            ?: throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_INEXISTANTE)

        return indisponibiliteTemporaireData
    }
}
