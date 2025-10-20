package remocra.usecase.pei

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.data.PeiForCalculDispoData
import remocra.data.enums.CodeSdis
import remocra.db.AnomalieRepository
import remocra.db.CalculDispoRepository
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.PoidsAnomalieRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.Reservoir
import remocra.usecase.AbstractUseCase
import java.util.UUID

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

    @Inject
    private lateinit var anomalieRepository: AnomalieRepository

    @Inject
    private lateinit var indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository

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

    private fun checkReservoirId(pei: PeiForCalculDispoData) {
        if (pei.reservoirId == null) {
            throw IllegalArgumentException("Pas de réservoir pour le calcul de dispo")
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
     * Garantit que l'objet PEI a bien son objet *reservoir* chargé, soit parce qu'il l'est déjà, soit en le faisant au travers du *reservoirId*
     * Modifie le paramètre d'entrée *pei* pour éviter tout appel ultérieur
     * @return Reservoir
     */
    private fun ensureReservoir(pei: PeiForCalculDispoData): Reservoir {
        checkReservoirId(pei)
        if (pei.reservoir == null) {
            pei.reservoir = dataCacheProvider.getReservoirs()[pei.reservoirId!!] as Reservoir
        }
        return pei.reservoir!!
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
        deleteAnomaliesDebitPression(pei)
        val anomaliesDebitPression = computeAnomaliesDebitPression(pei)
        insertAnomaliesDebitPression(pei, anomaliesDebitPression)

        // Si le PEI a une IT en cours, il est indispo de toute façon
        if (hasIndispoTemporaires(pei)) {
            return Disponibilite.INDISPONIBLE
        }

        // on a besoin de la dernière visite :
        val lastVisite = visiteRepository.getLastVisite(pei.peiId)
        val allAnomalies = anomalieRepository.getAllById()
        val anomaliesDerniereVisite: List<Anomalie> = lastVisite?.let { visiteRepository.getAnomaliesFromVisite(lastVisite.visiteId).map { allAnomalies[it]!! } }
            ?: listOf()

        // On construit un set contenant toutes les anomalies possibles
        val setGlobalAnomalies: MutableSet<Anomalie> = mutableSetOf()
        if (anomaliesDebitPression.isNotEmpty()) {
            setGlobalAnomalies.addAll(anomaliesDebitPression.map { allAnomalies[it]!! })
        }
        if (anomaliesDerniereVisite.isNotEmpty()) {
            setGlobalAnomalies.addAll(anomaliesDerniereVisite)
        }

        if (setGlobalAnomalies.isNotEmpty()) {
            // On va chercher les poids des anomalies concernées
            val poidsAnomalies = poidsAnomalieRepository.getPoidsAnomalies(setGlobalAnomalies.map { it.anomalieId }, pei.peiNatureId, lastVisite?.visiteTypeVisite)

            // On calcule le poids total, comme étant la somme des poids de chaque anomalie présente sur le PEI, quelle que soit sa provenance
            val note = setGlobalAnomalies.mapNotNull { ano ->
                ano.anomaliePoidsAnomalieSystemeValIndispoTerrestre
                    ?: poidsAnomalies.find { poidsAno -> poidsAno.poidsAnomalieAnomalieId == ano.anomalieId }?.poidsAnomalieValIndispoTerrestre
            }.reduceOrNull { acc, next -> acc.plus(next) } ?: 0

            if (note >= 5) {
                return Disponibilite.INDISPONIBLE
            }
            // Sinon, il est peut-être non conforme
            if (setGlobalAnomalies.any { it.anomalieRendNonConforme }) {
                return Disponibilite.NON_CONFORME
            }
        }
        // Le PEI est disponible, on n'a rien à faire de particulier
        return Disponibilite.DISPONIBLE
    }

    /**
     * Retourne VRAI si le PEI a au moins une IT en cours au moment de la requête, FAUX sinon
     */
    private fun hasIndispoTemporaires(pei: PeiForCalculDispoData): Boolean {
        return indisponibiliteTemporaireRepository.hasPeiIndisponibiliteTemporaire(pei.peiId)
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
            CodeSdis.SDIS_22 -> pei.nature!!.natureCode == GlobalConstants.NATURE_PENA_ETUDE // Si PEI en étude, alors indispo
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> {
                if (pei.penaCapacite != null && pei.penaCapacite < 60) {
                    return true
                }
                if (
                    pei.nature!!.natureCode == GlobalConstants.NATURE_PEA &&
                    (pei.penaCapacite == null || pei.penaCapacite < 60)
                ) {
                    return true
                }

                return false
            }

            CodeSdis.SDIS_42 -> pei.penaCapacite == null || pei.penaCapacite < 2
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> pei.penaCapacite != null && pei.penaCapacite < 15
            CodeSdis.SDIS_59 ->
                if (isNatureConcerned(pei)) {
                    pei.penaCapacite != null && pei.penaCapacite < 30
                } else {
                    false
                }
            CodeSdis.SDIS_61 -> pei.penaCapacite != null && pei.penaCapacite < 30
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> false
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> false
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_971 -> pei.penaCapacite == null || pei.penaCapacite < 60
            CodeSdis.SDIS_973 -> false
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
            CodeSdis.SDIS_22 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> pei.penaCapacite != null && pei.penaCapacite in 60..119
            CodeSdis.SDIS_42 -> pei.penaCapaciteIncertaine == true || pei.penaCapacite != null && pei.penaCapacite < 30
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> pei.penaCapacite != null && pei.penaCapacite in 16..29
            CodeSdis.SDIS_59 -> false
            CodeSdis.SDIS_61 -> false
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> false
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> false
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_971 -> pei.penaCapacite != null && pei.penaCapacite in 60..119
            CodeSdis.SDIS_973 -> false
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
            CodeSdis.SDIS_22 -> pei.pression == null || pei.pression < 0.95 || pei.nature!!.natureCode == GlobalConstants.NATURE_PIBI_ETUDE // Si PEI en étude, alors indispo
            CodeSdis.SDIS_38 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_39 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_42 -> false
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_58 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_59 ->
                if (isNatureConcerned(pei)) {
                    pei.pression == null || pei.pression < 0.95
                } else {
                    false
                }
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
            CodeSdis.SDIS_971 -> isPressionInsuffisanteDefault(pei)
            CodeSdis.SDIS_973 -> isPressionInsuffisanteDefault(pei)
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
            CodeSdis.SDIS_22 -> pei.pression != null && pei.pression in 0.95..<1.0
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> false
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> pei.pression != null && pei.pression > 8
            CodeSdis.SDIS_59 ->
                if (isNatureConcerned(pei)) {
                    pei.pression != null && pei.pression in 0.95..<1.0
                } else {
                    false
                }
            CodeSdis.SDIS_61 -> false
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> false
            CodeSdis.SDIS_78 -> pei.pression != null && pei.pression > 0 && pei.pression < 1
            CodeSdis.SDIS_83 -> false
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_971 -> false
            CodeSdis.SDIS_973 -> false
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
            CodeSdis.SDIS_22 -> pei.pression != null && pei.pression > 6
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> isPressionTropEleveeDefault(pei)
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> pei.pression != null && pei.pression > 14
            CodeSdis.SDIS_58 -> false
            CodeSdis.SDIS_59 -> false
            CodeSdis.SDIS_61 -> pei.pression != null && pei.pression > 8
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> false
            CodeSdis.SDIS_77 -> isPressionTropEleveeDefault(pei)
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> isPressionTropEleveeDefault(pei)
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_971 -> pei.pression != null && pei.pression >= 7
            CodeSdis.SDIS_973 -> false
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
            CodeSdis.SDIS_22 -> pei.pressionDynamique == null || pei.pressionDynamique < 0.95 || pei.nature!!.natureCode == GlobalConstants.NATURE_PIBI_ETUDE // Si PEI en étude, alors indispo
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> {
                if (isNaturePIBI(pei)) {
                    if (pei.pressionDynamique != null && pei.pressionDynamique < 0.6) {
                        return true
                    }
                }

                return false
            }

            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> pei.pressionDynamique == null || pei.pressionDynamique < 1
            CodeSdis.SDIS_59 -> false
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
            CodeSdis.SDIS_971 -> isPressionDynamiqueInsuffisanteDefault(pei)
            CodeSdis.SDIS_973 -> pei.pressionDynamique == null
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
            CodeSdis.SDIS_22 -> pei.pressionDynamique != null && pei.pressionDynamique in 0.95..<1.0
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> pei.pressionDynamique != null && pei.pressionDynamique >= 0.6 && pei.pressionDynamique < 1
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> false
            CodeSdis.SDIS_59 -> false
            CodeSdis.SDIS_61 -> pei.pressionDynamique != null && pei.pressionDynamique < 1
            CodeSdis.SDIS_66 -> false
            CodeSdis.SDIS_71 -> pei.pressionDynamique != null && pei.pressionDynamique >= 1 && pei.pressionDynamique < 2
            CodeSdis.SDIS_77 -> false
            CodeSdis.SDIS_78 -> pei.pressionDynamique != null && pei.pressionDynamique > 0 && pei.pressionDynamique < 1
            CodeSdis.SDIS_83 -> false
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_971 -> false
            CodeSdis.SDIS_973 -> false
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
            CodeSdis.SDIS_22 -> pei.pressionDynamique != null && pei.pressionDynamique > 6
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> isPressionDynamiqueTropEleveeDefault(pei)
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> false
            CodeSdis.SDIS_58 -> pei.pressionDynamique != null && pei.pressionDynamique > 8
            CodeSdis.SDIS_59 -> false
            CodeSdis.SDIS_61 ->
                pei.pression != null && pei.pressionDynamique != null &&
                    pei.pression >= 1 && pei.pression <= 8 &&
                    pei.pressionDynamique > 8

            CodeSdis.SDIS_66 -> false
            // TODO choix à faire (avec le SDIS) : seuil à 10 ou à 16 ?
            CodeSdis.SDIS_71 -> pei.pressionDynamique != null && pei.pressionDynamique > 16
            CodeSdis.SDIS_77 -> isPressionDynamiqueTropEleveeDefault(pei)
            CodeSdis.SDIS_78 -> false
            CodeSdis.SDIS_83 -> isPressionDynamiqueTropEleveeDefault(pei)
            CodeSdis.SDIS_89 -> false
            CodeSdis.SDIS_91 -> false
            CodeSdis.SDIS_95 -> false
            CodeSdis.SDIS_971 -> pei.pressionDynamique != null && pei.pressionDynamique >= 6
            CodeSdis.SDIS_973 -> false
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
            CodeSdis.SDIS_22 -> {
                return if (pei.nature!!.natureCode == GlobalConstants.NATURE_PIBI_ETUDE) { // Si PEI en étude, alors indispo
                    true
                } else if (pei.diametreId == null) {
                    // Si le diamètre est null, on applique la règle ayant le plus grand handicap opérationnel = Ø 80
                    pei.debit == null || pei.debit < 27
                } else if (isDiametre80(pei)) {
                    pei.debit == null || pei.debit < 27
                } else if (isDiametre100(pei)) {
                    pei.debit == null || pei.debit < 30
                } else if (isDiametre150(pei)) {
                    pei.debit == null || pei.debit < 60
                } else {
                    // Si le diamètre est autre que null/80/100/150, on applique la règle ayant le plus grand handicap opérationnel = Ø 80
                    pei.debit == null || pei.debit < 27
                }
            }
            CodeSdis.SDIS_38 -> pei.debit == null || pei.debit < 15
            CodeSdis.SDIS_39 -> (pei.debit == null || pei.debit <= 29)
            CodeSdis.SDIS_42 -> pei.debit == null
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
            CodeSdis.SDIS_58 -> pei.debit == null || pei.debit < 15
            CodeSdis.SDIS_59 ->
                if (isNatureConcerned(pei)) {
                    if (pei.debit == null) {
                        true
                    } else if (pei.debit < 15) {
                        pei.diametreId == null || isDiametre80(pei) || isDiametre100(pei)
                    } else if (pei.debit < 30) {
                        isDiametre150(pei)
                    } else {
                        false
                    }
                } else {
                    false
                }
            CodeSdis.SDIS_61 -> {
                if (pei.pressionDynamique == null || pei.pressionDynamique <= 8) {
                    if (pei.pression != null && pei.pression >= 1 && pei.pression <= 8) {
                        return pei.debit == null || pei.debit < 30
                    }
                }
                if (pei.debit == null) {
                    return pei.pressionDynamique == null || pei.pressionDynamique < 1
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
                if (pei.diametreId == null) {
                    return false
                }
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
            CodeSdis.SDIS_971 -> pei.debit == null || pei.debit < 30
            CodeSdis.SDIS_973 -> pei.debit == null || pei.debit < 30
            CodeSdis.BSPP -> pei.debit != null && pei.debit < 60
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
            CodeSdis.SDIS_22 -> {
                return if (pei.diametreId == null) {
                    // Si le diamètre est null, on applique la règle ayant le plus grand handicap opérationnel = Ø 80
                    pei.debit != null && pei.debit in 27..<30
                } else if (isDiametre80(pei)) {
                    pei.debit != null && pei.debit in 27..<30
                } else if (isDiametre100(pei)) {
                    pei.debit != null && pei.debit in 30..<54
                } else if (isDiametre150(pei)) {
                    pei.debit != null && pei.debit in 60..<105
                } else {
                    // Si le diamètre est autre que null/80/100/150, on applique la règle ayant le plus grand handicap opérationnel = Ø 80
                    pei.debit != null && pei.debit in 27..<30
                }
            }
            CodeSdis.SDIS_38 -> pei.debit != null && pei.debit in 15..29
            CodeSdis.SDIS_39 -> pei.debit != null && (pei.debit in 30..59)
            CodeSdis.SDIS_42 -> pei.debit != null && pei.debit < 30
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
            CodeSdis.SDIS_58 -> pei.debit != null && (pei.debit in 15..29)
            CodeSdis.SDIS_59 ->
                if (isNatureConcerned(pei)) {
                    when (pei.debit) {
                        in 15..<30 ->
                            pei.diametreId == null || isDiametre80(pei) || isDiametre100(pei)
                        in 30..<60 ->
                            isDiametre150(pei)
                        else ->
                            false
                    }
                } else {
                    false
                }
            CodeSdis.SDIS_61 -> {
                if (isDiametre100(pei) &&
                    (
                        pei.pression != null && pei.pression >= 1 && pei.pression <= 8 &&
                            (pei.pressionDynamique == null || pei.pressionDynamique < 1)
                        ) &&
                    pei.debit != null && pei.debit >= 30 && pei.debit < 60
                ) {
                    return true
                } else if (isDiametre150(pei) &&
                    (
                        pei.pression != null && pei.pression >= 1 && pei.pression <= 8 &&
                            (pei.pressionDynamique == null || pei.pressionDynamique < 1)
                        ) &&
                    pei.debit != null && pei.debit >= 30 && pei.debit < 120
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
                if (pei.diametre == null) {
                    return false
                }
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
            CodeSdis.SDIS_971 -> {
                return if (pei.diametreId == null) {
                    false
                } else if (isDiametre100(pei)) {
                    pei.debit != null && pei.debit in 30..59
                } else if (isDiametre150(pei)) {
                    pei.debit != null && pei.debit in 30..119
                } else {
                    false
                }
            }
            CodeSdis.SDIS_973 -> pei.debit != null && pei.debit >= 30 && pei.debit < 60
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
            CodeSdis.SDIS_22 -> false
            CodeSdis.SDIS_38 -> false
            CodeSdis.SDIS_39 -> false
            CodeSdis.SDIS_42 -> pei.debit != null && pei.debit > 500
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_53 -> pei.debit != null && pei.debit > 1000
            CodeSdis.SDIS_58 -> false
            CodeSdis.SDIS_59 -> false
            CodeSdis.SDIS_61 -> {
                if (isDiametre80(pei) && isPressionsBetween1And8(pei) &&
                    (pei.debit != null && pei.debit >= 90)
                ) {
                    return true
                } else if (isDiametre100(pei) && isPressionsBetween1And8(pei) &&
                    (pei.debit != null && pei.debit > 150)
                ) {
                    return true
                } else if (isDiametre150(pei) && isPressionsBetween1And8(pei) &&
                    (pei.debit != null && pei.debit > 270)
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
            CodeSdis.SDIS_971 -> false
            CodeSdis.SDIS_973 -> false
            CodeSdis.BSPP -> false
            CodeSdis.SDMIS -> false
        }
    }

    /**
     * Enumération de toutes les "anomalies système" pouvant influer sur la disponibilité d'un PEI.
     */
    enum class TypePredicatDispo() {
        PRESSION_INSUFF,
        PRESSION_NON_CONFORME,
        PRESSION_TROP_ELEVEE,

        PRESSION_DYN_INSUFF,
        PRESSION_DYN_NON_CONFORME,
        PRESSION_DYN_TROP_ELEVEE,

        DEBIT_INSUFF,
        DEBIT_NON_CONFORME,
        DEBIT_TROP_ELEVE,

        // Pour les PENA
        VOLUME_INSUFF,
        VOLUME_NON_CONFORME,
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
     * Retourne TRUE si le PEI est d'une nature différente des suivantes (SDIS 59) :
     * 'ALI_PR', 'ALI_COND', 'REFL_COND', 'REFL_PR'
     */
    private fun isNatureConcerned(pei: PeiForCalculDispoData): Boolean {
        // SDIS 59 -  Ces natures de PEI ne sont pas concernées par les anomalies automatiques
        return !listOf(GlobalConstants.NATURE_ALIMENTATION_POTEAU_RELAIS, GlobalConstants.NATURE_ALIMENTATION_DE_CONDUITE, GlobalConstants.NATURE_REFOULEMENT_DE_CONDUITE, GlobalConstants.NATURE_POTEAU_RELAIS).contains(ensureNature(pei).natureCode)
    }

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

    /**
     * Utilitaire retournant TRUE si les pressions statiques et dynamiques sont non nulles et comprises entre 1 et 8
     */
    private fun isPressionsBetween1And8(pei: PeiForCalculDispoData): Boolean {
        return pei.pression != null && pei.pression >= 1 && pei.pression <= 8 &&
            pei.pressionDynamique != null && pei.pressionDynamique >= 1 && pei.pressionDynamique <= 8
    }
}
