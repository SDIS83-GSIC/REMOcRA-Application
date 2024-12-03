package remocra.usecase.admin.task

import jakarta.inject.Inject
import org.apache.logging.log4j.core.util.CronExpression
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.TaskInputData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.TaskRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Task
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) : AbstractCUDUseCase<TaskInputData>(typeOperation = TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_PARAM_TRAITEMENTS)) {
            throw RemocraResponseException(ErrorType.ADMIN_TASK_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: TaskInputData) {
        // La méthode isValidExpression retourne un booléan indiquant si l'expression est conforme ou non
        // Donc si false, l'expression est invalide, on remonte l'info dans le Front
        if (!CronExpression.isValidExpression(element.taskPlanification)) {
            throw(IllegalArgumentException("La CronExpression fournie est invalide : ${element.taskPlanification}"))
        }
    }

    override fun execute(userInfo: UserInfo?, element: TaskInputData): TaskInputData {
        taskRepository.update(
            Task(
                taskId = element.taskId,
                taskType = element.taskType,
                taskActif = element.taskActif,
                taskPlanification = element.taskPlanification,
                taskExecManuelle = element.taskExecManuelle,
                taskParametres = element.taskParametres,
                taskNotification = element.taskNotification,
            ),
        )
        return element
    }

    override fun postEvent(element: TaskInputData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.taskId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.TASK,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        )
    }
}
