package remocra.usecase.pei

import com.google.inject.Inject
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.data.PeiForCalculDispoData
import remocra.data.enums.CodeSdis
import remocra.db.CalculDispoRepository
import remocra.db.PoidsAnomalieRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.usecase.AbstractUseCase
import java.util.UUID

// TODO spécifique au 39, mais on aimerait que ça saute, c'est a priori juste une problématique de numérotation ; en attendant, on constantise pour en garder trace
const val NATURE_PEI_A = "A"

/**
 * But du usecase (pour l'instant) : tabula rasa sur les anomalies système, recalcul des ano système, mise à jour du statut de disponibilité du PEI.
 * Doit être morcelé (morcelable) afin de s'intégrer dans l'enregistrement transactionnel d'un PEI.
 */
class CalculDispoUseCase : AbstractUseCase() {
    @Inject
    private lateinit var appSettings: AppSettings

    @Inject
    private lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    private lateinit var calculDispoRepository: CalculDispoRepository

    @Inject
    private lateinit var visiteRepository: VisiteRepository

    @Inject
    private lateinit var poidsAnomalieRepository: PoidsAnomalieRepository

    private fun getAnomalieIdFromCode(code: String): UUID {
        return (dataCacheProvider.getAnomalies().values).find { ano -> ano.anomalieCode == code }!!.anomalieId
    }

    private fun deleteAnomaliesDebitPression(pei: PeiForCalculDispoData) {
        // On récupère l'ID de chaque anomalie système à supprimer
        val anomalieASupprIds = TypePredicatDispo.entries.map { it.name }.map { codeAno -> getAnomalieIdFromCode(codeAno) }

        calculDispoRepository.deleteAnomalies(pei.peiId, anomalieASupprIds)
    }

    private fun insertAnomaliesDebitPression(pei: PeiForCalculDispoData, anomaliesIds: Collection<UUID>) {
        calculDispoRepository.insertAnomaliesSysteme(pei.peiId, anomaliesIds)
    }

    private fun checkDiametreId(pei: PeiForCalculDispoData) {
        if (pei.diametreId == null) {
            throw IllegalArgumentException("Pas de diamètre pour le calcul de dispo")
        }
    }

    /**
     * Garantit que l'objet PEI a bien son objet *diametre* chargé, soit parce qu'il l'est déjà, soit en le faisant au travers du *peiDiametreId*
     * Modifie le paramètre d'entrée *pei* pour éviter tout appel ultérieur
     * @return Diametre
     */
    private fun ensureDiametre(pei: PeiForCalculDispoData): Diametre {
        checkDiametreId(pei)
        if (pei.diametre == null) {
            pei.diametre = dataCacheProvider.getDiametres()[pei.diametreId!!] as Diametre
        }
        return pei.diametre!!
    }

    /**
     * Garantit que l'objet PEI a bien son objet *nature* chargé, soit parce qu'il l'est déjà, soit en le faisant au travers du *peiNatureId*
     * Modifie le paramètre d'entrée *pei* pour éviter tout appel ultérieur
     * @return Nature
     */
    private fun ensureNature(pei: PeiForCalculDispoData): Nature {
        if (pei.nature == null) {
            pei.nature = dataCacheProvider.getNatures()[pei.peiNatureId] as Nature
        }
        return pei.nature!!
    }

