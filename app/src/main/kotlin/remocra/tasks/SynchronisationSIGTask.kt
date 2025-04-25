package remocra.tasks

import jakarta.inject.Inject
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.NotificationMailData
import remocra.db.CommuneRepository
import remocra.db.EntrepotSigRepository
import remocra.db.SigRepository
import remocra.db.VoieRepository
import remocra.db.jooq.bindings.GeometryBinding
import remocra.db.jooq.bindings.ZonedDateTimeBinding
import remocra.db.jooq.remocra.enums.TypeTask
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

class SynchronisationSIGTask : SchedulableTask<SynchronisationSIGTaskParameter, SchedulableTaskResults>() {

    @Inject lateinit var communeRepository: CommuneRepository

    @Inject lateinit var voieRepository: VoieRepository

    @Inject lateinit var entrepotSigRepository: EntrepotSigRepository

    @Inject lateinit var sigRepository: SigRepository

    override fun execute(parameters: SynchronisationSIGTaskParameter?, userInfo: WrappedUserInfo): SchedulableTaskResults? {
        // La vérification des paramètres a déja été faite par checkParameters, pour éviter de spécifier !! a chaque appel, on duplique le dit paramètre
        val timeSource = TimeSource.Monotonic
        val startJob = timeSource.markNow()
        val listeTableASynchroniser = parameters!!.listeTableASynchroniser
        logManager.info("Exécution du job")
        /** Récupération des informations coté SIG : */
        logManager.info("Récupération des données à synchroniser")
        val startRecuperation = timeSource.markNow()
        listeTableASynchroniser.forEach { tableASynchroniser ->
            val startSynchroTable = timeSource.markNow()
            // [SIG] Récupération de la structure de la table
            logManager.info("[SIG] Récupération des informations coté SIG : ${tableASynchroniser.schemaSource}.${tableASynchroniser.tableSource}")
            val structureTable = sigRepository.getMetaStructureTable(tableASynchroniser.schemaSource, tableASynchroniser.tableSource)
            // [REMOCRA] Creation de la table
            // Construction de la requête CREATE TABLE
            logManager.info("[REMOcRA] Récupération des champs pour la création de la table")
            val concatColumn = structureTable.joinToString(", ") { columnInfo ->
                "${columnInfo.columnName} ${columnInfo.columnType} ${if (columnInfo.columnNullable) "NULL" else "NOT NULL"}"
            }
            logManager.info("[REMOcRA] Champs à insérer : $concatColumn")
            val nomTableDestination = tableASynchroniser.tableDestination ?: tableASynchroniser.tableSource
            // Suppression de la table si elle est déja présente coté remocra
            logManager.info("[REMOcRA] Suppression de la table $nomTableDestination si elle existe.")
            entrepotSigRepository.dropTable(tableDestination = nomTableDestination)
            // Création de la table coté remocra
            logManager.info("[REMOcRA] Création de la table $nomTableDestination")
            entrepotSigRepository.createTable(tableDestination = nomTableDestination, concatColumn = concatColumn)
            // [SIG] Récupération des données
            // Construction requete Select
            logManager.info("[SIG] Création de la requête 'SELECT ...'")
            val fieldsToSelect = structureTable.map { columnInfo ->
                when (columnInfo.columnType.lowercase()) {
                    // Types spécifiques
                    "geometry" -> DSL.field(columnInfo.columnName, GeometryBinding())
                    "timestamp with time zone" -> DSL.field(columnInfo.columnName, ZonedDateTimeBinding())
                    // Sinon, tout en text et au besoin la vue/le scriptPostSynchro se chargera de cast correctement
                    else -> DSL.field(columnInfo.columnName, SQLDataType.VARCHAR)
                }
            }
            logManager.info("[SIG] Liste des fields à SELECT : $fieldsToSelect")
            // Émission de la requête SELECT
            val dataToInsert = sigRepository.selectAll(fieldsToSelect, tableASynchroniser.schemaSource, tableASynchroniser.tableSource)
            logManager.info("[SIG] ${dataToInsert.size} éléments récupérés")
            // [REMOCRA] Insertion des données
            logManager.info("[REMOcRA] Insertion de ces éléments dans ${GlobalConstants.SCHEMA_ENTREPOT_SIG}.$nomTableDestination")
            entrepotSigRepository.insertAllInto(dataToInsert, nomTableDestination)
            logManager.info("Fin de la récupération des données de ${tableASynchroniser.schemaSource}.${tableASynchroniser.tableSource} (${getStringifiedExecutionDuration(startSynchroTable)})")
        }
        logManager.info("Fin de la phase de récupération des données (${getStringifiedExecutionDuration(startRecuperation)})")

        /** Traitement des informations reçues : */
        val startPostTraitement = timeSource.markNow()
        logManager.info("Traitement des données")
        listeTableASynchroniser.forEach { tableASynchroniser ->
            val startPostTraitementTable = timeSource.markNow()
            logManager.info("Traitement de ${tableASynchroniser.tableDestination ?: tableASynchroniser.tableSource}")
            logManager.info("Type de synchronisation ${tableASynchroniser.typeSynchronisation}")
            when (tableASynchroniser.typeSynchronisation) {
                TypeSynchronisation.STOCKAGE_SIMPLE -> {
                    if (!tableASynchroniser.scriptPostRecuperation.isNullOrEmpty()) {
                        logManager.info("[${tableASynchroniser.typeSynchronisation}] Exécution du script post-récupération")
                        val listeRequete = tableASynchroniser.scriptPostRecuperation.split(";")
                        listeRequete.forEach { requete ->
                            if (requete.trim().isNotEmpty()) {
                                logManager.info("[${tableASynchroniser.typeSynchronisation}] ${requete.trim()}")
                                entrepotSigRepository.executeFromString(requete.trim())
                            }
                        }
                    }
                }
                TypeSynchronisation.MISE_A_JOUR_REMOCRA_COMMUNE -> {
                    /** Mise a jour des éléments déja présent coté remocra */
                    logManager.info("[${tableASynchroniser.typeSynchronisation}] Mise à jour de la table remocra.commune")
                    val champsAUpdate = tableASynchroniser.listeChampsAUpdate ?: listOf("libelle", "code_postal", "geometrie", "pprif")
                    logManager.info("[${tableASynchroniser.typeSynchronisation}] Champs à mettre à jour : $champsAUpdate")
                    communeRepository.updateFromEntrepotSig(champsAUpdate)
                    /** Insertion des nouveaux éléments */
                    // Récupération de tous les code_insee présent coté remocra pour filtrer les éléments a insérer
                    logManager.info("[${tableASynchroniser.typeSynchronisation}] Insertion des nouveaux éléments dans table remocra.commune, s'il y en a.")
                    val listeCodeInseeDejePresent = communeRepository.getAllCodeInsee()
                    // Insertion des éléments dont le code_insee n'est pas déjà référencé
                    communeRepository.insertFromEntrepotSig(listeCodeInseeDejePresent)
                }
                TypeSynchronisation.MISE_A_JOUR_REMOCRA_VOIE -> {
                    /** Mise a jour des éléments déja présent coté remocra */
                    // Le libelle et commune_id servant à l'identification de l'élément, on ne peut pas les mettre à jour
                    // On ne laisse pas le choix sur ce qui peut etre update et on n'update que la géométrie
                    logManager.info("[${tableASynchroniser.typeSynchronisation}] Mise à jour de la table remocra.voie")
                    voieRepository.updateGeomFromEntrepotSig()
                    /** Insertion des nouveaux éléments */
                    // Récupération des éléments à insérer
                    val nouvellesVoies = voieRepository.getAllNewElementFromEntrepotSig()
                    if (nouvellesVoies.isNotEmpty()) {
                        logManager.info("[${tableASynchroniser.typeSynchronisation}] Insertion des nouveaux éléments dans table remocra.voie")
                        nouvellesVoies.forEach {
                            if (it != null) {
                                logManager.info("[${tableASynchroniser.typeSynchronisation}] Insertion de $it")
                                voieRepository.insertVoie(it)
                            }
                        }
                    }
                }
            }
            logManager.info("[${tableASynchroniser.typeSynchronisation}] Fin du post-traitement (${getStringifiedExecutionDuration(startPostTraitementTable)})")
        }
        logManager.info("Fin de la phase de post-traitement (${getStringifiedExecutionDuration(startPostTraitement)})")
        logManager.info("Fin de l'exécution du job (${getStringifiedExecutionDuration(startJob)})")
        return null
    }

