package remocra.usecase.pei

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.GlobalConstants.NATURE_DECI_ICPE
import remocra.GlobalConstants.NATURE_DECI_ICPE_CONVENTIONNE
import remocra.app.AppSettings
import remocra.data.PeiForNumerotationData
import remocra.data.enums.CodeSdis
import remocra.db.CommuneRepository
import remocra.db.GestionnaireRepository
import remocra.db.NumerotationRepository
import remocra.db.ZoneIntegrationRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Commune
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.db.jooq.remocra.tables.pojos.ZoneIntegration
import remocra.usecase.AbstractUseCase
import java.util.Locale
import java.util.UUID

/**
 * Constantes potentiellement globales
 */
private const val MAX_PEI_NUMERO_INTERNE = 99999
private const val NATURE_DECI_PRIVE = "PRIVE"
private const val NATURE_DECI_PUBLIC = "PUBLIC"
private const val NATURE_DECI_CONVENTIONNE = "CONVENTIONNE"

/**
 * SDIS 53 : codes INSEE des communes et zones spéciales
 */
private const val CODE_INSEE_LAVAL = "53130"
private const val CODE_INSEE_CHANGE = "53054"
private const val CODE_INSEE_LOUVERNE = "53140"

private const val SECTEUR_LAVAL_1 = "SECTEUR LAVAL 1"
private const val SECTEUR_LAVAL_2 = "SECTEUR LAVAL 2"
private const val SECTEUR_LAVAL_3 = "SECTEUR LAVAL 3"
private const val SECTEUR_LAVAL_4 = "SECTEUR LAVAL 4"
private const val SECTEUR_LAVAL_5 = "SECTEUR LAVAL 5"
private const val SECTEUR_LAVAL_6 = "SECTEUR LAVAL 6"
private const val SECTEUR_LAVAL_7 = "SECTEUR LAVAL 7"
private const val SECTEUR_LAVAL_8 = "SECTEUR LAVAL 8"
private const val SECTEUR_CHANGE_1 = "SECTEUR CHANGE 1"
private const val SECTEUR_CHANGE_2 = "SECTEUR CHANGE 2"
private const val SECTEUR_CHANGE_PRIVE = "SECTEUR CHANGE PRIVE"
private const val SECTEUR_CHANGE_RIVE_DROITE = "SECTEUR CHANGE RIVE DROITE"
private const val SECTEUR_CHANGE_RIVE_GAUCHE = "SECTEUR CHANGE RIVE GAUCHE"
private const val SECTEUR_LOUVERNE_2 = "SECTEUR LOUVERNE 2"

// SDIS 78
private const val CODE_DOMAINE_AUTOROUTE = "AUTOROUTE"

// SDIS 83
private const val NATURE_RESERVE_INCENDIE = "RI"

// Puisard (SDIS 58)
const val NATURE_PUISARD = "PU"

/**
 * Usecase permettant de gérer tous les cas de la numérotation d'un PEI. Le point de départ est le *codeSdis* défini dans les paramètres système, et injecté dans les *appSettings*.
 *
 * La numérotation s'appuie sur 2 concepts de base
 * * calcul du prochain numéro interne (*pei.pei_numero_interne*) avec différentes stratégies (bouchage de trous, on commence par la fin, ...)
 * * utilisation d'attributs supplémentaires pour construire le numéro à partir du numéro interne
 *
 * Les méthodes multi-SDIS sont volontairement nommées A, B, C de manière arbitraire ; il n'y a aucune cohérence entre le calcul du numéro complet et du numéro interne (un SDIS peut avoir un "calcul numéro 78" et un "calcul numéro interne A")
 *
 * En fonction de la volonté du SDIS, on aiguille vers une méthode déjà construite, ou on en fait une spécifique.
 * Ce useCase est stateless, et doit être utilisé par les services d'enregistrement d'un PEI au sein d'une transaction (sinon le nextNuméro peut être faussé !)
 *
 */
class NumerotationUseCase : AbstractUseCase() {
    @Inject
    private lateinit var appSettings: AppSettings

    @Inject
    private lateinit var numerotationRepository: NumerotationRepository

    @Inject
    private lateinit var communeRepository: CommuneRepository

    @Inject
    private lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    @Inject
    private lateinit var gestionnaireRepository: GestionnaireRepository

