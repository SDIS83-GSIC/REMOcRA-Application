package remocra.web.admin

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import remocra.data.enums.ParametreEnum
import remocra.db.ParametreRepository
import remocra.eventbus.EventBus
import remocra.eventbus.parametres.ParametresModifiedEvent

/**
 * Usecase permettant de manipuler les paramètres de l'application.
 */
class ParametresUseCase {
    @Inject
    private lateinit var parametreRepository: ParametreRepository

    @Inject
    private lateinit var objectMapper: ObjectMapper

    @Inject
    private lateinit var eventBus: EventBus

    // TODO la gestion des valeurs par défaut / NULL nécessitera certainement des ajustements
    fun getParametresData(): ParametresData {
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
            caracteristiquesPena = mapParametres.getListOfString(ParametreEnum.CARACTERISTIQUE_PENA.name, objectMapper),
            caracteristiquesPibi = mapParametres.getListOfString(ParametreEnum.CARACTERISTIQUE_PIBI.name, objectMapper),
            dureeValiditeToken = mapParametres.getInt(ParametreEnum.DUREE_VALIDITE_TOKEN.name),
            gestionAgent = mapParametres.getStringOrNull(ParametreEnum.GESTION_AGENT.name),
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

        )

        return ParametresData(
            general = general,
            mobile = mobile,
            cartographie = cartographie,
            couvertureHydraulique = couvertureHydraulique,
            permis = permis,
            pei = pei,

        )
    }

    fun updateParametres(parametresData: ParametresData) {
        // Général
        updateParametre(ParametreEnum.MENTION_CNIL, parametresData.general.mentionCnil)
        updateParametre(ParametreEnum.MESSAGE_ENTETE, parametresData.general.messageEntete)
        updateParametre(ParametreEnum.TITRE_PAGE, parametresData.general.titrePage)
        updateParametre(ParametreEnum.TOLERANCE_VOIES_METRES, parametresData.general.toleranceVoiesMetres?.toString())

        // Mobile
        updateParametre(ParametreEnum.AFFICHAGE_INDISPO, parametresData.mobile.affichageIndispo?.toString())
        updateParametre(ParametreEnum.AFFICHAGE_SYMBOLES_NORMALISES, parametresData.mobile.affichageSymbolesNormalises?.toString())
        updateParametre(ParametreEnum.CARACTERISTIQUE_PENA, objectMapper.writeValueAsString(parametresData.mobile.caracteristiquesPena))
        updateParametre(ParametreEnum.CARACTERISTIQUE_PIBI, objectMapper.writeValueAsString(parametresData.mobile.caracteristiquesPibi))
        updateParametre(ParametreEnum.DUREE_VALIDITE_TOKEN, parametresData.mobile.dureeValiditeToken.toString())
        updateParametre(ParametreEnum.GESTION_AGENT, parametresData.mobile.gestionAgent)

        // Cartographie
        updateParametre(ParametreEnum.COORDONNEES_FORMAT_AFFICHAGE, parametresData.cartographie.coordonneesFormatAffichage)

        // Couverture hydraulique
        updateParametre(ParametreEnum.DECI_DISTANCE_MAX_PARCOURS, parametresData.couvertureHydraulique.deciDistanceMaxParcours?.toString())
        updateParametre(ParametreEnum.DECI_ISODISTANCES, objectMapper.writeValueAsString(parametresData.couvertureHydraulique.deciIsodistances))
        updateParametre(ParametreEnum.PROFONDEUR_COUVERTURE, parametresData.couvertureHydraulique.profondeurCouverture?.toString())

        // Permis
        updateParametre(ParametreEnum.PERMIS_TOLERANCE_CHARGEMENT_METRES, parametresData.permis.permisToleranceChargementMetres?.toString())

        // PEI
        updateParametre(ParametreEnum.BUFFER_CARTE, parametresData.pei.bufferCarte?.toString())
        updateParametre(ParametreEnum.PEI_COLONNES, objectMapper.writeValueAsString(parametresData.pei.peiColonnes))

        eventBus.post(ParametresModifiedEvent())
    }

    private fun updateParametre(parametreEnum: ParametreEnum, value: String?): Boolean {
        return parametreRepository.updateParametre(parametreEnum.name, value)
    }
}

private fun <String, Parametre> Map<String, Parametre>.getParam(key: String): Parametre {
    val value = this[key] ?: throw IllegalArgumentException("La clé $key n'existe pas dans les paramètres")

    return value
}

data class ParametresData(
    val general: ParametresSectionGeneral,

    val mobile: ParametresSectionMobile,
    val cartographie: ParametresSectionCartographie,
    val couvertureHydraulique: ParametresSectionCouvertureHydraulique,
    val permis: ParametresSectionPermis,
    val pei: ParametresSectionPei,

)

data class ParametresSectionGeneral(
    val mentionCnil: String?,
    val messageEntete: String?,
    val titrePage: String?,
    val toleranceVoiesMetres: Int?,

)

data class ParametresSectionMobile(
    val affichageIndispo: Boolean?,
    val affichageSymbolesNormalises: Boolean?,
    val caracteristiquesPena: List<String>?,
    val caracteristiquesPibi: List<String>?,
    val dureeValiditeToken: Int,
    val gestionAgent: String?,

)

data class ParametresSectionCartographie(
    val coordonneesFormatAffichage: String?,
)

data class ParametresSectionCouvertureHydraulique(
    val deciDistanceMaxParcours: Int?,
    val deciIsodistances: List<Int>?,
    val profondeurCouverture: Int?,
)

data class ParametresSectionPermis(
    val permisToleranceChargementMetres: Int?,
)

data class ParametresSectionPei(
    val bufferCarte: Int?,
    val peiColonnes: List<String>?,
)
