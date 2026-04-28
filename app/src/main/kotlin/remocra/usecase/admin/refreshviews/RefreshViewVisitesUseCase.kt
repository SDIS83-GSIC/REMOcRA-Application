package remocra.usecase.admin.refreshviews

import jakarta.inject.Inject
import remocra.db.MaterializedViewRepository
import remocra.usecase.AbstractUseCase

class RefreshViewVisitesUseCase @Inject constructor(private val materializedViewRepository: MaterializedViewRepository) : AbstractUseCase() {
    fun execute() {
        materializedViewRepository.refreshViewVisites()
    }
}
