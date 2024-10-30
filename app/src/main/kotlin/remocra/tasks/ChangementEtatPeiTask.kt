package remocra.tasks

import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.PeiData
import remocra.db.JobRepository
import remocra.db.TracabiliteRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.eventbus.notification.NotificationEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import java.time.ZonedDateTime
import java.util.*

/**
 * Tâche permettant
 * * d'identifier, depuis la dernière exécution, tous les PEI ayant changé d'état de disponibilité
 * * de notifier les bons interlocuteurs en s'appuyant sur le paramétrage
 */
class ChangementEtatPeiTask : SchedulableTask<ChangementEtatPeiTaskParameter, ChangementEtatPeiJobResult>() {

    @Inject
    lateinit var tracabiliteRepository: TracabiliteRepository

    @Inject
    lateinit var jobRepository: JobRepository

    override fun getType(): TypeTask {
        return TypeTask.NOTIFIER_CHANGEMENTS_ETAT
    }

    override fun getTaskParametersClass(): Class<ChangementEtatPeiTaskParameter> {
        return ChangementEtatPeiTaskParameter::class.java
    }

    override fun notifySpecific(executionResults: ChangementEtatPeiJobResult?, notificationRaw: NotificationRaw) {
        if (this.jobDb?.jobId == null) {
            return
        } else if (executionResults?.listPei == null) {
            return
        }

        /** Récupération des destinataires */
        val contactRole = GlobalConstants.CHANGEMENT_ETAT_PEI
        val mapPeiIdParDestinataire =
            taskUseCase.getDestinataireByListPei(
                listPeiId = executionResults.listPei.map { it.pojoId },
                contactRole = contactRole,
                typeDestinataire = notificationRaw.typeDestinataire,
            )

        /** Jointure entre les différents destinataires et les objets de traca qui leurs sont liés */
        val mapJoinDestinataireEventTracaOnPeiId: MutableMap<Destinataire, List<TracabiliteEvent<PeiData>>> = mutableMapOf()
        mapPeiIdParDestinataire.forEach {
                (destinataire, listPeiId) ->
            val currentDestinatairePei: MutableList<TracabiliteEvent<PeiData>> = mutableListOf()
            listPeiId.forEach {
                    peiId ->
                executionResults.listPei.find { it.pojoId == peiId }?.let { currentDestinatairePei.add(it) }
            }
            if (currentDestinatairePei.isNotEmpty()) {
                mapJoinDestinataireEventTracaOnPeiId[destinataire] = currentDestinatairePei
            }
        }

        /** Remplacement des PlaceHolders */
        val initialObjet = notificationRaw.objet
        val initialCorps = notificationRaw.corps

        val objetsANotifier: MutableList<NotificationMail> = mutableListOf()
        // Placeholders degré 1 : entête/footer (genre logo du sdis et autres)
        val genericCorps = initialCorps.replace(
            "#FOOTER#",
            "En cas d'incompréhension de ce message, merci de prendre contact avec votre SDIS.",
        )

        // Placeholders degré 2 : chaque destinataire permet de paramétrer différement le mail
        // Au niveau du bonjour, ou du contenu personnalisé
        mapJoinDestinataireEventTracaOnPeiId.forEach { (destinataire, listeTracaEvent) ->
            val listePeiDispo = listeTracaEvent.filter { it.pojo.peiDisponibiliteTerrestre == Disponibilite.DISPONIBLE }.map { it.pojo.peiNumeroComplet }.joinToString(", ")
            val listePeiIndispo = listeTracaEvent.filter { it.pojo.peiDisponibiliteTerrestre == Disponibilite.INDISPONIBLE }.map { it.pojo.peiNumeroComplet }.joinToString(", ")
            val listePeiNonConforme = listeTracaEvent.filter { it.pojo.peiDisponibiliteTerrestre == Disponibilite.NON_CONFORME }.map { it.pojo.peiNumeroComplet }.joinToString(", ")

            var corps = genericCorps

            corps = remplaceIfNotEmpty(corps, "#resultsDispo#", "Les PEIs suivants sont passés à l'état Disponible #liste#.\n", listePeiDispo)
            corps = remplaceIfNotEmpty(corps, "#resultsIndispo#", "Les PEI suivants sont passés à l'état Indisponible #liste#.\n", listePeiIndispo)
            corps = remplaceIfNotEmpty(corps, "#resultsNonConforme#", "Les PEI suivants sont passés à l'état Disponible #liste#.\n", listePeiNonConforme)

            objetsANotifier.add(
                NotificationMail(
                    destinataires = setOf(destinataire.destinataireEmail),
                    objet = initialObjet,
                    corps = corps,
                ),
            )
        }

        /** Envoi des notifications dans l'EventBus */
        objetsANotifier.forEach { obj ->
            eventBus.post(NotificationEvent(obj, this.jobDb!!.jobId))
        }
    }

