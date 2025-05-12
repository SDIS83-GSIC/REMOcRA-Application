package remocra.api.usecase

import jakarta.inject.Inject
import remocra.data.ApiDiametreCodeLibelle
import remocra.db.DiametreRepository
import remocra.usecase.AbstractUseCase

class ApiDiametreNatureUseCase @Inject constructor(
    private val diametreRepository: DiametreRepository,
) : AbstractUseCase() {

    fun execute(natureCode: String, limit: Long?, offset: Long?): Collection<ApiDiametreCodeLibelle> =
        diametreRepository.getDiametres(natureCode, limit, offset)
}
