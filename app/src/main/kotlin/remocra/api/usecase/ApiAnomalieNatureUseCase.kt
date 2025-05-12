package remocra.api.usecase

import jakarta.inject.Inject
import remocra.data.ApiAnomalieWithNature
import remocra.data.enums.ErrorType
import remocra.db.AnomalieRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase

class ApiAnomalieNatureUseCase @Inject constructor(
    private val anomalieRepository: AnomalieRepository,
) : AbstractUseCase() {

    fun execute(natureCode: String, typeVisite: String?, typePei: TypePei, limit: Int?, offset: Int?): Collection<ApiAnomalieWithNature> {
        // On vérifie si le typeVisite est bien un TypeVisite
        val typeVisiteCast = typeVisite.takeIf { !it.isNullOrEmpty() }?.let {
            TypeVisite.entries.firstOrNull { v -> v.literal == it } ?: throw RemocraResponseException(ErrorType.CODE_TYPE_VISITE_INEXISTANT)
        }

        return anomalieRepository.getAnomalieWithNature(
            natureCode,
            typeVisiteCast,
            typePei,
            limit,
            offset,
        )
    }
}
