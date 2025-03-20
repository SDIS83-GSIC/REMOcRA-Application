package remocra.usecase.admin

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import remocra.data.ParametresAdminData
import remocra.data.ParametresSectionCartographie
import remocra.data.ParametresSectionCouvertureHydraulique
import remocra.data.ParametresSectionGeneral
import remocra.data.ParametresSectionMobile
import remocra.data.ParametresSectionPei
import remocra.data.ParametresSectionPeiLongueIndispo
import remocra.data.ParametresSectionPermis
import remocra.data.enums.ParametreEnum
import remocra.db.ParametreRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.getBooleanOrNull
import remocra.utils.getInt
import remocra.utils.getIntOrNull
import remocra.utils.getListOfInt
import remocra.utils.getListOfPeiCaracteristique
import remocra.utils.getListOfString
import remocra.utils.getString
import remocra.utils.getStringOrNull

/**
 * Usecase permettant de manipuler les paramètres de l'application.
 */
class ParametresUseCase : AbstractUseCase() {
    @Inject
    private lateinit var parametreRepository: ParametreRepository

    @Inject
    private lateinit var objectMapper: ObjectMapper

    // TODO la gestion des valeurs par défaut / NULL nécessitera certainement des ajustements
    fun getParametresData(): ParametresAdminData {
        val mapParametres = parametreRepository.getMapParametres()

        val general = ParametresSectionGeneral(
            mentionCnil = mapParametres.getStringOrNull(ParametreEnum.MENTION_CNIL.name),
            messageEntete = mapParametres.getStringOrNull(ParametreEnum.MESSAGE_ENTETE.name),
            titrePage = mapParametres.getStringOrNull(ParametreEnum.TITRE_PAGE.name),
            toleranceVoiesMetres = mapParametres.getIntOrNull(ParametreEnum.TOLERANCE_VOIES_METRES.name),
        )

        val mobile = ParametresSectionMobile(
            affichageIndispo = mapParametres.getBooleanOrNull(ParametreEnum.AFFICHAGE_INDISPO.name),
            affichageSymbolesNormalises = mapParametres.getBooleanOrNull(ParametreEnum.AFFICHAGE_SYMBOLES_NORMALISES.name),
            caracteristiquesPena = mapParametres.getListOfPeiCaracteristique(ParametreEnum.CARACTERISTIQUE_PENA.name, objectMapper),
            caracteristiquesPibi = mapParametres.getListOfPeiCaracteristique(ParametreEnum.CARACTERISTIQUE_PIBI.name, objectMapper),
            dureeValiditeToken = mapParametres.getInt(ParametreEnum.DUREE_VALIDITE_TOKEN.name),
            gestionAgent = mapParametres.getStringOrNull(ParametreEnum.GESTION_AGENT.name),
            mdpAdministrateur = mapParametres.getStringOrNull(ParametreEnum.MDP_ADMINISTRATEUR.name),
            modeDeconnecte = mapParametres.getBooleanOrNull(ParametreEnum.MODE_DECONNECTE.name),
            creationPeiMobile = mapParametres.getBooleanOrNull(ParametreEnum.CREATION_PEI_MOBILE.name),
        )

        val cartographie = ParametresSectionCartographie(
            coordonneesFormatAffichage = mapParametres.getStringOrNull(ParametreEnum.COORDONNEES_FORMAT_AFFICHAGE.name),
        )

        val couvertureHydraulique = ParametresSectionCouvertureHydraulique(
            deciDistanceMaxParcours = mapParametres.getIntOrNull(ParametreEnum.DECI_DISTANCE_MAX_PARCOURS.name),
            deciIsodistances = mapParametres.getListOfInt(ParametreEnum.DECI_ISODISTANCES.name, objectMapper),
            profondeurCouverture = mapParametres.getIntOrNull(ParametreEnum.PROFONDEUR_COUVERTURE.name),
        )

        val permis = ParametresSectionPermis(
            permisToleranceChargementMetres = mapParametres.getIntOrNull(ParametreEnum.PERMIS_TOLERANCE_CHARGEMENT_METRES.name),
        )
        val pei = ParametresSectionPei(
            bufferCarte = mapParametres.getIntOrNull(ParametreEnum.BUFFER_CARTE.name),
            peiColonnes = mapParametres.getListOfString(ParametreEnum.PEI_COLONNES.name, objectMapper),
            peiDelaiCtrlUrgent = mapParametres.getIntOrNull(ParametreEnum.PEI_DELAI_CTRL_URGENT.name),
            peiDelaiCtrlWarn = mapParametres.getIntOrNull(ParametreEnum.PEI_DELAI_CTRL_WARN.name),
            peiDelaiRecoUrgent = mapParametres.getIntOrNull(ParametreEnum.PEI_DELAI_RECO_URGENT.name),
            peiDelaiRecoWarn = mapParametres.getIntOrNull(ParametreEnum.PEI_DELAI_RECO_WARN.name),
            peiDeplacementDistWarn = mapParametres.getIntOrNull(ParametreEnum.PEI_DEPLACEMENT_DIST_WARN.name),
            peiGenerationCarteTournee = mapParametres.getBooleanOrNull(ParametreEnum.PEI_GENERATION_CARTE_TOURNEE.name),
            peiMethodeTriAlphanumerique = mapParametres.getBooleanOrNull(ParametreEnum.PEI_METHODE_TRI_ALPHANUMERIQUE.name),
            peiRenouvellementCtrlPrive = mapParametres.getIntOrNull(ParametreEnum.PEI_RENOUVELLEMENT_CTRL_PRIVE.name),
            vitesseEau = mapParametres.getIntOrNull(ParametreEnum.VITESSE_EAU.name),
            peiRenouvellementCtrlPublic = mapParametres.getIntOrNull(ParametreEnum.PEI_RENOUVELLEMENT_CTRL_PUBLIC.name),
            peiRenouvellementRecoPrive = mapParametres.getIntOrNull(ParametreEnum.PEI_RENOUVELLEMENT_RECO_PRIVE.name),
            peiRenouvellementRecoPublic = mapParametres.getIntOrNull(ParametreEnum.PEI_RENOUVELLEMENT_RECO_PUBLIC.name),
            peiToleranceCommuneMetres = mapParametres.getIntOrNull(ParametreEnum.PEI_TOLERANCE_COMMUNE_METRES.name),
            peiHighlightDuree = mapParametres.getIntOrNull(ParametreEnum.PEI_HIGHLIGHT_DUREE.name),
            peiRenumerotationInterneAuto = mapParametres.getBooleanOrNull(ParametreEnum.PEI_RENUMEROTATION_INTERNE_AUTO.name),
            voieSaisieLibre = mapParametres.getBooleanOrNull(ParametreEnum.VOIE_SAISIE_LIBRE.name),
            caracteristiquesPenaTooltipWeb = mapParametres.getListOfPeiCaracteristique(ParametreEnum.CARACTERISTIQUES_PENA_TOOLTIP_WEB.name, objectMapper),
            caracteristiquesPibiTooltipWeb = mapParametres.getListOfPeiCaracteristique(ParametreEnum.CARACTERISTIQUES_PIBI_TOOLTIP_WEB.name, objectMapper),
            peiNombreHistorique = mapParametres.getIntOrNull(ParametreEnum.PEI_NOMBRE_HISTORIQUE.name),
        )

        val peiLongueIndispo = ParametresSectionPeiLongueIndispo(
            peiLongueIndisponibiliteJours = mapParametres.getInt(ParametreEnum.PEI_LONGUE_INDISPONIBILITE_JOURS.name),
            peiLongueIndisponibiliteMessage = mapParametres.getString(ParametreEnum.PEI_LONGUE_INDISPONIBILITE_MESSAGE.name),
            peiLongueIndisponibiliteTypeOrganisme = mapParametres.getListOfString(ParametreEnum.PEI_LONGUE_INDISPONIBILITE_TYPE_ORGANISME.name, objectMapper),
        )

        return ParametresAdminData(
            general = general,
            mobile = mobile,
            cartographie = cartographie,
            couvertureHydraulique = couvertureHydraulique,
            permis = permis,
            pei = pei,
            peiLongueIndispo = peiLongueIndispo,

        )
    }
}

private fun <String, Parametre> Map<String, Parametre>.getParam(key: String): Parametre {
    val value = this[key] ?: throw IllegalArgumentException("La clé $key n'existe pas dans les paramètres")

    return value
}
