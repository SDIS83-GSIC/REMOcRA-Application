package remocra

object GlobalConstants {
    const val UTILISATEUR_SYSTEME_USERNAME = "UTILISATEUR_SYSTEME"

    /** Type prédéfini pour un organisme de type "service des eaux"  */
    const val SERVICE_EAUX: String = "SERVICEEAUX"

    /** Type prédéfini pour un organisme de type "prestataire technique"  */
    const val PRESTATAIRE_TECHNIQUE: String = "PRESTATAIRE_TECHNIQUE"

    /** Type prédéfini pour un organisme de type "commune  */
    const val COMMUNE: String = "COMMUNE"

    /** Type prédéfini pour un organisme de type "EPCI"  */
    const val EPCI: String = "EPCI"

    const val TYPE_ORGANISME_COMMUNE = "COMMUNE"
    const val TYPE_ORGANISME_GROUPEMENT = "GROUPEMENT"
    const val TYPE_ORGANISME_CIS = "CIS"
    const val TYPE_ORGANISME_CASERNE = "CASERNE"

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

    // Clés des paramètres
    const val PARAM_PEI_RENUMEROTATION_INTERNE_AUTO = "PEI_RENUMEROTATION_INTERNE_AUTO"
    const val PEI_TOLERANCE_COMMUNE_METRES = "PEI_TOLERANCE_COMMUNE_METRES"
    const val TOLERANCE_VOIES_METRES = "TOLERANCE_VOIES_METRES"
    const val VOIE_SAISIE_LIBRE = "VOIE_SAISIE_LIBRE"

    const val SRID_4326 = 4326

    // TODO: convertir en configuration injectable (valeur par défaut dans reference.conf)
    // TODO: convertir en java.nio.file.Path
    val DOSSIER_DATA = System.getProperty("remocra.fs.base-dir", "/var/lib/remocra/")

    val DOSSIER_IMAGES = DOSSIER_DATA + "images/"

    val DOSSIER_DOCUMENT_OLD = DOSSIER_DATA + "old/"

    val DOSSIER_DOCUMENT = DOSSIER_DATA + "documents/"

    val DOSSIER_DOCUMENT_TEMPORAIRE = DOSSIER_DOCUMENT + "tmp/"

    val DOSSIER_DEBIT_SIMULTANE = DOSSIER_DOCUMENT + "debits_simultanes/"

    val DOSSIER_DOCUMENT_PEI = DOSSIER_DOCUMENT + "pei/"

    val DOSSIER_DOCUMENT_ETUDE = DOSSIER_DOCUMENT + "etudes/"

    val DOSSIER_DOCUMENT_RCCI = DOSSIER_DOCUMENT + "rcci/"

    val DOSSIER_MODELES = DOSSIER_DATA + "modeles/"

    val DOSSIER_MODELES_COURRIERS = DOSSIER_MODELES + "courriers/"

    val DOSSIER_MODELES_EXPORT_CTP = DOSSIER_MODELES + "export_ctp/"
    const val TEMPLATE_EXPORT_CTP_FILE_NAME = "template_export_ctp.xlsx"
    val TEMPLATE_EXPORT_CTP_FULL_PATH = DOSSIER_MODELES_EXPORT_CTP + TEMPLATE_EXPORT_CTP_FILE_NAME

    val DOSSIER_IMAGE_MODULE = DOSSIER_IMAGES + "accueil/"
    val DOSSIER_DOCUMENT_HABILITABLE = DOSSIER_DOCUMENT + "document-habilitable/"

    val DOSSIER_TMP_COUVERTURE_HYDRAULIQUE = DOSSIER_DATA + "couverture_hydraulique/tmp/"
    val DOSSIER_TMP_IMPORT_SITES = DOSSIER_DATA + "sites/tmp/"
    val DOSSIER_TMP_IMPORT_ZONES_INTEGRATION = DOSSIER_DATA + "zones_integration/tmp/"

    // Ressources "statiques", logo, bannière & co
    val DOSSIER_IMAGES_RESSOURCES = DOSSIER_IMAGES + "ressources/"
    val BANNIERE_FULL_PATH = DOSSIER_IMAGES_RESSOURCES + "banniere"
    val LOGO_FULL_PATH = DOSSIER_IMAGES_RESSOURCES + "logo"

    // Toutes les images participant à la symbologie des PEI
    val DOSSIER_IMAGES_SYMBOLOGIE = DOSSIER_IMAGES + "symbologie/"

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

    /** Constante utilisée dans PurgerTask indiquant l'ancienneté min des documents_tmp à purger (en heure) */
    const val DELAI_PURGE_FICHIER_TEMPORAIRE: Long = 24

    /** Code des Rôles Contact Protected */
    const val CHANGEMENT_ETAT_PEI: String = "CHANGEMENT_ETAT_PEI"
    const val IT_NOTIF_AVANT_DEBUT: String = "IT_NOTIF_AVANT_DEBUT"
    const val IT_NOTIF_AVANT_FIN: String = "IT_NOTIF_AVANT_FIN"
    const val IT_NOTIF_RESTE_INDISPO: String = "IT_NOTIF_RESTE_INDISPO"
    // TODO : A agrémenter petit à petit

    const val SCHEMA_ENTREPOT_SIG: String = "ENTREPOTSIG"

    const val PLACEHOLDER_ERROR_TYPE: String = "#PLACEHOLDER#"

    const val DELIMITER_CSV = ';'
}
