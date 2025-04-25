package remocra.tasks

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.NotificationMailData
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.IndisponibiliteTemporaire
import remocra.eventbus.notification.NotificationEvent

class NotifResteIndispoIndispoTempTask : SchedulableTask<NotifResteIndispoIndispoTempTaskParameter, NotifResteIndispoIndispoTempJobResult>() {

    @Inject lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    private val identificationJob = this.getType().toString()

    override fun execute(parameters: NotifResteIndispoIndispoTempTaskParameter?, userInfo: WrappedUserInfo): NotifResteIndispoIndispoTempJobResult {
        logManager.info("[$identificationJob] Lancement de l'exécution du job")
        val indispoTempANotifier = indisponibiliteTemporaireRepository.getAllResteIndispoNotNotified()
        var peiIdFromItANotifier: List<IndisponibiliteTemporaireRepository.PeiForItMoulinette> = listOf()
        if (indispoTempANotifier.isNotEmpty()) {
            peiIdFromItANotifier = indisponibiliteTemporaireRepository.getPeiResteIndispoFromItId(indispoTempANotifier.map { it.indisponibiliteTemporaireId })
        }
        logManager.info("[$identificationJob] Fin de l'exécution du job")
        return NotifResteIndispoIndispoTempJobResult(
            listITNotifierPeiResteIndispo = indispoTempANotifier,
            listPeiITNotifierResteIndispo = peiIdFromItANotifier,
        )
    }

    override fun notifySpecific(executionResults: NotifResteIndispoIndispoTempJobResult?, notificationRaw: NotificationRaw) {
        logManager.info("[$identificationJob] Début de la notification du job")
        if (this.jobDb?.jobId == null) {
            logManager.error("[$identificationJob] Le Job n'a pas d'identifiant unique")
            return
        } else if (executionResults == null) {
            logManager.info("[$identificationJob] Les résultats d'exécution sont nuls ou vides")
            return
        }

        logManager.info("[$identificationJob] Liste des IT dont certains PEI sont resté indispo après la fin : ${executionResults.listITNotifierPeiResteIndispo}")

        /** Récupération des destinataires */
        val contactRole = GlobalConstants.IT_NOTIF_RESTE_INDISPO
        val mapPeiIdParDestinataire =
            taskUseCase.getDestinataireByListPei(
                listPeiId = executionResults.listPeiITNotifierResteIndispo.map { it.peiId },
                contactRole = contactRole,
                typeDestinataire = notificationRaw.typeDestinataire,
            )

        /** Jointure entre les différents destinataires et les PEI qui leurs sont liés */
        val mapJoinDestinataireObjectOnPeiId: MutableMap<Destinataire, List<IndisponibiliteTemporaireRepository.PeiForItMoulinette>> = mutableMapOf()
        mapPeiIdParDestinataire.forEach {
                (destinataire, listPeiId) ->
            val currentDestinatairePei: MutableList<IndisponibiliteTemporaireRepository.PeiForItMoulinette> = mutableListOf()
            listPeiId.forEach {
                    peiId ->
                executionResults.listPeiITNotifierResteIndispo.find { it.peiId == peiId }?.let { currentDestinatairePei.add(it) }
            }
            if (currentDestinatairePei.isNotEmpty()) {
                mapJoinDestinataireObjectOnPeiId[destinataire] = currentDestinatairePei
            }
        }

        /** Remplacement des PlaceHolders */
        val objetsANotifier: MutableList<NotificationMailData> = mutableListOf()
        // Placeholders degré 1
        val genericCorps = notificationRaw.corps.replace(
            "#FOOTER#",
            "En cas d'incompréhension de ce message, merci de prendre contact avec votre SDIS.\n\n",
        )
        // Placeholders degré 2
        mapJoinDestinataireObjectOnPeiId.forEach { (destinataire, listeObjects) ->
            val formatedListPei = listeObjects.joinToString("\n") { it.peiNumeroComplet }
            val specificCorps = genericCorps.replace("#LISTE_PEI_RESTE_INDISPO#", formatedListPei)

            objetsANotifier.add(
                NotificationMailData(
                    destinataires = setOf(destinataire.destinataireEmail),
                    objet = notificationRaw.objet,
                    corps = specificCorps,
                ),
            )
        }

        logManager.info("[$identificationJob] Envoi des notifications")
        /** Envoi des notifications dans l'EventBus */
        objetsANotifier.forEach { obj ->
            eventBus.post(NotificationEvent(obj, this.jobDb!!.jobId))
        }

        executionResults.listITNotifierPeiResteIndispo.forEach {
            logManager.info("[$identificationJob] Mise à jour du flag 'indisponibilite_temporaire_notification_reste_indispo' pour l'IT ${it.indisponibiliteTemporaireId} ")
            indisponibiliteTemporaireRepository.setNotificationResteIndispo(dateNotification = dateUtils.now())
        }
        logManager.info("[$identificationJob] Fin de la notification du job")
    }

    override fun checkParameters(parameters: NotifResteIndispoIndispoTempTaskParameter?) {
        // Pas de paramètre pour cette task
    }

    override fun getType(): TypeTask {
        return TypeTask.IT_NOTIF_RESTE_INDISPO
    }

    override fun getTaskParametersClass(): Class<NotifResteIndispoIndispoTempTaskParameter> {
        return NotifResteIndispoIndispoTempTaskParameter::class.java
    }
}

class NotifResteIndispoIndispoTempTaskParameter(
    override val notification: NotificationMailData?,
) : SchedulableTaskParameters(notification)

class NotifResteIndispoIndispoTempJobResult(
    val listITNotifierPeiResteIndispo: List<IndisponibiliteTemporaire>,
    val listPeiITNotifierResteIndispo: List<IndisponibiliteTemporaireRepository.PeiForItMoulinette>,
) : SchedulableTaskResults()
