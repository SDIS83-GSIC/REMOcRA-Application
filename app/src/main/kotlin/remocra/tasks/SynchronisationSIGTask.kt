package remocra.tasks

import jakarta.inject.Inject
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import remocra.auth.UserInfo
import remocra.db.CommuneRepository
import remocra.db.EntrepotSigRepository
import remocra.db.SigRepository
import remocra.db.VoieRepository
import remocra.db.jooq.bindings.GeometryBinding
import remocra.db.jooq.bindings.ZonedDateTimeBinding
import remocra.db.jooq.remocra.enums.TypeTask

class SynchronisationSIGTask : SchedulableTask<SynchronisationSIGTaskParameter, SchedulableTaskResults>() {

    @Inject lateinit var communeRepository: CommuneRepository

    @Inject lateinit var voieRepository: VoieRepository

    @Inject lateinit var entrepotSigRepository: EntrepotSigRepository

    @Inject lateinit var sigRepository: SigRepository

    override fun execute(parameters: SynchronisationSIGTaskParameter?, userInfo: UserInfo): SchedulableTaskResults? {
        // La vérification des paramètres a déja été faite par checkParameters, pour éviter de spécifier !! a chaque appel, on duplique le dit paramètre
        val listeTableASynchroniser = parameters!!.listeTableASynchroniser
        /** Récupération des informations coté SIG : */
        listeTableASynchroniser.forEach { tableASynchroniser ->
            // [SIG] Récupération de la structure de la table
            val structureTable = sigRepository.getMetaStructureTable(tableASynchroniser.schemaSource, tableASynchroniser.tableSource)
            // [REMOCRA] Creation de la table
            // Construction de la requête CREATE TABLE
            val concatColumn = structureTable.joinToString(", ") { columnInfo ->
                "${columnInfo.columnName} ${columnInfo.columnType} ${if (columnInfo.columnNullable) "NULL" else "NOT NULL"}"
            }
            val nomTableDestination = tableASynchroniser.tableDestination ?: tableASynchroniser.tableSource
            // Suppression de la table si elle est déja présente coté remocra
            entrepotSigRepository.dropTable(tableDestination = nomTableDestination)
            // Création de la table coté remocra
            entrepotSigRepository.createTable(tableDestination = nomTableDestination, concatColumn = concatColumn)
            // [SIG] Récupération des données
            // Construction requete Select
            val fieldsToSelect = structureTable.map { columnInfo ->
                when (columnInfo.columnType.lowercase()) {
                    // Types spécifiques
                    "geometry" -> DSL.field(columnInfo.columnName, GeometryBinding())
                    "timestamp with time zone" -> DSL.field(columnInfo.columnName, ZonedDateTimeBinding())
                    // Sinon, tout en text et au besoin la vue/le scriptPostSynchro se chargera de cast correctement
                    else -> DSL.field(columnInfo.columnName, SQLDataType.VARCHAR)
                }
            }
            // Émission de la requête SELECT
            val dataToInsert = sigRepository.selectAll(fieldsToSelect, tableASynchroniser.schemaSource, tableASynchroniser.tableSource)
            // [REMOCRA] Insertion des données
            entrepotSigRepository.insertAllInto(dataToInsert, nomTableDestination)
        }

        /** Traitement des informations reçues : */
        listeTableASynchroniser.forEach { tableASynchroniser ->
            when (tableASynchroniser.typeSynchronisation) {
                TypeSynchronisation.STOCKAGE_SIMPLE -> {
                    if (!tableASynchroniser.scriptPostRecuperation.isNullOrEmpty()) {
                        val listeRequete = tableASynchroniser.scriptPostRecuperation.split(";")
                        listeRequete.forEach { requete ->
                            if (requete.trim().isNotEmpty()) {
                                entrepotSigRepository.executeFromString(requete.trim())
                            }
                        }
                    }
                }
                TypeSynchronisation.MISE_A_JOUR_REMOCRA_COMMUNE -> {
                    /** Mise a jour des éléments déja présent coté remocra */
                    val champsAUpdate = tableASynchroniser.listeChampsAUpdate ?: listOf("libelle", "code_postal", "geometrie", "pprif")
                    communeRepository.updateFromEntrepotSig(champsAUpdate)
                    /** Insertion des nouveaux éléments */
                    // Récupération de tous les code_insee présent coté remocra pour filtrer les éléments a insérer
                    val listeCodeInseeDejePresent = communeRepository.getAllCodeInsee()
                    // Insertion des éléments dont le code_insee n'est pas déjà référencé
                    communeRepository.insertFromEntrepotSig(listeCodeInseeDejePresent)
                }
                TypeSynchronisation.MISE_A_JOUR_REMOCRA_VOIE -> {
                    /** Mise a jour des éléments déja présent coté remocra */
                    // Le libelle et commune_id servant à l'identification de l'élément, on ne peut pas les mettre à jour
                    // On ne laisse pas le choix sur ce qui peut etre update et on n'update que la géométrie
                    voieRepository.updateGeomFromEntrepotSig()
                    /** Insertion des nouveaux éléments */
                    // Récupération des éléments à insérer
                    val nouvellesVoies = voieRepository.getAllNewElementFromEntrepotSig()
                    nouvellesVoies.forEach {
                        if (it != null) {
                            voieRepository.insertVoie(it)
                        }
                    }
                }
            }
        }
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
}

class SynchronisationSIGTaskParameter(
    override val notification: NotificationMail?,
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
