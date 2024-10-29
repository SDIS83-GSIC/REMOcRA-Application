package remocra.tasks

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.IndisponibiliteTemporaire
import remocra.eventbus.notification.NotificationEvent

class NotifAvantDebutIndispoTempTask : SchedulableTask<NotifAvantDebutIndispoTempTaskParameter, NotifAvantDebutIndispoTempJobResult>() {

    @Inject lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

    private val identificationJob = this.getType().toString()

    override fun execute(parameters: NotifAvantDebutIndispoTempTaskParameter?, userInfo: UserInfo): NotifAvantDebutIndispoTempJobResult {
        logManager.info("[$identificationJob] Lancement de l'exécution du job")
        val indispoTempANotifier = indisponibiliteTemporaireRepository.getITToNotifyDebut(parameters!!.deltaMinuteNotificationDebut)
        var peiIdFromItANotifier: List<IndisponibiliteTemporaireRepository.PeiForItMoulinette> = listOf()
        if (indispoTempANotifier.isNotEmpty()) {
            peiIdFromItANotifier = indisponibiliteTemporaireRepository.getPeiFromListIt(indispoTempANotifier.map { it.indisponibiliteTemporaireId })
        }
        logManager.info("[$identificationJob] Fin de l'exécution du job")
        return NotifAvantDebutIndispoTempJobResult(
            listITNotifierDebut = indispoTempANotifier,
            listPeiITNotifierDebut = peiIdFromItANotifier,
        )
    }

    override fun notifySpecific(executionResults: NotifAvantDebutIndispoTempJobResult?, notificationRaw: NotificationRaw) {
        logManager.info("[$identificationJob] Début de la notification du job")
        if (this.jobDb?.jobId == null) {
            logManager.error("[$identificationJob] Le Job n'a pas d'identifiant unique")
            return
        } else if (executionResults == null) {
            logManager.info("[$identificationJob] Les résultats d'exécution sont nuls ou vides")
            return
        }

        logManager.info("[$identificationJob] Liste des IT dont le début est à notifier : ${executionResults.listITNotifierDebut}")

        /** Récupération des destinataires */
        val contactRole = GlobalConstants.IT_NOTIF_AVANT_DEBUT
        val mapPeiIdParDestinataire =
            taskUseCase.getDestinataireByListPei(
                listPeiId = executionResults.listPeiITNotifierDebut.map { it.peiId },
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
                executionResults.listPeiITNotifierDebut.find { it.peiId == peiId }?.let { currentDestinatairePei.add(it) }
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
            val specificCorps = genericCorps.replace("#LISTE_PEI_DEBUT_INDISPO#", formatedListPei)

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

        executionResults.listITNotifierDebut.forEach {
            logManager.info("[$identificationJob] Mise à jour du flag 'indisponibilite_temporaire_notification_debut' pour l'IT ${it.indisponibiliteTemporaireId} ")
            indisponibiliteTemporaireRepository.setNotificationDebut(dateNotification = dateUtils.now())
        }
        logManager.info("[$identificationJob] Fin de la notification du job")
    }

    override fun checkParameters(parameters: NotifAvantDebutIndispoTempTaskParameter?) {
        if (parameters == null) {
            logManager.error("Aucun paramètre fourni")
            throw IllegalArgumentException("Aucun paramètre fourni")
        }
    }

    override fun getType(): TypeTask {
        return TypeTask.IT_NOTIF_AVANT_DEBUT
    }

    override fun getTaskParametersClass(): Class<NotifAvantDebutIndispoTempTaskParameter> {
        return NotifAvantDebutIndispoTempTaskParameter::class.java
    }
}

class NotifAvantDebutIndispoTempTaskParameter(
    override val notification: NotificationMail?,
    val deltaMinuteNotificationDebut: Long,
) : SchedulableTaskParameters(notification)

class NotifAvantDebutIndispoTempJobResult(
    val listITNotifierDebut: List<IndisponibiliteTemporaire>,
    val listPeiITNotifierDebut: List<IndisponibiliteTemporaireRepository.PeiForItMoulinette>,
) : SchedulableTaskResults()