    /**
     * Point d'entrée pour calculer le numéro complet d'un PEI.
     * Prend en compte, sous forme précâblée, toutes les contraintes de chaque SDIS, au travers de méthodes dédiées.
     *
     * S'appuie sur le paramètre *codeSdis* pour savoir sur quelle instance on se trouve.
     *
     * /!\ *DOIT* être encapsulé dans une transaction de plus haut niveau pour éviter les collisions !
     *
     * @param pei: Classe [PeiForNumerotationData] dont on remplit les propriétés "qui nous intéressent" en fonction des cas
     */
    fun computeNumero(pei: PeiForNumerotationData): String {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01,
            CodeSdis.SDIS_61,
            -> computeNumeroMethodeA(pei)

            CodeSdis.SDIS_09 -> computeNumero09(pei)
            CodeSdis.SDIS_16 -> TODO()
            CodeSdis.SDIS_21 -> computeNumero21(pei)
            CodeSdis.SDIS_22 -> computeNumero22(pei)
            CodeSdis.SDIS_38 -> computeNumero38(pei)
            CodeSdis.SDIS_39 -> computeNumero39(pei)
            CodeSdis.SDIS_42,
            CodeSdis.SDIS_89,
            CodeSdis.SDMIS,
            -> computeNumeroMethodeB(pei)

            CodeSdis.SDIS_49 -> computeNumero49(pei)
            CodeSdis.SDIS_53 -> computeNumero53(pei)
            CodeSdis.SDIS_58 -> computeNumero58(pei)
            CodeSdis.SDIS_59 -> computeNumero59(pei)
            CodeSdis.SDIS_62 -> TODO()
            CodeSdis.SDIS_66,
            -> computeNumero66(pei)
            CodeSdis.SDIS_71,
            CodeSdis.SDIS_83,
            -> computeNumeroMethodeC(pei)

            CodeSdis.SDIS_77,
            CodeSdis.BSPP,
            -> computeNumeroMethodeD(pei)

            CodeSdis.SDIS_78 -> computeNumero78(pei)
            CodeSdis.SDIS_95 -> computeNumero95(pei)
            CodeSdis.SDIS_971 -> computeNumero971(pei)
            CodeSdis.SDIS_973 -> computeNumero973(pei)
        }
    }

    /**
     * Retourne le prochain numéro interne disponible pour le PEI passé en paramètre ;<br />
     * L'objet passé devra contenir tous les paramètres utiles au calcul pour le SDIS concerné (commune, type, ...)
     * @param pei L'objet PEI contenant les infos utiles au calcul
     * @return Int le numéro interne à utiliser
     */
    fun computeNumeroInterne(pei: PeiForNumerotationData): Int {
        // Retour du numéro interne s'il existe
        if (pei.peiNumeroInterne != null && pei.peiId != null) {
            return pei.peiNumeroInterne!!
        }
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01,
            CodeSdis.SDIS_22,
            CodeSdis.SDIS_38,
            CodeSdis.SDIS_42,
            CodeSdis.SDIS_61,
            CodeSdis.SDIS_66,
            CodeSdis.SDIS_78,
            CodeSdis.SDIS_971,
            CodeSdis.BSPP,
            CodeSdis.SDMIS,
            -> computeNumeroInterneMethodeA(pei)
            CodeSdis.SDIS_09,
            CodeSdis.SDIS_21,
            CodeSdis.SDIS_77,
            CodeSdis.SDIS_89,
            CodeSdis.SDIS_973,
            -> computeNumeroInterneMethodeB(pei)
            CodeSdis.SDIS_16 -> TODO()
            CodeSdis.SDIS_39 -> computeNumeroInterneMethodeC(pei)
            CodeSdis.SDIS_49 -> computeNumeroInterne49()
            CodeSdis.SDIS_53 -> computeNumeroInterne53(pei)
            CodeSdis.SDIS_58 -> computeNumeroInterne58(pei)
            CodeSdis.SDIS_59 -> computeNumeroInterne59(pei)
            CodeSdis.SDIS_62 -> TODO()
            CodeSdis.SDIS_71,
            CodeSdis.SDIS_83,
            -> computeNumeroInterne83(pei)
            CodeSdis.SDIS_95 -> computeNumeroInterne95(pei)
        }
    }

    private fun computeNumeroInterne58(pei: PeiForNumerotationData): Int {
        checkCommuneId(pei)

        val seed: Int
        val maxValue: Int

        val typePei = pei.nature!!.natureTypePei

        if (typePei == TypePei.PIBI) {
            // On part de 1, on finit à 599
            seed = 1
            maxValue = 599
        } else {
            if (pei.nature.natureCode == NATURE_PUISARD) {
                seed = 600
                maxValue = 799
            } else {
                seed = 800
                maxValue = 999
            }
        }

        val listPeiNumeroInterne = numerotationRepository.getListPeiNumeroInterne(
            typePei = typePei,
            peiNatureId = pei.nature.natureId,
            peiCommuneId = if (pei.peiZoneSpecialeId == null) pei.peiCommuneId else null,
            peiZoneSpecialeId = pei.peiZoneSpecialeId,
            peiNatureDeciId = null,
        )

        return listPeiNumeroInterne.getNextNumeroInterneWhile(seed, maxValue)
    }

    /**
     * numéro interne en fonction de la commune ou zone speciale ET nature DECI max +1 => pas de remplissage
     *
     */
    private fun computeNumeroInterne95(pei: PeiForNumerotationData): Int {
        checkCommuneId(pei)

        val seed: Int
        val maxValue: Int

        val listPeiNumeroInterne = numerotationRepository.getListPeiNumeroInterne(
            typePei = null,
            peiNatureId = null,
            peiCommuneId = if (pei.peiZoneSpecialeId == null) pei.peiCommuneId else null,
            peiZoneSpecialeId = pei.peiZoneSpecialeId,
            peiNatureDeciId = null,
        )
        if (isNatureDeciPrive(pei) || isNatureDeciConventionne(pei)) {
            // On part de 1000, on finit à 9999
            seed = 1000
            maxValue = 9999
        } else {
            // On part de 1, on finit à 999
            seed = 1
            maxValue = 999
        }
        return listPeiNumeroInterne.getNextNumeroInterneWhile(seed, maxValue)
    }

    /**
     * numéro interne en fonction de la nature et de la commune ou zone spéciale max +1 =>
     * pas de remplissage
     *
     */
    private fun computeNumeroInterne83(pei: PeiForNumerotationData): Int {
        checkNature(pei)

        val listPeiNumeroInterne = numerotationRepository.getListPeiNumeroInterne(
            typePei = pei.nature!!.natureTypePei,
            peiNatureId = null,
            peiCommuneId = if (pei.peiZoneSpecialeId == null) pei.peiCommuneId else null,
            peiZoneSpecialeId = pei.peiZoneSpecialeId,
            peiNatureDeciId = null,
        )

        return if (listPeiNumeroInterne.isEmpty()) MAX_PEI_NUMERO_INTERNE else listPeiNumeroInterne.first() - 1
    }

    private fun computeNumeroInterneMethodeA(pei: PeiForNumerotationData): Int {
        checkCommuneId(pei)

        var numInterne: Int
        try {
            // Incrementation automatique de numero interne
            numInterne = numerotationRepository.getNextPeiNumeroInterneMethodeA(pei.peiCommuneId!!)

            // On prend le suivant
            numInterne++
        } catch (e: Exception) {
            numInterne = MAX_PEI_NUMERO_INTERNE
        }
        return numInterne
    }

    /** Premier numéro interne disponible max +1 => pas de remplissage  */
    private fun computeNumeroInterne49(): Int {
        return numerotationRepository.getMaxPeiNumeroInterne()
    }

    /**
     * numéro interne en fonction de la commune ou zone spéciale
     *
     */
    private fun computeNumeroInterneMethodeB(pei: PeiForNumerotationData): Int {
        checkNature(pei)

        val listPeiNumeroInterne = numerotationRepository.getListPeiNumeroInterne(
            typePei = pei.nature!!.natureTypePei,
            peiNatureId = null,
            peiCommuneId = if (pei.peiZoneSpecialeId == null) pei.peiCommuneId else null,
            peiZoneSpecialeId = pei.peiZoneSpecialeId,
            peiNatureDeciId = null,
        )

        val max = if (listPeiNumeroInterne.isEmpty()) MAX_PEI_NUMERO_INTERNE else listPeiNumeroInterne.last()
        return listPeiNumeroInterne.getNextNumeroInterne(1, max)
    }

    /**
     * Numéro interne en fonction de la nature du PEI et de la commune
     *
     */
    private fun computeNumeroInterneMethodeC(pei: PeiForNumerotationData): Int {
        checkCommuneId(pei)
        checkNature(pei)

        val listPei = numerotationRepository.getListPeiNumeroInterneMethodeC(peiCommuneId = pei.peiCommuneId!!, peiNatureId = pei.nature!!.natureId)
        val max = if (listPei.isEmpty()) MAX_PEI_NUMERO_INTERNE else listPei.last()

        return listPei.getNextNumeroInterne(1, max)
    }

    /**
     * Numéro interne en fonction de la nature du PEI, de la nature_deci, de la commune ou de la zone spéciale si
     * renseignée. Beaucoup de cas particuliers sur les zones spéciales.
     *
     */
    private fun computeNumeroInterne53(pei: PeiForNumerotationData): Int {
        checkCommuneId(pei)
        checkNature(pei)

        val defaultValue = MAX_PEI_NUMERO_INTERNE

        val commune = ensureCommune(pei)
        try {
            if (TypePei.PIBI == pei.nature!!.natureTypePei) {
                if (CODE_INSEE_LAVAL.equals(commune.communeCodeInsee, ignoreCase = true)) {
                    if (pei.peiZoneSpecialeId != null) {
                        val zoneSpeciale = zoneIntegrationRepository.getById(pei.peiZoneSpecialeId)

                        when (zoneSpeciale.zoneIntegrationCode) {
                            SECTEUR_LAVAL_1 -> {
                                return computeNumeroInterne53ZoneSpeciale(pei, 101, 199)
                            }

                            SECTEUR_LAVAL_2 -> {
                                return computeNumeroInterne53ZoneSpeciale(pei, 201, 299)
                            }

                            SECTEUR_LAVAL_3 -> {
                                return computeNumeroInterne53ZoneSpeciale(pei, 301, 399)
                            }

                            SECTEUR_LAVAL_4 -> {
                                return computeNumeroInterne53ZoneSpeciale(pei, 401, 499)
                            }

                            SECTEUR_LAVAL_5 -> {
                                return computeNumeroInterne53ZoneSpeciale(pei, 501, 599)
                            }

                            SECTEUR_LAVAL_6 -> {
                                var numInterne = computeNumeroInterne53ZoneSpeciale(pei, 601, 701)
                                if (numInterne == 700) {
                                    numInterne =
                                        computeNumeroInterne53ZoneSpeciale(pei, 6001, 6999) // reprise de la numérotation à partir de 6001 car plus de 100 PEI sur le secteur
                                }
                                return numInterne
                            }

                            SECTEUR_LAVAL_7 -> {
                                return computeNumeroInterne53ZoneSpeciale(pei, 701, 799)
                            }

                            SECTEUR_LAVAL_8 -> {
                                return computeNumeroInterne53ZoneSpeciale(pei, 801, 899)
                            }
                        }
                    }
                } else if (CODE_INSEE_CHANGE.equals(commune.communeCodeInsee, ignoreCase = true)) { // Changé
                    if (pei.peiZoneSpecialeId != null) {
                        val zoneSpeciale = zoneIntegrationRepository.getById(pei.peiZoneSpecialeId)
                        when (zoneSpeciale.zoneIntegrationCode) {
                            SECTEUR_CHANGE_1 -> {
                                return computeNumeroInterne53ZoneSpeciale(pei, 101, 199)
                            }

                            SECTEUR_CHANGE_2 -> {
                                return computeNumeroInterne53ZoneSpeciale(pei, 201, 299)
                            }

                            SECTEUR_CHANGE_PRIVE, SECTEUR_CHANGE_RIVE_DROITE, SECTEUR_CHANGE_RIVE_GAUCHE -> {
                                return computeNumeroInterne53Private(pei, true)
                            }
                        }
                    }
                } else if (CODE_INSEE_LOUVERNE.equals(commune.communeCodeInsee, ignoreCase = true)) { // Louverne
                    if (pei.peiZoneSpecialeId != null) {
                        val zoneSpeciale = zoneIntegrationRepository.getById(pei.peiZoneSpecialeId)
                        if (zoneSpeciale.zoneIntegrationCode.equals(SECTEUR_LOUVERNE_2, true)) {
                            return computeNumeroInterne53ZoneSpeciale(pei, 201, 299)
                        }
                    } else { // PIBI à Louverne mais hors zone spéciale
                        return computeNumeroInterne53Private(pei, false)
                    }
                } else { // PIBI hors communes spéciale
                    return computeNumeroInterne53Private(pei, false)
                }
            } else { // PENA
                return computeNumeroInterne53Private(pei, false)
            }
        } catch (e: java.lang.Exception) {
            return defaultValue
        }
        // Normalement impossible
        return defaultValue
    }

    /** numéro interne en fonction de la commune ou zone spéciale et de la nature DECI */
    private fun computeNumeroInterne59(pei: PeiForNumerotationData): Int {
        checkNature(pei)
        checkNatureDeci(pei)

        val listPeiNumeroInterne = numerotationRepository.getListPeiNumeroInterne(
            typePei = null,
            peiNatureId = null,
            peiCommuneId = if (pei.peiZoneSpecialeId == null) pei.peiCommuneId else null,
            peiZoneSpecialeId = null,
            peiNatureDeciId = pei.natureDeci!!.natureDeciId,
        )

        val max = if (listPeiNumeroInterne.isEmpty()) MAX_PEI_NUMERO_INTERNE else listPeiNumeroInterne.last()
        return listPeiNumeroInterne.getNextNumeroInterne(1, max)
    }

    /**
     * Garantit que l'objet PEI a bien son objet *commune* chargé, soit parce qu'il l'est déjà, soit en le faisant au travers du *peiCommuneId*
     * Modifie le paramètre d'entrée *pei* pour éviter tout appel ultérieur
     * @return Commune
     */
    private fun ensureCommune(pei: PeiForNumerotationData): Commune {
        if (pei.commune == null) {
            pei.commune = communeRepository.getById(pei.peiCommuneId!!)
        }
        return pei.commune!!
    }

    /**
     * Garantit que l'objet PEI a bien son objet *commune* chargé, soit parce qu'il l'est déjà, soit en le faisant au travers du *peiGestionnaireId*
     * Modifie le paramètre d'entrée *pei* pour éviter tout appel ultérieur
     * @return Gestionnaire
     */
    private fun ensureGestionnaire(pei: PeiForNumerotationData): Gestionnaire {
        if (pei.gestionnaire == null) {
            pei.gestionnaire = gestionnaireRepository.getById(pei.gestionnaireId!!)
        }
        return pei.gestionnaire!!
    }

    /**
     * Garantit que l'objet PEI a bien son objet *zoneSpeciale* chargé, soit parce qu'il l'est déjà, soit en le faisant au travers du *peiZoneSpecialeId*
     * Modifie le paramètre d'entrée *pei* pour éviter tout appel ultérieur
     * @return ZoneIntegration
     */
    private fun ensureZoneSpeciale(pei: PeiForNumerotationData): ZoneIntegration {
        if (pei.zoneSpeciale == null) {
            pei.zoneSpeciale = zoneIntegrationRepository.getById(pei.peiZoneSpecialeId!!)
        }
        return pei.zoneSpeciale!!
    }

    private fun computeNumeroInterne53ZoneSpeciale(
        pei: PeiForNumerotationData,
        startRange: Int,
        stopRange: Int,
    ): Int {
        return if (isNatureDeciPrive(pei)) {
            computeNumeroInterne53Private(pei, false)
        } else {
            computeNumeroInterne53Public(pei, startRange, stopRange)
        }
    }

    private fun computeNumeroInterne53Public(pei: PeiForNumerotationData, startRange: Int, stopRange: Int): Int {
        val listPeiNumeroInterne = numerotationRepository.getListPeiNumeroInterne(pei.nature!!.natureTypePei, pei.nature.natureId, pei.peiCommuneId!!, pei.peiZoneSpecialeId!!, pei.natureDeci?.natureDeciId)

        return listPeiNumeroInterne.getNextNumeroInterneWhile(startRange, stopRange)
    }

    private fun computeNumeroInterne53Private(pei: PeiForNumerotationData, ignoreDECI: Boolean): Int {
        val listPeiNumeroInterne: Collection<Int> = if (TypePei.PIBI == pei.nature!!.natureTypePei) {
            if (ignoreDECI) { // si on ignore la nature DECI ; Cas des secteurs Changé Rive Gauche/Droite et Changé Privé
                numerotationRepository.getListPeiNumeroInterne(pei.nature.natureTypePei, null, pei.peiCommuneId!!, pei.peiZoneSpecialeId, null)
            } else {
                numerotationRepository.getListPeiNumeroInterne(pei.nature.natureTypePei, null, pei.peiCommuneId!!, pei.peiZoneSpecialeId, pei.natureDeci!!.natureDeciId)
            }
        } else {
            numerotationRepository.getListPeiNumeroInterne(pei.nature.natureTypePei, null, pei.peiCommuneId!!, null, null)
        }

        val max = if (listPeiNumeroInterne.isEmpty()) MAX_PEI_NUMERO_INTERNE else listPeiNumeroInterne.last()
        return listPeiNumeroInterne.getNextNumeroInterne(1, max)
    }

    /**
     * <code insee commune>_<numéro interne>
     * avec le numéro interne composé de 3 chiffres
     * Exemple : 01346_095
     *
     */
    private fun computeNumeroMethodeA(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)
        val commune = ensureCommune(pei)
        return commune.communeCodeInsee + "_" + "%03d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * <code nature><code insee commune>.<numéro interne>
     * avec le numéro interne sur 5 chiffres
     * *avec le code nature égal à P, B, A ou N*
     * avec un point (.) entre insee et num_interne
     * Exemple : P39473.00001, A39199.21547
     *
     */
    private fun computeNumero39(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)
        checkNature(pei)

        val commune = ensureCommune(pei)
        // Si c'est un PEA, on le préfixe d'un A, sinon on prend la nature
        val prefixe = if (pei.nature!!.natureCode == GlobalConstants.NATURE_PEA) "A" else pei.nature.natureCode
        return prefixe + commune.communeCodeInsee + "." + "%05d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    private fun computeNumero53(pei: PeiForNumerotationData): String {
        checkNatureDeci(pei)
        var suffixe = ""

        val natureDeci = pei.natureDeci!!
        if (TypePei.PIBI == pei.nature!!.natureTypePei) {
            if (CODE_INSEE_LAVAL.equals(ensureCommune(pei).communeCodeInsee, ignoreCase = true)) {
                if (pei.peiZoneSpecialeId != null && NATURE_DECI_PRIVE.equals(natureDeci.natureDeciCode, ignoreCase = true)) {
                    when (ensureZoneSpeciale(pei).zoneIntegrationCode) {
                        SECTEUR_LAVAL_1 -> suffixe = "S1"
                        SECTEUR_LAVAL_2 -> suffixe = "S2"
                        SECTEUR_LAVAL_3 -> suffixe = "S3"
                        SECTEUR_LAVAL_4 -> suffixe = "S4"
                        SECTEUR_LAVAL_5 -> suffixe = "S5"
                        SECTEUR_LAVAL_6 -> suffixe = "S6"
                        SECTEUR_LAVAL_7 -> suffixe = "S7"
                        SECTEUR_LAVAL_8 -> suffixe = "S8"
                    }
                }
            } else if (CODE_INSEE_CHANGE.equals(ensureCommune(pei).communeCodeInsee, ignoreCase = true)) {
                if (pei.peiZoneSpecialeId != null) {
                    when (ensureZoneSpeciale(pei).zoneIntegrationCode) {
                        SECTEUR_CHANGE_RIVE_DROITE -> suffixe = "D"
                        SECTEUR_CHANGE_RIVE_GAUCHE -> suffixe = "G"
                        SECTEUR_CHANGE_1 -> if (NATURE_DECI_PRIVE.equals(natureDeci.natureDeciCode, ignoreCase = true)) {
                            suffixe = "S1"
                        }

                        SECTEUR_CHANGE_2 -> if (NATURE_DECI_PRIVE.equals(natureDeci.natureDeciCode, ignoreCase = true)) {
                            suffixe = "S2"
                        }

                        SECTEUR_CHANGE_PRIVE -> if (NATURE_DECI_PRIVE.equals(natureDeci.natureDeciCode, ignoreCase = true)) {
                            suffixe = "P"
                        }
                    }
                }
            } else if (NATURE_DECI_PRIVE.equals(pei.natureDeci.natureDeciCode, ignoreCase = true)) {
                suffixe = "P"
            }
        }

        // Construction Numéro PEI
        val sb = StringBuilder()
        sb.append(pei.nature.natureCode)
        sb.append(ensureCommune(pei).communeCodeInsee)
        if (pei.peiNumeroInterne.toString().length == 4) {
            sb.append("%04d".format(Locale.getDefault(), pei.peiNumeroInterne))
        } else {
            sb.append("%03d".format(Locale.getDefault(), pei.peiNumeroInterne))
        }
        sb.append(suffixe)
        return sb.toString()
    }

    /**
     * <code de la zone spéciale OU code insee commune><numéro interne>
     * sans espace
     * sur 3 chiffres
     * Exemple : 09122012
     *
     */
    private fun computeNumero09(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)

        val commune = ensureCommune(pei)
        val codeZoneSpeciale = if (pei.peiZoneSpecialeId != null) ensureZoneSpeciale(pei).zoneIntegrationCode else null

        return (codeZoneSpeciale ?: commune.communeCodeInsee) + "%03d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * <insee> <numero_interne> avec un espace dans insee et le numero interne sur 4 chiffres
     * Exemple  : 14 118 0001
     *
     */
    private fun computeNumero14(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)

        val insee = ensureCommune(pei).communeCodeInsee
        return insee.substring(0, 2) + " " + insee.substring(2, 5) + " " + "%04d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * <code insee commune>-<numéro interne>
     * avec insee sur 5 chiffres
     * avec num-interne sur 4 chiffres
     * et un tiret (-) entre les deux
     * Exemple : 86194-9947
     *
     */
    private fun computeNumero38(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)

        return ensureCommune(pei).communeCodeInsee + "-" + "%04d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * <numéro interne> (sans contrainte) Exemple : 1 ou 17280
     *
     */
    private fun computeNumero49(pei: PeiForNumerotationData): String {
        return pei.peiNumeroInterne.toString()
    }

    /**
     * <code insee commune>_<numéro interne>(<P>)
     * Avec P à la fin pour PEI privé
     * Exemple : 89043_12P
     *
     */
    private fun computeNumero66(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)

        val commune = ensureCommune(pei)

        val suffixe = if (isNatureDeciPrive(pei)) "P" else ""

        return commune.communeCodeInsee + "_" + pei.peiNumeroInterne + suffixe
    }

    /**
     * <code insee commune><numéro interne> sans espace
     * avec num_intern sur 4 chiffres
     * Exemple : 772880012
     *
     */
    private fun computeNumeroMethodeD(pei: PeiForNumerotationData): String {
        val codeZoneSpeciale = if (pei.peiZoneSpecialeId != null) ensureZoneSpeciale(pei).zoneIntegrationCode else null
        val commune = ensureCommune(pei)

        return (codeZoneSpeciale ?: commune.communeCodeInsee) + "%04d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * <code insee commune>_<numéro interne>
     * avec num_intern sur 4 chiffres
     * Exemple : 21231_0621
     *
     */
    private fun computeNumero21(pei: PeiForNumerotationData): String {
        val codeZoneSpeciale = if (pei.peiZoneSpecialeId != null) ensureZoneSpeciale(pei).zoneIntegrationCode else null
        val commune = ensureCommune(pei)

        return (codeZoneSpeciale ?: commune.communeCodeInsee) + "_%04d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * <code insee commune>-<numéro interne>
     * avec num_intern sur 4 chiffres
     * Exemple : 22243-0007
     *
     */
    private fun computeNumero22(pei: PeiForNumerotationData): String {
        return (ensureCommune(pei).communeCodeInsee) + "-%04d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * <code insee commune><numéro interne> ou <code insee commune>A<numéro interne>
     * sans espace
     * avec num_interne sur 4 chiffres pour Autoroutes
     * sinon num_interne sur 5 chiffres
     * Exemple : 8904300012 ou  89043A0012 pour les Autoroutes
     *
     */
    private fun computeNumero78(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)
        if (pei.domaine != null) {
            val codeDomaine = pei.domaine.domaineCode

            if (CODE_DOMAINE_AUTOROUTE == codeDomaine) {
                return ensureCommune(pei).communeCodeInsee + "A" + "%04d".format(Locale.getDefault(), pei.peiNumeroInterne)
            }
        }
        return ensureCommune(pei).communeCodeInsee + "%05d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * <Code nature pour les PIBI ou les RI, PN pour les autres> <commune_code>
     * <numéro interne>
     * <p>
     * Exemple : PI TLN 12
     *
     */
    private fun computeNumeroMethodeC(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)
        checkNature(pei)

        val codeZoneSpeciale = if (pei.peiZoneSpecialeId != null) ensureZoneSpeciale(pei).zoneIntegrationCode else null
        val commune = ensureCommune(pei)
        val codeNature: String = pei.nature!!.natureCode

        val sb = StringBuilder()
        if (TypePei.PIBI == pei.nature.natureTypePei || NATURE_RESERVE_INCENDIE == codeNature) {
            sb.append(codeNature)
        } else {
            sb.append("PN")
        }
        sb.append(" ")
        sb.append(codeZoneSpeciale ?: commune.communeCode)
        return sb.append(" ").append(pei.peiNumeroInterne).toString()
    }

    /**
     * <code insee commune>_<numéro interne>
     * <p>
     * Exemple : 89043_12
     *
     */
    private fun computeNumeroMethodeB(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)

        val commune = ensureCommune(pei)
        return commune.communeCodeInsee + "_" + pei.peiNumeroInterne
    }

    /**
     * <code insee commune> <numéro interne>
     * numéro interne sur 3 chiffres:
     * - 0 à 599 : PIBI
     * - 600 à 799 : PUISARDS
     * - 800 à 999 : PENA
     * Exemple : 58267 805 - PEA sur commune de SAINT-SAULGE
     *
     */
    private fun computeNumero58(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)

        val commune = ensureCommune(pei)
        return commune.communeCodeInsee + " " + "%03d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * PEI Public : <code insee commune>_<numéro interne>
     * PEI Privé : <code gestionnaire>_<numéro interne>
     * numéro interne sur 5 chiffres:
     * Exemple : 59039_00004 - PEI public sur la commune d'AWOINGT
     * Exemple : 1242_00004 - PEI privé sur la commune d'AWOINGT
     */
    private fun computeNumero59(pei: PeiForNumerotationData): String {
        checkNatureDeci(pei)
        if (isNatureDeciPrive(pei) || isNatureDeciConventionne(pei) || isNatureDeciICPE(pei) || isNatureDeciICPEConventionne(pei)) {
            checkGestionnaire(pei)
            val gestionnaire = ensureGestionnaire(pei)
            return gestionnaire.gestionnaireCode + "_" + "%05d".format(Locale.getDefault(), pei.peiNumeroInterne)
        } else if (isNatureDeciPublic(pei)) {
            checkCommuneId(pei)
            val commune = ensureCommune(pei)
            return commune.communeCodeInsee + "_" + "%05d".format(Locale.getDefault(), pei.peiNumeroInterne)
        }
        throw IllegalArgumentException("Cas non pris en compte pour la numérotation")
    }

    /**
     * <commune_code>-<numéro interne>
     * numéro interne sur 5 chiffres
     * Exemple : BMA-00443, ABY-00001
     */
    private fun computeNumero971(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)

        val commune = ensureCommune(pei)
        return commune.communeCode + "-" + "%05d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * <code insee commune><PI.BI.PA><numéro interne>
     * sans espace
     * Exemple : 97309PI10, 97304PI122, 97314PA1
     *
     */
    private fun computeNumero973(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)
        checkNature(pei)

        val commune = ensureCommune(pei)
        return commune.communeCodeInsee + pei.nature!!.natureCode + pei.peiNumeroInterne
    }

    /**
     * <code insee commune>.<numéro interne>
     * avec num_interne sur 4 chiffres
     * et un point (.) entre les deux
     * Exemple : 95000.0001
     *
     */
    private fun computeNumero95(pei: PeiForNumerotationData): String {
        checkCommuneId(pei)

        val commune = ensureCommune(pei)
        return commune.communeCodeInsee + "." + "%04d".format(Locale.getDefault(), pei.peiNumeroInterne)
    }

    /**
     * Fonction retournant TRUE si le PEI est de nature DECI privée, FALSE sinon
     *
     */
    private fun isNatureDeciPrive(pei: PeiForNumerotationData): Boolean {
        return isNatureDeci(pei, NATURE_DECI_PRIVE)
    }

    /**
     * Fonction retournant TRUE si le PEI est de nature DECI publique, FALSE sinon
     *
     */
    private fun isNatureDeciPublic(pei: PeiForNumerotationData): Boolean {
        return isNatureDeci(pei, NATURE_DECI_PUBLIC)
    }

    /**
     * Fonction retournant TRUE si le PEI est de nature DECI conventionné, FALSE sinon
     *
     */
    private fun isNatureDeciConventionne(pei: PeiForNumerotationData): Boolean {
        return isNatureDeci(pei, NATURE_DECI_CONVENTIONNE)
    }

    /**
     * Fonction retournant TRUE si le PEI est de nature DECI ICPE, FALSE sinon
     *
     */
    private fun isNatureDeciICPE(pei: PeiForNumerotationData): Boolean {
        return isNatureDeci(pei, NATURE_DECI_ICPE)
    }

    /**
     * Fonction retournant TRUE si le PEI est de nature DECI ICPE Conventionné, FALSE sinon
     *
     */
    private fun isNatureDeciICPEConventionne(pei: PeiForNumerotationData): Boolean {
        return isNatureDeci(pei, NATURE_DECI_ICPE_CONVENTIONNE)
    }

    /**
     * Méthode interne pour les isNatureDeci...
     * @param constanteNatureDeci contenu de la constante associée pour tester le code de la nature DECI
     */
    private fun isNatureDeci(pei: PeiForNumerotationData, constanteNatureDeci: String): Boolean {
        return constanteNatureDeci.equals(pei.natureDeci?.natureDeciCode, ignoreCase = true)
    }

    private fun checkCommuneId(pei: PeiForNumerotationData) {
        if (pei.peiCommuneId == null) {
            throw IllegalArgumentException("Pas de commune pour la numérotation")
        }
    }

    private fun checkGestionnaire(pei: PeiForNumerotationData) {
        if (pei.gestionnaireId == null) {
            throw IllegalArgumentException("Pas de gestionnaire pour la numérotation")
        }
    }

    private fun checkNature(pei: PeiForNumerotationData) {
        if (pei.nature == null) {
            throw IllegalArgumentException("Pas de nature pour la numérotation")
        }
    }

    private fun checkNatureDeci(pei: PeiForNumerotationData) {
        if (pei.natureDeci == null) {
            throw IllegalArgumentException("Pas de nature DECI pour la numérotation")
        }
    }

    /**
     * Retourne TRUE si on a besoin de recalculer le numéro interne à cause d'un changement de domaine. <br />
     *
     *
     * @param domaineId id du domaine courante
     * @param domaineIdInitial id du domaine en BDD
     *
     * @return Boolean : doit-on recalculer le numéro interne ?
     */
    fun needComputeNumeroInterneDomaine(domaineId: UUID, domaineIdInitial: UUID?): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_78 ->
                domaineId != domaineIdInitial
            else -> false
        }
    }

    /**
     * Retourne TRUE si on a besoin de recalculer le numéro interne à cause d'un changement de gestionnaire. <br />
     *
     *
     * @param gestionnaireId id du domaine courante
     * @param gestionnaireIdInitial id du domaine en BDD
     *
     * @return Boolean : doit-on recalculer le numéro interne ?
     */
    fun needComputeNumeroInterneGestionnaire(gestionnaireId: UUID?, gestionnaireIdInitial: UUID?): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_59 ->
                gestionnaireId != gestionnaireIdInitial
            else -> false
        }
    }

    /**
     * Retourne TRUE si on a besoin de recalculer le numéro interne à cause d'un changement de nature DECI. <br />
     *
     *
     * @param natureDeciId id de la nature DECI courante
     * @param natureDeciIdInitial id de la nature DECI en BDD
     *
     * @return Boolean : doit-on recalculer le numéro interne ?
     */
    fun needComputeNumeroInterneNatureDeci(natureDeciId: UUID, natureDeciIdInitial: UUID?): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_53,
            CodeSdis.SDIS_66,
            CodeSdis.SDIS_95,
            ->
                natureDeciId != natureDeciIdInitial
            else -> false
        }
    }

    /**
     * Retourne TRUE si on a besoin de recalculer le numéro interne à cause d'un changement de commune. <br />
     *
     * Pour certaines méthodes de numérotation, on prend en compte la zone spéciale, on doit donc la passer aussi
     *
     * @param communeId id de la commune courante
     * @param communeIdInitial id de la commune en BDD
     * @param zoneSpecialeId id de la zone spéciale courante (nullable)
     * @param zoneSpecialeIdInitial id de la zone spéciale en BDD (nullable)
     *
     * @return Boolean : doit-on recalculer le numéro interne ?
     */
    fun needComputeNumeroInterneCommune(communeId: UUID, communeIdInitial: UUID?, zoneSpecialeId: UUID?, zoneSpecialeIdInitial: UUID?): Boolean {
        return when (appSettings.codeSdis) {
            CodeSdis.SDIS_01,
            CodeSdis.SDIS_22,
            CodeSdis.SDIS_39,
            CodeSdis.SDIS_42,
            CodeSdis.SDIS_53,
            CodeSdis.SDIS_58,
            CodeSdis.SDIS_59,
            CodeSdis.SDIS_61,
            CodeSdis.SDIS_66,
            CodeSdis.SDIS_78,
            CodeSdis.SDIS_971,
            CodeSdis.SDIS_973,
            CodeSdis.BSPP,
            CodeSdis.SDMIS,
            -> return communeId != communeIdInitial
            CodeSdis.SDIS_09,
            CodeSdis.SDIS_21,
            CodeSdis.SDIS_38,
            CodeSdis.SDIS_71,
            CodeSdis.SDIS_77,
            CodeSdis.SDIS_83,
            CodeSdis.SDIS_89,
            CodeSdis.SDIS_95,
            -> communeId != communeIdInitial || zoneSpecialeId != zoneSpecialeIdInitial
            CodeSdis.SDIS_49 -> false
            CodeSdis.SDIS_16 -> TODO()
            CodeSdis.SDIS_62 -> TODO()
        }
    }
}

/**
 * Extrait le prochain numéro interne de la liste des candidats ; On part de *seed*, on en prend *limit*, on trie par ordre naturel et on retourne le premier
 * @param seed Valeur de début de la génération
 * @param limit équivalent du *limit* SQL
 *
 * @return le prochain numéro interne
 */
private fun Collection<Int>.getNextNumeroInterne(seed: Int, limit: Int): Int {
    return generateSequence(seed) { it + 1 }
        .take(limit + 1)
        .minus(this.toSet())
        .sorted()
        .first()
}

/**
 * Extrait le prochain numéro interne de la liste des candidats ; On part de *seed*, on s'arrête quand on arrive à maxValue, on trie par ordre naturel et on retourne le premier
 * @param seed Valeur de début de la génération
 * @param maxValue Valeur max à ne pas dépasser
 *
 * @return le prochain numéro interne
 */
private fun Collection<Int>.getNextNumeroInterneWhile(seed: Int, maxValue: Int): Int {
    return generateSequence(seed) { it + 1 }
        .takeWhile { it <= maxValue }
        .minus(this.toSet())
        .sorted()
        .first()
}