    fun execute(pei: PeiForCalculDispoData): Disponibilite {
        // TODO à voir si on morcelle (les citeaux) ou si on laisse en monolithique, avec le service d'enregistrement du PEI
        deleteAnomaliesDebitPression(pei)
        val anomaliesDebitPression = computeAnomaliesDebitPression(pei)
        // TODO idem ici, laisser private si possible, sinon exposer et ne pas appeler dans le execute.
        insertAnomaliesDebitPression(pei, anomaliesDebitPression)

        // on a besoin de la  dernière visite :
        val lastVisite = visiteRepository.getLastVisite(pei.peiId)
        val anomaliesDerniereVisite: List<Anomalie> = lastVisite?.let { visiteRepository.getAnomaliesFromVisite(lastVisite.visiteId).map { dataCacheProvider.getAnomalies()[it]!! } }
            ?: listOf()

        val anomalieIndispoTemp = getIndispoTemporaires(pei)?.let { dataCacheProvider.getAnomalies()[it] }

        // On construit un set contenant toutes les anomalies possibles
        val setGlobalAnomalies: MutableSet<Anomalie> = mutableSetOf()
        if (anomaliesDebitPression.isNotEmpty()) {
            setGlobalAnomalies.addAll(anomaliesDebitPression.map { dataCacheProvider.getAnomalies()[it]!! })
        }
        if (anomaliesDerniereVisite.isNotEmpty()) {
            setGlobalAnomalies.addAll(anomaliesDerniereVisite)
        }
        if (anomalieIndispoTemp != null) {
            setGlobalAnomalies.add(anomalieIndispoTemp)
        }

        if (setGlobalAnomalies.isNotEmpty()) {
            // On va chercher les poids des anomalies concernées
            val poidsAnomalies = poidsAnomalieRepository.getPoidsAnomalies(setGlobalAnomalies.map { it.anomalieId }, pei.peiNatureId, lastVisite?.visiteTypeVisite)

            // On calcule le poids total, comme étant la somme des poids de chaque anomalie présente sur le PEI, quelle que soit sa provenance
            val note = setGlobalAnomalies.map { ano ->
                poidsAnomalies.find { poidsAno -> poidsAno.poidsAnomalieAnomalieId == ano.anomalieId }?.poidsAnomalieValIndispoTerrestre
            }.reduce { acc, next -> if (acc != null && next != null) acc.plus(next) else null } ?: 0

            if (note > 5) {
                return Disponibilite.INDISPONIBLE
            }
            // Sinon, il est peut-être non conforme
            if (setGlobalAnomalies.any { !it.anomalieRendNonConforme }) {
                return Disponibilite.NON_CONFORME
            }
        }
        // Le PEI est disponible, on n'a rien à faire de particulier
        return Disponibilite.DISPONIBLE
    }

    // TODO quoi retourner, l'UUID, boolean, le futur objet IT ? Pour l'instant, on imagine retourner l'UUID de l'anomalie si elle existe
    private fun getIndispoTemporaires(pei: PeiForCalculDispoData): UUID? {
        return null
    }

    /**
     * Fonction permettant de calculer les anomalies liées au débit / pression d'un PEI (=anomalies "système")
     *
     */
    private fun computeAnomaliesDebitPression(pei: PeiForCalculDispoData): Set<UUID> {
        val naturePei = ensureNature(pei)

        val mapPredicats: MutableMap<TypePredicatDispo, Boolean> = mutableMapOf()

        if (TypePei.PIBI == naturePei.natureTypePei) {
            mapPredicats[TypePredicatDispo.PRESSION_INSUFF] = isPressionInsuffisante(pei)
            mapPredicats[TypePredicatDispo.PRESSION_NON_CONFORME] = isPressionNonConforme(pei)
            mapPredicats[TypePredicatDispo.PRESSION_TROP_ELEVEE] = isPressionTropElevee(pei)

            mapPredicats[TypePredicatDispo.PRESSION_DYN_INSUFF] = isPressionDynamiqueInsuffisante(pei)
            mapPredicats[TypePredicatDispo.PRESSION_DYN_NON_CONFORME] = isPressionDynamiqueNonConforme(pei)
            mapPredicats[TypePredicatDispo.PRESSION_DYN_TROP_ELEVEE] = isPressionDynamiqueTropElevee(pei)

            mapPredicats[TypePredicatDispo.DEBIT_INSUFF] = isDebitInsuffisant(pei)
            mapPredicats[TypePredicatDispo.DEBIT_NON_CONFORME] = isDebitNonConforme(pei)
            mapPredicats[TypePredicatDispo.DEBIT_TROP_ELEVE] = isDebitTropEleve(pei)
        } else {
            mapPredicats[TypePredicatDispo.VOLUME_INSUFF] = isVolumeInsuffisant(pei)
            mapPredicats[TypePredicatDispo.VOLUME_NON_CONFORME] = isVolumeNonConforme(pei)
        }

        // TODO cas des anomalies spéficiques, comme au 38 ? voir si on peut les éliminer, sinon prendre en compte ici, quitte à avoir une méthode is[Whatever]Specific qui retourne un booléen qu'on ajoute à la liste
        // TODO POur l'instant on a que "si on ne trouve pas de visite avec CDP, on pose une ano CDP_NON_REALISEE", mais ne veut-on pas qu'un PEI soit indispo tant qu'on est pas certain qu'il l'est ???

        // On ne retourne que les prédicats retournant TRUE, sous forme d'ID d'anomalie pour insertion directe en base
        return mapPredicats.entries.filter { it.value }.map { getAnomalieIdFromCode(it.key.name) }.toSet()
    }

