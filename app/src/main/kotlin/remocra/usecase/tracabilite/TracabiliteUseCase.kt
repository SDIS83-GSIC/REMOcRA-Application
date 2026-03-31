package remocra.usecase.tracabilite

import jakarta.inject.Inject
import remocra.data.tracabilite.Search
import remocra.db.TracabiliteRepository
import remocra.db.jooq.historique.tables.pojos.Tracabilite
import remocra.usecase.AbstractUseCase

class TracabiliteUseCase
@Inject
constructor(
    private val tracabiliteRepository: TracabiliteRepository,
) :
    AbstractUseCase() {

    fun search(search: Search): List<Tracabilite> = tracabiliteRepository.searchTracabilite(search)
}
