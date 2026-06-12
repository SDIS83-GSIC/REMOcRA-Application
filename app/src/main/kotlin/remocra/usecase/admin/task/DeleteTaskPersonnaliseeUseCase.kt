package remocra.usecase.admin.task

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.TaskPersonnaliseeInputData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.JobRepository
import remocra.db.LogLineRepository
import remocra.db.TaskRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.parametres.ParametresModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.tasks.ApacheHopTask.ApacheHopParametre
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class DeleteTaskPersonnaliseeUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val jobRepository: JobRepository,
    private val logLineRepository: LogLineRepository,
    private val documentUtils: DocumentUtils,
    private val objectMapper: ObjectMapper,
) : AbstractCUDUseCase<TaskPersonnaliseeInputData>(typeOperation = TypeOperation.DELETE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(Droit.ADMIN_PARAM_TRAITEMENTS)) {
            throw RemocraResponseException(ErrorType.ADMIN_TASK_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: TaskPersonnaliseeInputData) {
        // pas de contraintes
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: TaskPersonnaliseeInputData,
    ): TaskPersonnaliseeInputData {
        // On supprime les documents physiques
        val task = objectMapper.readValue(element.taskParametres.toString(), ApacheHopParametre::class.java)
        documentUtils.deleteDirectory(GlobalConstants.DOSSIER_APACHE_HOP_TASK.resolve(task.taskCode))
        documentUtils.deleteFileIfExists("${task.taskCode}.json", GlobalConstants.DOSSIER_APACHE_HOP_CONFIG)

        // Puis on delete en base les log_ligne et les jobs
        val jobsId = jobRepository.getJobsId(element.taskId)
        logLineRepository.deleteByJobsId(jobsId)
        jobRepository.deleteByIdJob(jobsId)

        taskRepository.deleteTaskPersonnalisee(element.taskId)

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
