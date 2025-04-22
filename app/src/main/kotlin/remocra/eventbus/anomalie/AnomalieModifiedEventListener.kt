package remocra.eventbus.anomalie

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import remocra.db.AnomalieRepository
import remocra.db.PeiRepository
import remocra.eventbus.EventListener
import remocra.usecase.pei.PeiUseCase
import remocra.usecase.pei.UpdatePeiUseCase

class AnomalieModifiedEventListener @Inject constructor() :
    EventListener<AnomalieModifiedEvent> {
    @Inject
    lateinit var anomalieRepository: AnomalieRepository

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var updatePeiUseCase: UpdatePeiUseCase

    @Inject
    lateinit var peiUseCase: PeiUseCase

    @Subscribe
    override fun onEvent(event: AnomalieModifiedEvent) {
        // Pour chaque PEI associée à cette anomalie, on relance le calcul de dispo
        anomalieRepository.getPeiIds(event.anomalieId).forEach { peiId ->
            peiUseCase.getInfoPei(peiId).let { peiData ->
                updatePeiUseCase.execute(event.userInfo, peiData)
            }
        }
    }
}
