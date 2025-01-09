package remocra.usecase.tracabilite

import com.google.inject.Inject
import remocra.data.tracabilite.Search
import remocra.db.TracabiliteRepository
import remocra.db.jooq.historique.tables.pojos.Tracabilite
import remocra.usecase.AbstractUseCase

class TracabiliteUseCase : AbstractUseCase() {
    @Inject
    lateinit var tracabiliteRepository: TracabiliteRepository

    fun search(search: Search): List<Tracabilite> = tracabiliteRepository.searchTracabilite(search)
}
