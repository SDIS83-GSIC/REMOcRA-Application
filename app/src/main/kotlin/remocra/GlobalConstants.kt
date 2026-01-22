package remocra

import java.nio.file.Path

object GlobalConstants {
    const val UTILISATEUR_SYSTEME_USERNAME = "UTILISATEUR_SYSTEME"

    /**
     * Types d'organismes protégés
     */
    const val TYPE_ORGANISME_SERVICE_EAUX: String = "SERVICE_EAUX"
    const val TYPE_ORGANISME_PRESTATAIRE_TECHNIQUE: String = "PRESTATAIRE_TECHNIQUE"
    const val TYPE_ORGANISME_EPCI: String = "EPCI"
    const val TYPE_ORGANISME_COMMUNE = "COMMUNE"
    const val TYPE_ORGANISME_GROUPEMENT = "GROUPEMENT"
    const val TYPE_ORGANISME_CIS = "CIS"
    const val TYPE_ORGANISME_CASERNE = "CASERNE"
    const val TYPE_ORGANISME_AUTRE_SERVICE_PUBLIC_DECI = "AUTRE_SERVICE_PUBLIC_DECI"
    const val TYPE_ORGANISME_PREFECTURE: String = "PREFECTURE"

    // Distance maximale entre 2 BI pour qu'il puisse y avoir jumelage
    const val DISTANCE_MAXIMALE_JUMELAGE = 25

    // Diamètre des PEI
    const val DIAMETRE_70 = "DIAM70"
    const val DIAMETRE_80 = "DIAM80"
    const val DIAMETRE_100 = "DIAM100"
    const val DIAMETRE_150 = "DIAM150"

    /**  Nature des PEI */
    const val NATURE_PI = "PI"
    const val NATURE_BI = "BI"

    const val NATURE_PEN = "PEN"
    const val NATURE_PEA = "PEA"

    // Propre au SDIS 22 - Les valeurs sont protected en base du 22
    const val NATURE_PIBI_ETUDE = "PIBI_ETUDE"
    const val NATURE_PENA_ETUDE = "PENA_ETUDE"

    // Propre au SDIS 59 - Les valeurs sont protected en base du 59
    const val NATURE_ALIMENTATION_POTEAU_RELAIS = "ALI_PR"
    const val NATURE_ALIMENTATION_DE_CONDUITE = "ALI_COND"
    const val NATURE_REFOULEMENT_DE_CONDUITE = "REFL_COND"
    const val NATURE_POTEAU_RELAIS = "REFL_PR"

    // Clés des paramètres
    const val PARAM_PEI_RENUMEROTATION_INTERNE_AUTO = "PEI_RENUMEROTATION_INTERNE_AUTO"
    const val PEI_TOLERANCE_COMMUNE_METRES = "PEI_TOLERANCE_COMMUNE_METRES"
    const val TOLERANCE_VOIES_METRES = "TOLERANCE_VOIES_METRES"
    const val VOIE_SAISIE_LIBRE = "VOIE_SAISIE_LIBRE"

    const val SRID_3857 = 3857
    const val SRID_4326 = 4326

    // TODO: convertir en configuration injectable (valeur par défaut dans reference.conf)
    val DOSSIER_DATA = Path.of(System.getProperty("remocra.fs.base-dir", "/var/lib/remocra/"))

    private val DOSSIER_IMAGES = DOSSIER_DATA.resolve("images")

    val DOSSIER_DOCUMENT_OLD = DOSSIER_DATA.resolve("old")

    val DOSSIER_DOCUMENT = DOSSIER_DATA.resolve("documents")

    val DOSSIER_APACHE_HOP = DOSSIER_DATA.resolve("apache_hop")
    val DOSSIER_APACHE_HOP_CONFIG = DOSSIER_APACHE_HOP.resolve("config")
    val DOSSIER_APACHE_HOP_TASK = DOSSIER_APACHE_HOP.resolve("tasks")

    val DOSSIER_DOCUMENT_TEMPORAIRE = DOSSIER_DOCUMENT.resolve("tmp")
    val DOSSIER_DOCUMENT_SIGNALEMENT = DOSSIER_DOCUMENT.resolve("signalement")
    val DOSSIER_DOCUMENT_DFCI_TRAVAUX = DOSSIER_DOCUMENT.resolve("dfci")
    val DOSSIER_DOCUMENT_COURRIER = DOSSIER_DOCUMENT.resolve("courriers")
    val DOSSIER_DOCUMENT_DELIBERATION = DOSSIER_DOCUMENT.resolve("deliberation")

    val DOSSIER_DEBIT_SIMULTANE = DOSSIER_DOCUMENT.resolve("debits_simultanes")

    val DOSSIER_DOCUMENT_PEI = DOSSIER_DOCUMENT.resolve("pei")

    val DOSSIER_DOCUMENT_ETUDE = DOSSIER_DOCUMENT.resolve("etudes")

    val DOSSIER_DOCUMENT_EVENEMENT = DOSSIER_DOCUMENT.resolve("evenements")

    val DOSSIER_DOCUMENT_CRISE = DOSSIER_DOCUMENT.resolve("crises")

    val DOSSIER_DOCUMENT_RCCI = DOSSIER_DOCUMENT.resolve("rcci")

    val DOSSIER_DOCUMENT_PERMIS = DOSSIER_DOCUMENT.resolve("permis")

