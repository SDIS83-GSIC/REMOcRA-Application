package remocra.usecase.indisponibiliteTemporaire

import com.google.inject.Inject
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.Params
import remocra.data.enums.ErrorType
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import java.time.Clock
import java.util.UUID

class IndisponibiliteTemporaireUseCase : AbstractUseCase() {

    @Inject
    lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    @Inject
    lateinit var clock: Clock

    fun getAllWithListPei(params: Params<IndisponibiliteTemporaireRepository.Filter, IndisponibiliteTemporaireRepository.Sort>): Collection<IndisponibiliteTemporaireRepository.IndisponibiliteTemporaireWithPei> {
        val listeIndisponibiliteTemporaire = indisponibiliteTemporaireRepository.getAllWithListPei(params)

        // Le statut est calculé en kotlin ce n'est pas une info stockée en base
        // On filtre côté back pour éviter de réimplémenter le calcul en BDD
        params.filterBy?.indisponibiliteTemporaireStatut?.let {
                statusSearch ->
            return listeIndisponibiliteTemporaire.filter { it.indisponibiliteTemporaireStatut == statusSearch }
        }

        return listeIndisponibiliteTemporaire
    }

    fun getAllWithListPeiByPei(idPei: UUID): List<IndisponibiliteTemporaireData>? {
        val listeIndisponibiliteTemporaire = indisponibiliteTemporaireRepository.getWithListPeiByPei(idPei)

        return listeIndisponibiliteTemporaire
    }
    fun getDataFromId(idIndisponibiliteTemporaire: UUID): IndisponibiliteTemporaireData {
        val indisponibiliteTemporaireData = indisponibiliteTemporaireRepository.getWithListPeiById(idIndisponibiliteTemporaire)
            ?: throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_INEXISTANTE)

        return indisponibiliteTemporaireData
    }
}
