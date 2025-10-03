package remocra.usecase.risque

import jakarta.inject.Inject
import remocra.db.RisqueExpressRepository
import remocra.db.jooq.remocra.tables.pojos.RisqueExpress
import remocra.usecase.AbstractUseCase

class GetRisquesExpressUseCase : AbstractUseCase() {
    @Inject
    lateinit var risqueExpressRepository: RisqueExpressRepository
    fun getRisquesExpress(): Collection<RisqueExpress> = risqueExpressRepository.getRisquesExpress()
}
