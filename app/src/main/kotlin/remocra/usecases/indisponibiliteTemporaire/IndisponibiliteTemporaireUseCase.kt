package remocra.usecases.indisponibiliteTemporaire

import com.google.inject.Inject
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.Params
import remocra.data.enums.ErrorType
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.exception.RemocraResponseException
import java.time.Clock
import java.util.UUID

class IndisponibiliteTemporaireUseCase {

    @Inject
    lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    @Inject
    lateinit var clock: Clock

    fun getAllWithListPei(params: Params<IndisponibiliteTemporaireRepository.Filter, IndisponibiliteTemporaireRepository.Sort>): Collection<IndisponibiliteTemporaireRepository.IndisponibiliteTemporaireWithPei> {
        return indisponibiliteTemporaireRepository.getAllWithListPei(params)
    }

    fun getDataFromId(id: UUID): IndisponibiliteTemporaireData {
        val indisponibiliteTemporaireData = indisponibiliteTemporaireRepository.getWithListPeiById(id)
            ?: throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_INEXISTANTE)

        return indisponibiliteTemporaireData
    }
}
