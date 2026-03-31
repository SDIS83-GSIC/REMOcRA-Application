package remocra.usecase.risque

import jakarta.inject.Inject
import remocra.db.RisqueExpressRepository
import remocra.db.jooq.remocra.tables.pojos.RisqueExpress
import remocra.usecase.AbstractUseCase

class GetRisquesExpressUseCase
@Inject
constructor(
    private val risqueExpressRepository: RisqueExpressRepository,
) :
    AbstractUseCase() {
    fun getRisquesExpress(): Collection<RisqueExpress> = risqueExpressRepository.getRisquesExpress()
}
