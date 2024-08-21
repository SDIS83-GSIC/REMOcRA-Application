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

    // Distance maximale entre 2 BI pour qu'il puisse y avoir jumelage
    const val DISTANCE_MAXIMALE_JUMELAGE = 25

    // Diamètre des PEI
    const val DIAMETRE_70 = "DIAM70"
    const val DIAMETRE_80 = "DIAM80"
    const val DIAMETRE_100 = "DIAM100"
    const val DIAMETRE_150 = "DIAM150"

    const val NATURE_PI = "PI"
    const val NATURE_BI = "BI"

    // Clés des paramètres
    const val PARAM_PEI_RENUMEROTATION_INTERNE_AUTO = "PEI_RENUMEROTATION_INTERNE_AUTO"
    const val PEI_TOLERANCE_COMMUNE_METRES = "PEI_TOLERANCE_COMMUNE_METRES"
    const val TOLERANCE_VOIES_METRES = "TOLERANCE_VOIES_METRES"

    const val SRID_4326 = 4326

    const val DOSSIER_DATA = "/var/lib/remocra/"

    const val DOSSIER_IMAGES = DOSSIER_DATA + "images/"

    const val DOSSIER_DOCUMENT = DOSSIER_DATA + "documents/"

    const val DOSSIER_DOCUMENT_TEMPORAIRE = DOSSIER_DOCUMENT + "tmp/"
    const val DOSSIER_DOCUMENT_PEI = DOSSIER_DOCUMENT + "pei/"

    const val DOSSIER_MODELES_COURRIERS = DOSSIER_DATA + "modeles/courriers/"

    const val DOSSIER_IMAGE_MODULE = DOSSIER_IMAGES + "accueil/"

    // Code catégorie anomalie systeme
    const val CATEGORIE_ANOMALIE_SYSTEME = "SYSTEME"

    // Constantes pour les courriers :
    const val ROLE_DESTINATAIRE_MAIRE_ROP = "DESTINATAIRE_MAIRE_ROP"
    const val ROLE_SIGNATAIRE_GROUPEMENT = "SIGNATAIRE_GROUPEMENT"
    const val COURRIER_CODE_ROP = "ROP"

    /** Tag pour les endpoints de l'application mobile  */
    const val REMOCRA_MOBILE_TAG: String = "REMOcRA Mobile"

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
}
