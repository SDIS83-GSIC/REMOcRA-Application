package remocra.tasks

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.NotificationMailData
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.usecase.pei.UpdatePeiUseCase
import java.util.UUID

class BasculeAutoIndispoTempTask : SchedulableTask<BasculeAutoIndispoTempTaskParameter, SchedulableTaskResults>() {

    @Inject lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    @Inject lateinit var peiRepository: PeiRepository

    @Inject lateinit var pibiRepository: PibiRepository

    @Inject lateinit var penaRepository: PenaRepository

    @Inject lateinit var updatePeiUseCase: UpdatePeiUseCase

    private val identificationJob = this.getType().toString()

    override fun execute(parameters: BasculeAutoIndispoTempTaskParameter?, userInfo: UserInfo): SchedulableTaskResults? {
        logManager.info("[$identificationJob] Lancement de l'exécution du job")
        val listIdItPourBasculeAutoDebut: List<UUID> = indisponibiliteTemporaireRepository.getItEnCoursToCalculIndispo()
        if (listIdItPourBasculeAutoDebut.isNotEmpty()) {
            logManager.info("[$identificationJob] Les IT suivantes viennent de commencer : $listIdItPourBasculeAutoDebut")
            listIdItPourBasculeAutoDebut.forEach {
                logManager.info("[$identificationJob] Récupération des PEIs liés à l'IT $it")
                val listPeiId = indisponibiliteTemporaireRepository.getAllPeiIdFromItId(it)
                logManager.info("[$identificationJob] Les PEIs liés sont : $listPeiId")
                listPeiId.forEach { currentPeiId ->
                    if (peiRepository.getTypePei(currentPeiId) == TypePei.PIBI) {
                        updatePeiUseCase.execute(userInfo, pibiRepository.getInfoPibi(currentPeiId), transactionManager)
                    } else {
                        updatePeiUseCase.execute(userInfo, penaRepository.getInfoPena(currentPeiId), transactionManager)
                    }
                }
                logManager.info("[$identificationJob] Mise à jour du flag BASCULE_DEBUT pour l'IT $it")
                indisponibiliteTemporaireRepository.setBasculeDebutTrue(it)
            }
        }

        val listIdItPourBasculeAutoFin: List<UUID> = indisponibiliteTemporaireRepository.getItTermineeToCalculIndispo()
        if (listIdItPourBasculeAutoFin.isNotEmpty()) {
            logManager.info("[$identificationJob] Les IT suivantes viennent de se terminer : $listIdItPourBasculeAutoDebut")
            listIdItPourBasculeAutoFin.forEach {
                logManager.info("[$identificationJob] Récupération des PEIs liés à l'IT $it")
                val listPeiId = indisponibiliteTemporaireRepository.getAllPeiIdFromItId(it)
                logManager.info("[$identificationJob] Les PEIs liés sont : $listPeiId")
                listPeiId.forEach { currentPeiId ->
                    if (peiRepository.getTypePei(currentPeiId) == TypePei.PIBI) {
                        updatePeiUseCase.execute(userInfo, pibiRepository.getInfoPibi(currentPeiId), transactionManager)
                    } else {
                        updatePeiUseCase.execute(userInfo, penaRepository.getInfoPena(currentPeiId), transactionManager)
                    }
                }
                logManager.info("[$identificationJob] Mise à jour du flag BASCULE_FIN pour l'IT $it")
                indisponibiliteTemporaireRepository.setBasculeFinTrue(it)
            }
        }
        logManager.info("[$identificationJob] Fin l'exécution du job")
        return null
    }

    override fun notifySpecific(executionResults: SchedulableTaskResults?, notificationRaw: NotificationRaw) {
        // Pas de notification ici, se référer aux task TypeTask.IT_NOTIF_AVANT_DEBUT, IT_NOTIF_AVANT_FIN, IT_NOTIF_RESTE_INDISPO
    }

    override fun checkParameters(parameters: BasculeAutoIndispoTempTaskParameter?) {
        // Pas de paramètre pour cette task
    }

    override fun getType(): TypeTask {
        return TypeTask.BASCULE_AUTO_INDISPO_TEMP
    }

    override fun getTaskParametersClass(): Class<BasculeAutoIndispoTempTaskParameter> {
        return BasculeAutoIndispoTempTaskParameter::class.java
    }
}

class BasculeAutoIndispoTempTaskParameter(
    override val notification: NotificationMailData?,
) : SchedulableTaskParameters(notification)
