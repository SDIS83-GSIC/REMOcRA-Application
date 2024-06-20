package remocra.data.enums

import com.fasterxml.jackson.annotation.JsonProperty
import remocra.db.jooq.remocra.enums.TypeTask

/**
 * Enum des différentes tâches qui peuvent être ordonnancées. <br />
 * Cet enum est un sous-ensemble de [remocra.db.jooq.remocra.enums.TypeTask] car toutes les tâches n'ont pas pour vocation à être planifiables.
 */
enum class SchedulableTaskType(val typeTask: TypeTask) {
    @JsonProperty("scheduleChangementEtatPei")
    CHANGEMENT_ETAT_PEI(TypeTask.NOTIFIER_CHANGEMENTS_ETAT),
}
