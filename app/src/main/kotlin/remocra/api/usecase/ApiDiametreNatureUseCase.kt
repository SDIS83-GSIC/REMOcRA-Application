package remocra.api.usecase

import jakarta.inject.Inject
import remocra.data.ApiDiametreCodeLibelle
import remocra.db.DiametreRepository
import remocra.usecase.AbstractUseCase

class ApiDiametreNatureUseCase : AbstractUseCase() {

    @Inject
    lateinit var diametreRepository: DiametreRepository

    fun execute(natureCode: String, limit: Long?, offset: Long?): Collection<ApiDiametreCodeLibelle> =
        diametreRepository.getDiametres(natureCode, limit, offset)
}
