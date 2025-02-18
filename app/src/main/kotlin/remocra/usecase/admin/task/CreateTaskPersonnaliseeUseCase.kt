package remocra.usecase.admin.task

import jakarta.inject.Inject
import org.apache.logging.log4j.core.util.CronExpression
import remocra.auth.WrappedUserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.TaskPersonnaliseeInputData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.TaskRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.Task
import remocra.eventbus.parametres.ParametresModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateTaskPersonnaliseeUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskPersonnaliseeUtils: TaskPersonnaliseeUtils,
) : AbstractCUDUseCase<TaskPersonnaliseeInputData>(typeOperation = TypeOperation.INSERT) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(Droit.ADMIN_PARAM_TRAITEMENTS)) {
            throw RemocraResponseException(ErrorType.ADMIN_TASK_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: TaskPersonnaliseeInputData) {
        // La méthode isValidExpression retourne un booléan indiquant si l'expression est conforme ou non
        // Donc si false, l'expression est invalide, on remonte l'info dans le Front
        if (!CronExpression.isValidExpression(element.taskPlanification)) {
            throw(IllegalArgumentException("La CronExpression fournie est invalide : ${element.taskPlanification}"))
        }
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: TaskPersonnaliseeInputData,
    ): TaskPersonnaliseeInputData {
        // On sauvegarde en base l'élément mis à jour
        taskRepository.insert(
            Task(
                taskId = element.taskId,
                taskType = TypeTask.PERSONNALISE,
                taskActif = element.taskActif,
                taskPlanification = element.taskPlanification,
                taskExecManuelle = false,
                taskParametres = element.taskParametres,
                taskNotification = null,
            ),
        )

        taskPersonnaliseeUtils.saveTask(element)

        return element.copy(zip = null)
    }

    data class ConfigApacheHop(
        val name: String,
        val filename: String,
        val enabled: Boolean = true,
    )

    override fun postEvent(element: TaskPersonnaliseeInputData, userInfo: WrappedUserInfo) {
        // Ajout Traçabilité
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.taskId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.TASK,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId!!,
                    nom = userInfo.nom!!,
                    prenom = userInfo.prenom,
                    email = userInfo.userInfo!!.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        )
        // Invalidation du cache pour prendre en compte les changements
        eventBus.post(
            ParametresModifiedEvent(),
        )
    }
}