    val DOSSIER_DOCUMENT_DECLARATION = DOSSIER_DOCUMENT.resolve("declaration")

    private val DOSSIER_MODELES = DOSSIER_DATA.resolve("modeles")

    val DOSSIER_CARTE_TOURNEE_TEMPLATE = DOSSIER_MODELES.resolve("carte_tournee")

    val DOSSIER_MODELES_COURRIERS = DOSSIER_MODELES.resolve("courriers")

    val DOSSIER_MODELES_EXPORT_CTP = DOSSIER_MODELES.resolve("export_ctp")
    val TEMPLATE_EXPORT_CTP_FILE_NAME = "template_export_ctp.xlsx"
    val TEMPLATE_EXPORT_CTP_FULL_PATH = DOSSIER_MODELES_EXPORT_CTP.resolve(TEMPLATE_EXPORT_CTP_FILE_NAME)

    val DOSSIER_IMAGE_MODULE = DOSSIER_IMAGES.resolve("accueil")
    val DOSSIER_DOCUMENT_HABILITABLE = DOSSIER_DOCUMENT.resolve("document-habilitable")

    val DOSSIER_TMP_COUVERTURE_HYDRAULIQUE = DOSSIER_DATA.resolve("couverture_hydraulique").resolve("tmp")
    val DOSSIER_TMP_IMPORT_SITES = DOSSIER_DATA.resolve("sites").resolve("tmp")
    val DOSSIER_TMP_IMPORT_ZONES_INTEGRATION = DOSSIER_DATA.resolve("zones_integration").resolve("tmp")
    val DOSSIER_TMP_IMPORT_CADASTRE = DOSSIER_DATA.resolve("cadastre").resolve("tmp")

    // Ressources "statiques", logo, bannière & co
    val DOSSIER_IMAGES_RESSOURCES = DOSSIER_IMAGES.resolve("ressources")
    val BANNIERE_FULL_PATH = DOSSIER_IMAGES_RESSOURCES.resolve("banniere")
    val LOGO_FULL_PATH = DOSSIER_IMAGES_RESSOURCES.resolve("logo")

    // Toutes les images participant à la symbologie des PEI
    val DOSSIER_IMAGES_SYMBOLOGIE = DOSSIER_IMAGES.resolve("symbologie")

    // Code catégorie anomalie systeme
    const val CATEGORIE_ANOMALIE_SYSTEME = "SYSTEME"

    // Constantes pour les courriers :
    const val ROLE_DESTINATAIRE_MAIRE_ROP = "DESTINATAIRE_MAIRE_ROP"
    const val ROLE_SIGNATAIRE_GROUPEMENT = "SIGNATAIRE_GROUPEMENT"
    const val COURRIER_CODE_ROP = "ROP"

    const val DOSSIER_DOC_HYDRANT: String = "DOSSIER_DOC_HYDRANT"

    /** Clé de la table "Parametre" pour la gestion des agents dans l'application mobile  */
    const val GESTION_AGENT: String = "GESTION_AGENT"

    /** Paramètres utilisés dans le cadre de l'appli mobile  */
    const val PARAMETRE_CARACTERISTIQUE_PIBI: String = "CARACTERISTIQUE_PIBI"

    const val PARAMETRE_CARACTERISTIQUE_PENA: String = "CARACTERISTIQUE_PENA"
    const val PARAMETRE_DUREE_VALIDITE_TOKEN: String = "DUREE_VALIDITE_TOKEN"
    const val PARAMETRE_MODE_DECONNECTE: String = "MODE_DECONNECTE"
    const val PARAMETRE_MDP_ADMINISTRATEUR: String = "MDP_ADMINISTRATEUR"

    enum class TypeDocument(val typeDocument: String) {
        TYPE_DOCUMENT_HYDRANT("HYDRANT"),
    }

    const val NATURE_DECI_PRIVE = "PRIVE"
    const val NATURE_DECI_ICPE = "ICPE"
    const val NATURE_DECI_ICPE_CONVENTIONNE = "ICPE_CONVENTIONNE"

    /** Constante utilisée dans PurgerTask indiquant l'ancienneté min des documents_tmp à purger (en heure) */
    const val DELAI_PURGE_FICHIER_TEMPORAIRE: Long = 24

    /** Code des Rôles Contact Protected */
    const val CHANGEMENT_ETAT_PEI: String = "CHANGEMENT_ETAT_PEI"
    const val IT_NOTIF_AVANT_DEBUT: String = "IT_NOTIF_AVANT_DEBUT"
    const val IT_NOTIF_AVANT_FIN: String = "IT_NOTIF_AVANT_FIN"
    const val IT_NOTIF_RESTE_INDISPO: String = "IT_NOTIF_RESTE_INDISPO"
    const val CONTACT_ROLE_RAPPORT_POST_ROP = "RAPPORT_POST_ROP"

    const val SCHEMA_ENTREPOT_SIG: String = "ENTREPOTSIG"

    const val PLACEHOLDER_ERROR_TYPE: String = "#PLACEHOLDER#"

    const val DELIMITER_CSV = ';'

    const val COUCHE_TOURNEE = "remocra:TOURNEE"

    // Code des Thématiques protégées
    const val THEMATIQUE_POINT_EAU = "POINT_EAU"
}
