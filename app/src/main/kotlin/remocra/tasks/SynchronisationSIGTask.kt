package remocra.tasks

import jakarta.inject.Inject
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.NotificationMailData
import remocra.data.enums.ErrorType
import remocra.db.CommuneRepository
import remocra.db.EntrepotSigRepository
import remocra.db.SigRepository
import remocra.db.VoieRepository
import remocra.db.jooq.bindings.GeometryBinding
import remocra.db.jooq.bindings.ZonedDateTimeBinding
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.exception.RemocraResponseException
import remocra.utils.RequestUtils
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

class SynchronisationSIGTask
@Inject
constructor(
    private val communeRepository: CommuneRepository,
    private val voieRepository: VoieRepository,
    private val entrepotSigRepository: EntrepotSigRepository,
    private val sigRepository: SigRepository,
    private val requestUtils: RequestUtils,
) :
    SchedulableTask<SynchronisationSIGTaskParameter, SchedulableTaskResults>() {

    val BATCH_SIZE = 50_000

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
            val isStockageSimple = tableASynchroniser.typeSynchronisation == TypeSynchronisation.STOCKAGE_SIMPLE
            val startSynchroTable = timeSource.markNow()
            // [SIG] Récupération de la structure de la table
            logManager.info("[SIG] Récupération des informations coté SIG : ${tableASynchroniser.schemaSource}.${tableASynchroniser.tableSource}")
            val structureTable =
                try {
                    sigRepository.getMetaStructureTable(tableASynchroniser.schemaSource, tableASynchroniser.tableSource)
                } catch (e: Exception) {
                    logManager.error("[SIG] Erreur lors de la récupération de la structure de la table ${tableASynchroniser.schemaSource}.${tableASynchroniser.tableSource} : ${e.message}")
                    return@forEach
                }

            // [REMOCRA] Creation de la table
            // Construction de la requête CREATE TABLE
            logManager.info("[REMOcRA] Récupération des champs pour la création de la table")
            val concatColumn = structureTable.joinToString(", ") { columnInfo ->
                "${columnInfo.columnName} ${columnInfo.columnType} ${if (columnInfo.columnNullable) "NULL" else "NOT NULL"}"
            }
            logManager.info("[REMOcRA] Champs à insérer : $concatColumn")
            val nomTableDestination = tableASynchroniser.tableDestination ?: tableASynchroniser.tableSource
            if (!isStockageSimple) {
                logManager.info("[REMOcRA] Suppression de la vue correspondante")
                dropViewOfTable(tableASynchroniser.typeSynchronisation)
            }
            // Suppression de la table si elle est déja présente coté remocra
            logManager.info("[REMOcRA] Suppression de la table $nomTableDestination si elle existe.")
            entrepotSigRepository.dropTable(tableDestination = nomTableDestination)
            // Création de la table coté remocra
            logManager.info("[REMOcRA] Création de la table $nomTableDestination")
            entrepotSigRepository.createTable(tableDestination = nomTableDestination, concatColumn = concatColumn)
            // Création de la vue côté remocra
            if (!tableASynchroniser.scriptCreationVue.isNullOrBlank() && !isStockageSimple) {
                logManager.info("[REMOcRA] Création de la vue correspondante")
                requestUtils.validateQueryWithCreate(tableASynchroniser.scriptCreationVue)
                val queryResult = entrepotSigRepository.executeFromString(tableASynchroniser.scriptCreationVue)
                if (queryResult != 0) {
                    logManager.error("La requête SQL de création de la vue n'est pas valide.")
                    throw RemocraResponseException(ErrorType.REQUETE_SQL_CREATION_INVALIDE)
                }
            }
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
            // Émission de la requête SELECT avec traitement par BATCH
            // Pour les très grandes tables (millions de lignes), on traite par chunk de parameters.batchInsert lignes
            // pour éviter les problèmes de mémoire et les timeouts de connexion
            logManager.info("[SIG] Début du traitement par BATCH (${parameters.batchInsert ?: BATCH_SIZE} lignes/lot)")
            var totalRows = 0
            var batchNumber = 0

            sigRepository.selectAllByBatch(
                fieldsToSelect,
                tableASynchroniser.schemaSource,
                tableASynchroniser.tableSource,
                batchSize = parameters.batchInsert ?: BATCH_SIZE,
            ) { batch ->
                batchNumber++
                logManager.info("[REMOcRA] Insertion du batch #$batchNumber contenant ${batch.size} éléments dans ${GlobalConstants.SCHEMA_ENTREPOT_SIG}.$nomTableDestination")
                entrepotSigRepository.insertBatch(batch, nomTableDestination)
                totalRows += batch.size
                logManager.info("[SIG] Total inséré jusqu'à présent: $totalRows éléments")
            }

            logManager.info("[SIG] $totalRows éléments au total récupérés et insérés")
            logManager.info("Fin de la récupération des données de ${tableASynchroniser.schemaSource}.${tableASynchroniser.tableSource} (${getStringifiedExecutionDuration(startSynchroTable)})")
        }
        logManager.info("Fin de la phase de récupération des données (${getStringifiedExecutionDuration(startRecuperation)})")

        /** Traitement des informations reçues : */
        val startPostTraitement = timeSource.markNow()
        logManager.info("Traitement des données")
        listeTableASynchroniser.sortedBy { it.typeSynchronisation.ordre }.forEach { tableASynchroniser ->
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
                    val champsAUpdate = tableASynchroniser.listeChampsAUpdate ?: listOf("LIBELLE", "CODE_POSTAL", "GEOMETRIE", "PPRIF", "CODE")
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
        requireNotNull(parameters) { "Aucun paramètre fourni" }
        require(parameters.listeTableASynchroniser.isNotEmpty()) { "Aucune table à synchroniser" }

        parameters.listeTableASynchroniser.forEach { tableASynchroniser ->
            val isStockageSimple: Boolean = tableASynchroniser.typeSynchronisation == TypeSynchronisation.STOCKAGE_SIMPLE
            require(tableASynchroniser.schemaSource.isNotEmpty()) { "Le schéma source n'est pas fourni" }
            require(tableASynchroniser.tableSource.isNotEmpty()) { "La table source n'est pas fournie" }
            require(!(tableASynchroniser.scriptCreationVue.isNullOrBlank() && !isStockageSimple)) { "Le script de création de vue est nécessaire pour le type de synchronisation ${tableASynchroniser.typeSynchronisation}" }

            if (!tableASynchroniser.scriptCreationVue.isNullOrBlank() && !isStockageSimple) {
                requestUtils.validateQueryWithCreate(tableASynchroniser.scriptCreationVue)
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

    private fun dropViewOfTable(typeSynchro: TypeSynchronisation) {
        when (typeSynchro) {
            TypeSynchronisation.MISE_A_JOUR_REMOCRA_COMMUNE -> communeRepository.dropViewForEntrepotSig()
            TypeSynchronisation.MISE_A_JOUR_REMOCRA_VOIE -> voieRepository.dropViewForEntrepotSig()
            TypeSynchronisation.STOCKAGE_SIMPLE -> Unit
        }
    }
}

class SynchronisationSIGTaskParameter(
    override val notification: NotificationMailData?,
    val listeTableASynchroniser: Set<TableASynchroniser>,
    var batchInsert: Int?,
) : SchedulableTaskParameters(notification)

data class TableASynchroniser(
    val schemaSource: String,
    val tableSource: String,
    val tableDestination: String?,
    val typeSynchronisation: TypeSynchronisation,
    val listeChampsAUpdate: List<String>?,
    val scriptPostRecuperation: String?,
    val scriptCreationVue: String?,
)

enum class TypeSynchronisation(val ordre: Int) {
    MISE_A_JOUR_REMOCRA_COMMUNE(1),
    MISE_A_JOUR_REMOCRA_VOIE(2),
    STOCKAGE_SIMPLE(3),
}