    private fun isVolumeInsuffisant(pei: PeiForCalculDispoData): Boolean {
        if (pei.penaCapaciteIllimitee == true) {
            return false
        }

        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> pei.penaCapaciteIllimitee != true && (pei.penaCapacite == null || pei.penaCapacite < 30)
            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> false
            CodeSdis.SDIS_21 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> {
                if (pei.penaCapacite != null && pei.penaCapacite < 60) {
                    return true
                }
                if (
                    pei.nature!!.natureCode == NATURE_PEI_A &&
                    (pei.penaCapacite == null || pei.penaCapacite < 60)
                ) {
                    return true
                }

                return false
            }

            CodeSdis.SDIS_42 -> false
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> pei.penaCapacite != null && pei.penaCapacite < 30
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> false
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> false
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> pei.penaCapacite != null && pei.penaCapacite < 10
        }
    }

    private fun isVolumeNonConforme(pei: PeiForCalculDispoData): Boolean {
        if (pei.penaCapaciteIllimitee == true) {
            return false
        }

        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> false
            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> false
            CodeSdis.SDIS_21 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> pei.penaCapacite != null && pei.penaCapacite in 60..119
            CodeSdis.SDIS_42 -> false
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> false
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> false
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> false
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> pei.penaCapacite != null && pei.penaCapacite in 10..29
        }
    }

    private fun isPressionInsuffisante(pei: PeiForCalculDispoData): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> false
            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> false
            CodeSdis.SDIS_21 -> false
            CodeSdis.SDIS_38 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_39 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_42 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> {
                if (isNaturePIBI(pei)) {
                    if (pei.pression != null && pei.pression >= 0 && pei.pression < 1) {
                        return true
                    }
                }

                return false
            }

            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_95 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> false
        }
    }

    private fun isPressionNonConforme(pei: PeiForCalculDispoData): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> false
            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> false
            CodeSdis.SDIS_21 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> false
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> false
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> false
            CodeSdis.SDIS_78 -> pei.pression != null && pei.pression > 0 && pei.pression < 1
            CodeSdis.SDIS_83 -> false
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> false
        }
    }

    private fun isPressionTropElevee(pei: PeiForCalculDispoData): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> false
            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> false
            CodeSdis.SDIS_21 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> isPressionTropEleveeDefault(pei)
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> pei.pression != null && pei.pression > 14
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> pei.pression != null && pei.pression > 8
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> isPressionTropEleveeDefault(pei)
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> isPressionTropEleveeDefault(pei)
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> false
        }
    }

    private fun isPressionDynamiqueInsuffisante(pei: PeiForCalculDispoData): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> false
            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> isPressionDynamiqueInsuffisanteDefault(pei)
            CodeSdis.SDIS_21 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> isPressionDynamiqueInsuffisanteDefault(pei)
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> false
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> isPressionDynamiqueInsuffisanteDefault(pei)
            CodeSdis.SDIS_77 -> isPressionDynamiqueInsuffisanteDefault(pei)
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> {
                if (isNaturePIBI(pei)) {
                    if (pei.pressionDynamique != null && pei.pressionDynamique >= 0.1 && pei.pressionDynamique < 1) {
                        return true
                    }
                }

                return false
            }

            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> isPressionDynamiqueInsuffisanteDefault(pei)
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> false
        }
    }

    private fun isPressionDynamiqueNonConforme(pei: PeiForCalculDispoData): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> false
            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> false
            CodeSdis.SDIS_21 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> false
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> pei.pressionDynamique != null && pei.pressionDynamique < 1
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> pei.pressionDynamique != null && pei.pressionDynamique >= 1 && pei.pressionDynamique < 2
            CodeSdis.SDIS_77 -> false
            CodeSdis.SDIS_78 -> pei.pressionDynamique != null && pei.pressionDynamique > 0 && pei.pressionDynamique < 1
            CodeSdis.SDIS_83 -> false
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> false
        }
    }

    private fun isPressionDynamiqueTropElevee(pei: PeiForCalculDispoData): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> false
            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> pei.pressionDynamique != null && pei.pressionDynamique >= 6
            CodeSdis.SDIS_21 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> isPressionDynamiqueTropEleveeDefault(pei)
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> pei.pressionDynamique != null && pei.pressionDynamique > 8
            CodeSdis.SDIS_66 -> false
            // TODO choix à faire (avec le SDIS) : seuil à 10 ou à 16 ?
            CodeSdis.SDIS_71 -> pei.pressionDynamique != null && pei.pressionDynamique > 16
            CodeSdis.SDIS_77 -> isPressionDynamiqueTropEleveeDefault(pei)
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> isPressionDynamiqueTropEleveeDefault(pei)
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> false
        }
    }

    private fun isDebitInsuffisant(pei: PeiForCalculDispoData): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> pei.debit == null || pei.debit < 30
            CodeSdis.SDIS_09 -> pei.debit != null && pei.debit == 0
            CodeSdis.SDIS_14 -> pei.debit != null && pei.debit < 30
            CodeSdis.SDIS_21 -> pei.debit != null && pei.debit < 30
            CodeSdis.SDIS_38 -> pei.debit == null || pei.debit < 15
            CodeSdis.SDIS_39 -> (pei.debit == null || pei.debit <= 29)
            CodeSdis.SDIS_42 -> isDebitInsuffisantDefault(pei)
            CodeSdis.SDIS_49 -> {
                if (isDiametre80(pei) &&
                    isNaturePI(pei) &&
                    (pei.debit == null || pei.debit < 15)
                ) {
                    return true
                } else if (listOf(GlobalConstants.DIAMETRE_100, GlobalConstants.DIAMETRE_150).contains(ensureDiametre(pei).diametreCode) &&
                    (pei.debit == null || pei.debit < 30)
                ) {
                    return true
                }

                return false
            }

            CodeSdis.SDIS_53 -> (pei.debit == null || pei.debit <= 15)
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> {
                if (listOf(GlobalConstants.DIAMETRE_80, GlobalConstants.DIAMETRE_100).contains(ensureDiametre(pei).diametreCode) &&
                    (pei.debit == null || pei.debit < 30)
                ) {
                    return true
                } else if (isDiametre150(pei) &&
                    (pei.debit == null || pei.debit < 60)
                ) {
                    return true
                }

                return false
            }

            CodeSdis.SDIS_66 -> pei.debit != null && pei.debit < 30
            CodeSdis.SDIS_71 -> {
                if (pei.diametreId == null || listOf(GlobalConstants.DIAMETRE_80, GlobalConstants.DIAMETRE_100).contains(ensureDiametre(pei).diametreCode) &&
                    (pei.debit != null && pei.debit < 30)
                ) {
                    return true
                } else if (isDiametre150(pei) &&
                    (pei.debit != null && pei.debit < 60)
                ) {
                    return true
                }
                return false
            }

            CodeSdis.SDIS_77 -> isDebitInsuffisantDefault(pei)
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> isDebitInsuffisantDefault(pei)
            CodeSdis.SDIS_89 -> pei.debit != null && pei.debit < 15
            CodeSdis.SDIS_91 -> {
                if (pei.debit != null) {
                    if (isDiametre150(pei)) {
                        return pei.debit < 90
                    }
                    return pei.debit < 30
                }

                return false
            }

            CodeSdis.SDIS_95 -> {
                if (isDiametre80(pei)) {
                    if (pei.debit != null && pei.debit < 15) {
                        return true
                    }
                }
                if (isDiametre100(pei)) {
                    if (pei.debit != null && pei.debit < 30) {
                        return true
                    }
                }
                if (isDiametre150(pei)) {
                    if (pei.debit != null && pei.debit < 60) {
                        return true
                    }
                }

                return false
            }

            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> pei.debit == null || pei.debit < 60
            CodeSdis.SDMIS -> false
        }
    }

    private fun isDebitNonConforme(pei: PeiForCalculDispoData): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> {
                // Cas du diamètre NULL à gérer avant que ça puisse planter
                if (pei.diametreId == null && pei.debit in 30..59) {
                    return true
                }

                val diametre = ensureDiametre(pei)
                // Un débit à NULL entrainera un DEBIT_INSUFF, donc non traité ici
                if (pei.debit == null) {
                    return false
                }
                if (GlobalConstants.DIAMETRE_100 == diametre.diametreCode) {
                    if (pei.debit in 30..59) {
                        return true
                    }
                } else if (GlobalConstants.DIAMETRE_150 == diametre.diametreCode) {
                    if (pei.debit in 30..119) {
                        return true
                    }
                }
                return false
            }

            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> false
            CodeSdis.SDIS_21 -> pei.debit != null && pei.debit in 30..59
            CodeSdis.SDIS_38 -> pei.debit != null && pei.debit in 15..29
            CodeSdis.SDIS_39 -> pei.debit != null && (pei.debit in 30..59)
            CodeSdis.SDIS_42 -> isDebitNonConformeDefault(pei)
            CodeSdis.SDIS_49 -> {
                if (isDiametre80(pei) &&
                    isNaturePI(pei) &&
                    (pei.debit in 15..29)
                ) {
                    return true
                } else if (isDiametre100(pei) &&
                    (pei.debit in 30..59)
                ) {
                    return true
                } else if (isDiametre150(pei) &&
                    (pei.debit in 30..119)
                ) {
                    return true
                }
                return false
            }

            CodeSdis.SDIS_53 -> pei.debit != null && (pei.debit in 16..59)
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> {
                if (isDiametre100(pei) &&
                    (pei.debit != null && pei.debit < 60)
                ) {
                    return true
                } else if (isDiametre150(pei) &&
                    (pei.debit != null && pei.debit < 120)
                ) {
                    return true
                }

                return false
            }

            CodeSdis.SDIS_66 -> pei.debit != null && pei.debit in 30..59
            CodeSdis.SDIS_71 -> {
                if (pei.diametreId == null || isDiametre100(pei) &&
                    (pei.debit != null && pei.debit in 30..59)
                ) {
                    return true
                } else if (isDiametre150(pei) &&
                    (pei.debit != null && pei.debit in 60..119)
                ) {
                    return true
                }

                return false
            }

            CodeSdis.SDIS_77 -> isDebitNonConformeDefault(pei)
            CodeSdis.SDIS_78 -> pei.debit != null && pei.debit in 1..44
            CodeSdis.SDIS_83 -> isDebitNonConformeDefault(pei)

            CodeSdis.SDIS_89 -> pei.debit != null && pei.debit in 15..29
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> {
                if (isDiametre80(pei)) {
                    if (pei.debit != null && pei.debit in 15..29) {
                        return true
                    }
                }
                if (isDiametre100(pei)) {
                    if (pei.debit != null && pei.debit in 30..59) {
                        return true
                    }
                }
                if (isDiametre150(pei)) {
                    if (pei.debit != null && pei.debit in 60..119) {
                        return true
                    }
                }

                return false
            }

            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> pei.debit != null && pei.debit < 30
        }
    }

    private fun isDebitTropEleve(pei: PeiForCalculDispoData): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01 -> false
            CodeSdis.SDIS_09 -> false
            CodeSdis.SDIS_14 -> false
            CodeSdis.SDIS_21 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> isDebitTropEleveDefault(pei)
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> pei.debit != null && pei.debit > 1000
            CodeSdis.SDIS_58 -> TODO()
            CodeSdis.SDIS_61 -> {
                if (isDiametre80(pei) &&
                    (pei.debit != null && pei.debit > 90)
                ) {
                    return true
                } else if (isDiametre100(pei) &&
                    (pei.debit != null && pei.debit > 130)
                ) {
                    return true
                } else if (isDiametre150(pei) &&
                    (pei.debit != null && pei.debit > 180)
                ) {
                    return true
                }
                return false
            }

            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> isDebitTropEleveDefault(pei)
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> isDebitTropEleveDefault(pei)
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_973 -> TODO()
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> false
        }
    }

    /**
     * Enumération de toutes les "anomalies système" pouvant influer sur la disponibilité d'un PEI.
     *
     * La propriété "rendIndispo" permet de différentier le cas du "non conforme :
     * * rendIndispo == true --> PEI indisponible
     * * rendIndispo == false --> PEI non conforme
     */
    enum class TypePredicatDispo(val rendIndispo: Boolean) {
        PRESSION_INSUFF(true),
        PRESSION_NON_CONFORME(false),
        PRESSION_TROP_ELEVEE(true),

        PRESSION_DYN_INSUFF(true),
        PRESSION_DYN_NON_CONFORME(false),
        PRESSION_DYN_TROP_ELEVEE(true),

        DEBIT_INSUFF(true),
        DEBIT_NON_CONFORME(false),
        DEBIT_TROP_ELEVE(true),

        // Pour les PENA
        VOLUME_INSUFF(true),
        VOLUME_NON_CONFORME(false),
    }

    /**
     * Retourne TRUE si le diamètre (non null) est du 80
     */
    private fun isDiametre80(pei: PeiForCalculDispoData) =
        GlobalConstants.DIAMETRE_80 == ensureDiametre(pei).diametreCode

    /**
     * Retourne TRUE si le diamètre (non null) est du 150
     */
    private fun isDiametre150(pei: PeiForCalculDispoData) =
        GlobalConstants.DIAMETRE_150 == ensureDiametre(pei).diametreCode

    /**
     * Retourne TRUE si le diamètre (non null) est du 100
     */
    private fun isDiametre100(pei: PeiForCalculDispoData) =
        GlobalConstants.DIAMETRE_100 == ensureDiametre(pei).diametreCode

    /**
     * Retourne TRUE si le PEI est de nature 'PI' ou 'BI'
     */
    private fun isNaturePIBI(pei: PeiForCalculDispoData) =
        listOf(GlobalConstants.NATURE_PI, GlobalConstants.NATURE_BI).contains(ensureNature(pei).natureCode)

    /**
     * Retourne TRUE si le PEI est de nature 'PI'
     */
    private fun isNaturePI(pei: PeiForCalculDispoData) =
        GlobalConstants.NATURE_PI == ensureNature(pei).natureCode

    /**
     * Règle par défaut pour savoir si la pression est considérée comme insuffisante
     */
    private fun isPressionInsuffisanteDefault(pei: PeiForCalculDispoData) =
        pei.pression == null || pei.pression < 1

    /**
     * Règle par défaut pour savoir si la pression est considérée comme trop élevée
     */
    private fun isPressionTropEleveeDefault(pei: PeiForCalculDispoData): Boolean {
        if (isNaturePIBI(pei)) {
            if (pei.pression != null && pei.pression > 16) {
                return true
            }
        }

        return false
    }

    /**
     * Règle par défaut pour savoir si la pression dynamique est considérée comme insuffisante
     */
    private fun isPressionDynamiqueInsuffisanteDefault(pei: PeiForCalculDispoData) =
        pei.pressionDynamique != null && pei.pressionDynamique < 1

    /**
     * Règle par défaut pour savoir si la pression dynamique est considérée comme trop élevée
     */
    private fun isPressionDynamiqueTropEleveeDefault(pei: PeiForCalculDispoData): Boolean {
        if (isNaturePIBI(pei)) {
            if (pei.pressionDynamique != null && pei.pressionDynamique > 16) {
                return true
            }
        }

        return false
    }

    /**
     * Règle par défaut pour savoir si le débit est considéré comme insuffisant
     */
    private fun isDebitInsuffisantDefault(pei: PeiForCalculDispoData): Boolean {
        if (listOf(GlobalConstants.DIAMETRE_70, GlobalConstants.DIAMETRE_80, GlobalConstants.DIAMETRE_100).contains(ensureDiametre(pei).diametreCode)) {
            return pei.debit != null && pei.debit in 0..29
        }

        return false
    }

    /**
     * Règle par défaut pour savoir si le débit est considéré comme non conforme
     */
    private fun isDebitNonConformeDefault(pei: PeiForCalculDispoData): Boolean {
        if (isDiametre100(pei)) {
            return pei.debit != null && pei.debit in 30..59
        }
        if (isDiametre150(pei)) {
            return pei.debit != null && pei.debit in 60..119
        }

        return false
    }

    /**
     * Règle par défaut pour savoir si le débit est considéré comme trop élevé
     */
    private fun isDebitTropEleveDefault(pei: PeiForCalculDispoData): Boolean {
        if (listOf(GlobalConstants.DIAMETRE_70, GlobalConstants.DIAMETRE_80).contains(ensureDiametre(pei).diametreCode)) {
            return pei.debit != null && pei.debit > 90
        }
        if (isDiametre100(pei)) {
            return pei.debit != null && pei.debit > 130
        }
        if (isDiametre150(pei)) {
            return pei.debit != null && pei.debit > 150
        }

        return false
    }
}
