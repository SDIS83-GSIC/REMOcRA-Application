package remocra.usecase.admin.task

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import org.apache.logging.log4j.core.util.CronExpression
import remocra.auth.WrappedUserInfo
import remocra.data.TaskInputData
import remocra.data.enums.ErrorType
import remocra.db.TaskRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.Task
import remocra.eventbus.parametres.ParametresModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.tasks.SynchroUtilisateurTaskParameters
import remocra.tasks.SynchronisationSIGTaskParameter
import remocra.tasks.TypeSynchronisation
import remocra.usecase.AbstractCUDUseCase
import remocra.utils.RequestUtils

class UpdateTaskUseCase @Inject constructor(private val taskRepository: TaskRepository, private val objectMapper: ObjectMapper, private val requestUtils: RequestUtils) : AbstractCUDUseCase<TaskInputData>(typeOperation = TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_PARAM_TRAITEMENTS)) {
            throw RemocraResponseException(ErrorType.ADMIN_TASK_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: TaskInputData) {
        // La méthode isValidExpression retourne un booléan indiquant si l'expression est conforme ou non
        // Donc si false, l'expression est invalide, on remonte l'info dans le Front
        if (!CronExpression.isValidExpression(element.taskPlanification)) {
            throw RemocraResponseException(ErrorType.ADMIN_TASK_SYNCHRO_CRON_INVALIDE, "${element.taskPlanification}")
        }
        if (element.taskType == TypeTask.SYNCHRONISATION_SIG) {
            val taskParameters =
                objectMapper.readValue<SynchronisationSIGTaskParameter>(element.taskParametres.toString())
            taskParameters.listeTableASynchroniser.forEach { tableASynchroniser ->
                if (tableASynchroniser.typeSynchronisation != TypeSynchronisation.STOCKAGE_SIMPLE) {
                    if (tableASynchroniser.scriptCreationVue.isNullOrBlank()) {
                        throw RemocraResponseException(ErrorType.ADMIN_TASK_SYNCHRO_SIG_SCRIPT_VUE, "${tableASynchroniser.typeSynchronisation}")
                    }
                    requestUtils.validateQueryWithCreate(tableASynchroniser.scriptCreationVue)
                }
            }
        }

        if (element.taskType == TypeTask.SYNCHRO_UTILISATEUR) {
            val taskParameters = objectMapper.readValue<SynchroUtilisateurTaskParameters>(element.taskParametres.toString())

            if (taskParameters.accepteUserSansEmail && taskParameters.emailParDefaut == null) {
                throw RemocraResponseException(ErrorType.ADMIN_TASK_SYNCHRO_UTILSATEUR_EMAIL)
            }
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: TaskInputData): TaskInputData {
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

    override fun postEvent(element: TaskInputData, userInfo: WrappedUserInfo) {
        // Ajout Traçabilité
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.taskId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.TASK,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
        // Invalidation du cache pour prendre en compte les changements
        eventBus.post(
            ParametresModifiedEvent(),
        )
    }
}
