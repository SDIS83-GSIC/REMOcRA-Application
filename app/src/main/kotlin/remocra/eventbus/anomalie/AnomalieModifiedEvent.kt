package remocra.eventbus.anomalie

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.eventbus.Event
import java.util.UUID

/**
 * Event déclenché lors de la modification d'une Anomalie, afin de recalculer en tâche de fond la disponibilité des PEI qui la portent
 */
class AnomalieModifiedEvent @Inject constructor(
    val anomalieId: UUID,
    val userInfo: WrappedUserInfo,
) : Event
