package remocra.tasks

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.IndisponibiliteTemporaire
import remocra.eventbus.notification.NotificationEvent

class NotifAvantFinIndispoTempTask : SchedulableTask<NotifAvantFinIndispoTempTaskParameter, NotifAvantFinIndispoTempJobResult>() {

    @Inject lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    private val identificationJob = this.getType().toString()

    override fun execute(parameters: NotifAvantFinIndispoTempTaskParameter?, userInfo: UserInfo): NotifAvantFinIndispoTempJobResult {
        logManager.info("[$identificationJob] Lancement de l'exécution du job")
        val indispoTempANotifier = indisponibiliteTemporaireRepository.getITToNotifyFin(parameters!!.deltaMinuteNotificationFin)
        var peiIdFromItANotifier: List<IndisponibiliteTemporaireRepository.PeiForItMoulinette> = listOf()
        if (indispoTempANotifier.isNotEmpty()) {
            peiIdFromItANotifier = indisponibiliteTemporaireRepository.getPeiFromListIt(indispoTempANotifier.map { it.indisponibiliteTemporaireId })
        }
        logManager.info("[$identificationJob] Fin de l'exécution du job")
        return NotifAvantFinIndispoTempJobResult(
            listITNotifierFin = indispoTempANotifier,
            listPeiITNotifierFin = peiIdFromItANotifier,
        )
    }

    override fun notifySpecific(executionResults: NotifAvantFinIndispoTempJobResult?, notificationRaw: NotificationRaw) {
        logManager.info("[$identificationJob] Début de la notification du job")
        if (this.jobDb?.jobId == null) {
            logManager.error("[$identificationJob] Le Job n'a pas d'identifiant unique")
            return
        } else if (executionResults == null) {
            logManager.info("[$identificationJob] Les résultats d'exécution sont nuls ou vides")
            return
        }

        logManager.info("[$identificationJob] Liste des IT dont la fin est à notifier : ${executionResults.listITNotifierFin}")

        /** Récupération des destinataires */
        val contactRole = GlobalConstants.IT_NOTIF_AVANT_FIN
        val mapPeiIdParDestinataire =
            taskUseCase.getDestinataireByListPei(
                listPeiId = executionResults.listPeiITNotifierFin.map { it.peiId },
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
                executionResults.listPeiITNotifierFin.find { it.peiId == peiId }?.let { currentDestinatairePei.add(it) }
            }
            if (currentDestinatairePei.isNotEmpty()) {
                mapJoinDestinataireObjectOnPeiId[destinataire] = currentDestinatairePei
            }
        }

        /** Remplacement des PlaceHolders */
        val objetsANotifier: MutableList<NotificationMail> = mutableListOf()
        // Placeholders degré 1
        val genericCorps = notificationRaw.corps.replace(
            "#FOOTER#",
            "En cas d'incompréhension de ce message, merci de prendre contact avec votre SDIS.\n\n",
        )
        // Placeholders degré 2
        mapJoinDestinataireObjectOnPeiId.forEach { (destinataire, listeObjects) ->
            val formatedListPei = listeObjects.joinToString("\n") { it.peiNumeroComplet }
            var specificCorps = genericCorps.replace("#LISTE_PEI_FIN_INDISPO#", formatedListPei)

            objetsANotifier.add(
                NotificationMail(
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

        executionResults.listITNotifierFin.forEach {
            logManager.info("[$identificationJob] Mise à jour du flag 'indisponibilite_temporaire_notification_fin' pour l'IT ${it.indisponibiliteTemporaireId} ")
            indisponibiliteTemporaireRepository.setNotificationFin(dateNotification = dateUtils.now())
        }
        logManager.info("[$identificationJob] Fin de la notification du job")
    }

    override fun checkParameters(parameters: NotifAvantFinIndispoTempTaskParameter?) {
        if (parameters == null) {
            logManager.error("Aucun paramètre fourni")
        }
    }

    override fun getType(): TypeTask {
        return TypeTask.IT_NOTIF_AVANT_FIN
    }

    override fun getTaskParametersClass(): Class<NotifAvantFinIndispoTempTaskParameter> {
        return NotifAvantFinIndispoTempTaskParameter::class.java
    }
}

class NotifAvantFinIndispoTempTaskParameter(
    override val notification: NotificationMail?,
    val deltaMinuteNotificationFin: Long,
) : SchedulableTaskParameters(notification)

class NotifAvantFinIndispoTempJobResult(
    val listITNotifierFin: List<IndisponibiliteTemporaire>,
    val listPeiITNotifierFin: List<IndisponibiliteTemporaireRepository.PeiForItMoulinette>,
) : SchedulableTaskResults()
