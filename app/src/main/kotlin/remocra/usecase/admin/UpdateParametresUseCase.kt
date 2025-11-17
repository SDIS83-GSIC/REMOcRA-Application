package remocra.usecase.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import org.owasp.html.PolicyFactory
import remocra.auth.WrappedUserInfo
import remocra.data.ParametresAdminData
import remocra.data.ParametresAdminDataInput
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.data.mapToParametresSectionCouvertureHydraulique
import remocra.db.ParametreRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.parametres.ParametresModifiedEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateParametresUseCase
@Inject constructor(
    private var objectMapper: ObjectMapper,
    private var parametreRepository: ParametreRepository,
    private var policyFactory: PolicyFactory,
) :
    AbstractCUDUseCase<ParametresAdminDataInput>(TypeOperation.UPDATE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroits(droitsWeb = setOf(Droit.ADMIN_PARAM_APPLI, Droit.ADMIN_PARAM_APPLI_MOBILE))) {
            throw RemocraResponseException(ErrorType.ADMIN_PARAMETRE_FORBIDDEN)
        }
    }

    override fun postEvent(element: ParametresAdminDataInput, userInfo: WrappedUserInfo) {
        // impossible de tracer un pojo qui n'a pas d'id aujourd'hui
        eventBus.post(ParametresModifiedEvent())
    }

    override fun execute(userInfo: WrappedUserInfo, element: ParametresAdminDataInput): ParametresAdminDataInput {
        val parametresAdminData = ParametresAdminData(
            pei = element.pei,
            signalement = element.signalement,
            dfci = element.dfci,
            general = element.general,
            mobile = element.mobile,
            cartographie = element.cartographie,
            couvertureHydraulique = mapToParametresSectionCouvertureHydraulique(element.couvertureHydraulique),
            permis = element.permis,
            peiLongueIndispo = element.peiLongueIndispo,
            utilisateur = element.utilisateur,
        )

        if (userInfo.hasDroit(droitWeb = Droit.ADMIN_PARAM_APPLI)) {
            // Général
            updateParametre(ParametreEnum.MENTION_CNIL, parametresAdminData.general.mentionCnil)
            updateParametre(ParametreEnum.MESSAGE_ENTETE, parametresAdminData.general.messageEntete)
            updateParametre(ParametreEnum.TITRE_PAGE, parametresAdminData.general.titrePage)
            updateParametre(
                ParametreEnum.TOLERANCE_VOIES_METRES,
                parametresAdminData.general.toleranceVoiesMetres?.toString(),
            )
            updateParametre(
                ParametreEnum.ACCUEIL_PUBLIC,
                policyFactory.sanitize(parametresAdminData.general.accueilPublic),
            )

            // Signalement
            updateParametre(ParametreEnum.SIGNALEMENT_DELIBERATION_DESTINATAIRE_EMAIL, parametresAdminData.signalement.signalementDeliberationDestinataireEmail)
            updateParametre(ParametreEnum.SIGNALEMENT_DELIBERATION_OBJET_EMAIL, parametresAdminData.signalement.signalementDeliberationObjetEmail)
            updateParametre(ParametreEnum.SIGNALEMENT_DELIBERATION_CORPS_EMAIL, parametresAdminData.signalement.signalementDeliberationCorpsEmail)

            // Cartographie
            updateParametre(
                ParametreEnum.COORDONNEES_FORMAT_AFFICHAGE,
                parametresAdminData.cartographie.coordonneesFormatAffichage,
            )
            updateParametre(
                ParametreEnum.EMPRISE_NATIVE,
                parametresAdminData.cartographie.empriseNative,
            )

            // Couverture hydraulique
            updateParametre(
                ParametreEnum.DECI_DISTANCE_MAX_PARCOURS,
                parametresAdminData.couvertureHydraulique.deciDistanceMaxParcours?.toString(),
            )
            updateParametre(
                ParametreEnum.DECI_ISODISTANCES,
                objectMapper.writeValueAsString(parametresAdminData.couvertureHydraulique.deciIsodistances),
            )
            updateParametre(
                ParametreEnum.PROFONDEUR_COUVERTURE,
                parametresAdminData.couvertureHydraulique.profondeurCouverture?.toString(),
            )

            // DFCI
            updateParametre(ParametreEnum.DFCI_TRAVAUX_DESTINATAIRE_EMAIL, parametresAdminData.dfci.dfciTravauxDestinataireEmail)
            updateParametre(ParametreEnum.DFCI_TRAVAUX_OBJET_EMAIL, parametresAdminData.dfci.dfciTravauxObjetEmail)
            updateParametre(ParametreEnum.DFCI_TRAVAUX_CORPS_EMAIL, parametresAdminData.dfci.dfciTravauxCorpsEmail)

            // Permis
            updateParametre(
                ParametreEnum.PERMIS_TOLERANCE_CHARGEMENT_METRES,
                parametresAdminData.permis.permisToleranceChargementMetres?.toString(),
            )

            // PEI
            updateParametre(ParametreEnum.BUFFER_CARTE, parametresAdminData.pei.bufferCarte?.toString())
            updateParametre(
                ParametreEnum.PEI_COLONNES,
                objectMapper.writeValueAsString(parametresAdminData.pei.peiColonnes),
            )
            updateParametre(ParametreEnum.PEI_DELAI_CTRL_URGENT, parametresAdminData.pei.peiDelaiCtrlUrgent?.toString())
            updateParametre(ParametreEnum.PEI_DELAI_CTRL_WARN, parametresAdminData.pei.peiDelaiCtrlWarn?.toString())
            updateParametre(ParametreEnum.PEI_DELAI_RECO_URGENT, parametresAdminData.pei.peiDelaiRecoUrgent?.toString())
            updateParametre(ParametreEnum.PEI_DELAI_RECO_WARN, parametresAdminData.pei.peiDelaiRecoWarn?.toString())
            updateParametre(
                ParametreEnum.PEI_DEPLACEMENT_DIST_WARN,
                parametresAdminData.pei.peiDeplacementDistWarn?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_GENERATION_CARTE_TOURNEE,
                parametresAdminData.pei.peiGenerationCarteTournee?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_METHODE_TRI_ALPHANUMERIQUE,
                parametresAdminData.pei.peiMethodeTriAlphanumerique?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_RENOUVELLEMENT_CTRL_PRIVE,
                parametresAdminData.pei.peiRenouvellementCtrlPrive?.toString(),
            )
            updateParametre(ParametreEnum.VITESSE_EAU, parametresAdminData.pei.vitesseEau?.toString())
            updateParametre(
                ParametreEnum.PEI_RENOUVELLEMENT_CTRL_PUBLIC,
                parametresAdminData.pei.peiRenouvellementCtrlPublic?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_RENOUVELLEMENT_CTRL_ICPE,
                parametresAdminData.pei.peiRenouvellementCtrlIcpe?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_RENOUVELLEMENT_CTRL_ICPE_CONVENTIONNE,
                parametresAdminData.pei.peiRenouvellementCtrlIcpeConventionne?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_RENOUVELLEMENT_RECO_PRIVE,
                parametresAdminData.pei.peiRenouvellementRecoPrive?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_RENOUVELLEMENT_RECO_PUBLIC,
                parametresAdminData.pei.peiRenouvellementRecoPublic?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_RENOUVELLEMENT_RECO_ICPE,
                parametresAdminData.pei.peiRenouvellementRecoIcpe?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_RENOUVELLEMENT_RECO_ICPE_CONVENTIONNE,
                parametresAdminData.pei.peiRenouvellementRecoIcpeConventionne?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_TOLERANCE_COMMUNE_METRES,
                parametresAdminData.pei.peiToleranceCommuneMetres?.toString(),
            )
            updateParametre(ParametreEnum.PEI_HIGHLIGHT_DUREE, parametresAdminData.pei.peiHighlightDuree?.toString())
            updateParametre(
                ParametreEnum.PEI_RENUMEROTATION_INTERNE_AUTO,
                parametresAdminData.pei.peiRenumerotationInterneAuto?.toString(),
            )
            updateParametre(ParametreEnum.VOIE_SAISIE_LIBRE, parametresAdminData.pei.voieSaisieLibre?.toString())
            updateParametre(
                ParametreEnum.CARACTERISTIQUES_PENA_TOOLTIP_WEB,
                objectMapper.writeValueAsString(parametresAdminData.pei.caracteristiquesPenaTooltipWeb),
            )
            updateParametre(
                ParametreEnum.CARACTERISTIQUES_PIBI_TOOLTIP_WEB,
                objectMapper.writeValueAsString(parametresAdminData.pei.caracteristiquesPibiTooltipWeb),
            )
            updateParametre(
                ParametreEnum.PEI_NOMBRE_HISTORIQUE,
                parametresAdminData.pei.peiNombreHistorique?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_FICHE_RESUME_STANDALONE,
                parametresAdminData.pei.peiFicheResumeStandalone?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_DISPLAY_IDENTIFIANT_GESTIONNAIRE,
                parametresAdminData.pei.peiDisplayIdentifiantGestionnaire?.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_DISPLAY_TYPE_ENGIN,
                parametresAdminData.pei.peiDisplayTypeEngin?.toString(),
            )
            updateParametre(ParametreEnum.DECLARATION_PEI_DESTINATAIRE_EMAIL, parametresAdminData.pei.declarationPeiDestinataireEmail)
            updateParametre(ParametreEnum.DECLARATION_PEI_OBJET_EMAIL, parametresAdminData.pei.declarationPeiObjetEmail)
            updateParametre(ParametreEnum.DECLARATION_PEI_CORPS_EMAIL, parametresAdminData.pei.declarationPeiCorpsEmail)
            updateParametre(ParametreEnum.PEI_LIBELLE_NON_CONFORME, parametresAdminData.pei.peiLibelleNonConforme)

            // ALERTE
            updateParametre(
                ParametreEnum.PEI_LONGUE_INDISPONIBILITE_MESSAGE,
                parametresAdminData.peiLongueIndispo.peiLongueIndisponibiliteMessage,
            )
            updateParametre(
                ParametreEnum.PEI_LONGUE_INDISPONIBILITE_JOURS,
                parametresAdminData.peiLongueIndispo.peiLongueIndisponibiliteJours.toString(),
            )
            updateParametre(
                ParametreEnum.PEI_LONGUE_INDISPONIBILITE_TYPE_ORGANISME,
                objectMapper.writeValueAsString(parametresAdminData.peiLongueIndispo.peiLongueIndisponibiliteTypeOrganisme),
            )

            // Utilisateur
            updateParametre(
                ParametreEnum.ORGANISME_DEFAUT,
                parametresAdminData.utilisateur.organismeDefaut,
            )
            updateParametre(
                ParametreEnum.PROFIL_UTILISATEUR_DEFAUT,
                parametresAdminData.utilisateur.profilUtilisateurDefaut,
            )
        }
        // Mobile
        if (userInfo.hasDroit(droitWeb = Droit.ADMIN_PARAM_APPLI_MOBILE)) {
            updateParametre(ParametreEnum.AFFICHAGE_INDISPO, parametresAdminData.mobile.affichageIndispo?.toString())
            updateParametre(
                ParametreEnum.AFFICHAGE_SYMBOLES_NORMALISES,
                parametresAdminData.mobile.affichageSymbolesNormalises?.toString(),
            )
            updateParametre(
                ParametreEnum.CARACTERISTIQUE_PENA,
                objectMapper.writeValueAsString(parametresAdminData.mobile.caracteristiquesPena),
            )
            updateParametre(
                ParametreEnum.CARACTERISTIQUE_PIBI,
                objectMapper.writeValueAsString(parametresAdminData.mobile.caracteristiquesPibi),
            )
            updateParametre(
                ParametreEnum.DUREE_VALIDITE_TOKEN,
                parametresAdminData.mobile.dureeValiditeToken.toString(),
            )
            updateParametre(ParametreEnum.GESTION_AGENT, parametresAdminData.mobile.gestionAgent)
            updateParametre(ParametreEnum.MDP_ADMINISTRATEUR, parametresAdminData.mobile.mdpAdministrateur)
            updateParametre(ParametreEnum.MODE_DECONNECTE, parametresAdminData.mobile.modeDeconnecte?.toString())
            updateParametre(ParametreEnum.CREATION_PEI_MOBILE, parametresAdminData.mobile.creationPeiMobile?.toString())

            updateParametre(
                ParametreEnum.BRIDAGE_PHOTO,
                parametresAdminData.mobile.bridagePhoto.toString(),
            )
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ParametresAdminDataInput) {
        // vérif du parametre "isodistance"
        element.couvertureHydraulique.deciIsodistances?.let {
            try {
                objectMapper.readValue<List<Int>>("[$it]")
            } catch (e: Exception) {
                throw RemocraResponseException(ErrorType.ADMIN_PARAMETRE_ISODISTANCE_FORMAT)
            }
        }
    }

    private fun updateParametre(parametreEnum: ParametreEnum, value: String?): Boolean {
        return parametreRepository.updateParametre(parametreEnum.name, value)
    }
}