    override fun notifySpecific(executionResults: SchedulableTaskResults?, notificationRaw: NotificationRaw) {
        // Pas de notification pour le moment
    }

    override fun checkParameters(parameters: SynchronisationSIGTaskParameter?) {
        if (parameters == null) {
            throw IllegalArgumentException("Aucun paramètre fourni")
        }
        if (parameters.listeTableASynchroniser.isEmpty()) {
            throw IllegalArgumentException("Aucune table à synchroniser")
        }
        parameters.listeTableASynchroniser.forEach { tableASynchroniser ->
            if (tableASynchroniser.schemaSource.isEmpty()) {
                throw IllegalArgumentException("Le schéma source n'est pas fourni")
            } else if (tableASynchroniser.tableSource.isEmpty()) {
                throw IllegalArgumentException("La table source n'est pas fournie")
            }
        }
    }

    override fun getType(): TypeTask {
        return TypeTask.SYNCHRONISATION_SIG
    }

    override fun getTaskParametersClass(): Class<SynchronisationSIGTaskParameter> {
        return SynchronisationSIGTaskParameter::class.java
    }

    private fun getStringifiedExecutionDuration(startReference: TimeSource.Monotonic.ValueTimeMark): String =
        startReference.elapsedNow().toString(DurationUnit.SECONDS, 2)
}

class SynchronisationSIGTaskParameter(
    override val notification: NotificationMailData?,
    val listeTableASynchroniser: Set<TableASynchroniser>,
) : SchedulableTaskParameters(notification)

data class TableASynchroniser(
    val schemaSource: String,
    val tableSource: String,
    val tableDestination: String?,
    val typeSynchronisation: TypeSynchronisation,
    val listeChampsAUpdate: List<String>?,
    val scriptPostRecuperation: String?,
)

enum class TypeSynchronisation {
    MISE_A_JOUR_REMOCRA_COMMUNE,
    MISE_A_JOUR_REMOCRA_VOIE,
    STOCKAGE_SIMPLE,
}