    private fun remplaceIfNotEmpty(corps: String, placeholder: String, message: String, liste: String): String {
        return if (liste.isNotEmpty()) {
            corps.replace(placeholder, message.replace("#liste#", liste))
        } else {
            corps.replace(placeholder, "")
        }
    }

    override fun checkParameters(parameters: ChangementEtatPeiTaskParameter?) {
        // Pas de parametres pour cette tâche
    }

    override fun execute(parameters: ChangementEtatPeiTaskParameter?, userInfo: UserInfo): ChangementEtatPeiJobResult? {
        /** TODO: La moulinette doit permettre de notifier au choix, les PEI passant à l'état Dispo, Indispo et Non conforme
         *  Au choix signifit que ca peut etre les trois, deux ou un seul cas
         */

        // Récupération des événements de tracabilité depuis la dernière éxécution de la tâche
        val listeEventTraca = tracabiliteRepository.getTracabilitePeiSince(getLastExecDate(this.getType(), this.jobDb!!.jobId))

        // Si aucun evenement de traca, on s'arrete là
        if (listeEventTraca.isEmpty()) {
            return null
        }

        // Désérialisation des evenements de tracabilité
        val listTracaObjects: List<TracabiliteEvent<PeiData>> =
            listeEventTraca.map { element ->
                TracabiliteEvent(
                    pojo = objectMapper.readValue<PeiData>(element.tracabiliteObjetData.toString()),
                    pojoId = element.tracabiliteObjetId,
                    typeOperation = element.tracabiliteTypeOperation,
                    typeObjet = element.tracabiliteTypeObjet,
                    auteurTracabilite = objectMapper.readValue<AuteurTracabiliteData>(element.tracabiliteAuteurData.toString()),
                    date = element.tracabiliteDate,
                )
            }

        // Pour chaque élément de traca
        val listPei: MutableList<TracabiliteEvent<PeiData>> = mutableListOf()
        listTracaObjects.forEach { tracaObject ->
            // Récupération de l'événement précédent s'il existe
            val previousTracaEvent = tracabiliteRepository.getPreviousPeiTracaEvent(tracaObject.pojoId, tracaObject.date)
            if (previousTracaEvent != null) {
                val previousTracaObject = TracabiliteEvent(
                    pojo = objectMapper.readValue<PeiData>(previousTracaEvent.tracabiliteObjetData.toString()),
                    pojoId = previousTracaEvent.tracabiliteObjetId,
                    typeOperation = previousTracaEvent.tracabiliteTypeOperation,
                    typeObjet = previousTracaEvent.tracabiliteTypeObjet,
                    auteurTracabilite = objectMapper.readValue<AuteurTracabiliteData>(previousTracaEvent.tracabiliteAuteurData.toString()),
                    date = previousTracaEvent.tracabiliteDate,
                )
                // Comparaison de la valeur DisponibilitéTerrestre entre l'état traca actuel et le précédent
                if (previousTracaObject.pojo.peiDisponibiliteTerrestre != tracaObject.pojo.peiDisponibiliteTerrestre) {
                    listPei.addFirst(tracaObject)
                }
            }
        }
        logManager.info("exécution de la tâche !")
        return ChangementEtatPeiJobResult(listPei = listPei)
    }

    private fun getLastExecDate(taskType: TypeTask, jobId: UUID): ZonedDateTime {
        // Remonte la date de dernière exécution, sinon le moment qu'il était il y a 5mn
        val test = jobRepository.getPreviousExecution(taskType, jobId)?.jobDateDebut
        return jobRepository.getPreviousExecution(taskType, jobId)?.jobDateDebut ?: dateUtils.now().minusMinutes(5)
    }
}

class ChangementEtatPeiTaskParameter(
    override val notification: NotificationMail?,
) : SchedulableTaskParameters(notification)

class ChangementEtatPeiJobResult(
    val listPei: List<TracabiliteEvent<PeiData>>?,
) : SchedulableTaskResults()

data class Destinataire(
    val destinataireId: UUID?,
    val destinataireCivilite: TypeCivilite?,
    val destinataireFonction: String?,
    val destinataireNom: String?,
    val destinatairePrenom: String?,
    val destinataireEmail: String,
)
