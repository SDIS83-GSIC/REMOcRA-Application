package remocra.data.enums

import jakarta.ws.rs.core.Response.Status
import remocra.GlobalConstants
import remocra.GlobalConstants.PLACEHOLDER_ERROR_TYPE
import remocra.db.jooq.remocra.enums.TypeVisite

/**
 * Enumération des erreurs pouvant être déclenchées au travers de l'utilisation de l'application. <br />
 * Le *code* servira dans l'API, qui le remonte systématiquement.
 * Le *status* permet d'interpréter proprement l'exception au moyen d'un *wrap* générique, il est par défaut à [Status.BAD_REQUEST]
 *
 */
enum class ErrorType(val code: Int, val libelle: String, val status: Status = Status.BAD_REQUEST, val replacement: String? = null) {
    // ********************************************************************************
    // Erreurs générales
    // ********************************************************************************
    BAD_UUID(100, "Cette chaîne ne représente pas un UUID valide"),

    FORBIDDEN_ZONE_COMPETENCE(101, "L'élément n'est pas dans votre zone de compétence", Status.FORBIDDEN),

    /**
     * Pour la gestion des PEI
     */
    PEI_INEXISTANT(1000, "Le numéro spécifié ne correspond à aucun PEI"),
    PEI_FORBIDDEN_C(1001, "Vous n'avez pas les droits de création de PEI.", Status.FORBIDDEN),
    PEI_FORBIDDEN_U(1002, "Vous n'avez pas les droits de modification de PEI", Status.FORBIDDEN),
    PEI_FORBIDDEN_D(1003, "Vous n'avez pas les droits de suppression de PEI", Status.FORBIDDEN),
    PEI_DOCUMENT_MEME_NOM(1004, "Les documents d'un même PEI ne doivent pas avoir le même nom."),
    PEI_DOCUMENT_PHOTO(1005, "Un seul document peut représenter la photo du PEI"),
    PEI_VOIE_SAISIE_LIBRE_FORBIDDEN(1007, "La saisie libre d'une voie n'est pas autorisée pour votre SDIS"),
    PEI_VOIE_OBLIGATOIRE(1008, "La saisie d'une voie est obligatoire"),
    PEI_VOIE_XOR(1009, "Vous ne pouvez pas à la fois sélectionner une voie et saisir une valeur textuelle"),

    /*
        Erreur si on essaie de supprimer une IT en cascade de la suppression d'un PEI mais qu'on n'a pas les droits de suppresion
        des IT
     */
    PEI_FORBIDDEN_D_INDISPONIBILITE_TEMPORAIRE(
        1100,
        """
        Le PEI que vous tentez de supprimer possède une indisponibilité temporaire contenant seulement ce PEI,
        mais vous n'avez pas les droits pour supprimer cette dernière. Cette opération ne peut donc aboutir,
        contactez le SDIS pour supprimer ce PEI
        """.trimIndent(),
        Status.FORBIDDEN,

    ),

    /*
        Erreur si on essaie de supprimer une tournée en cascade de la suppression d'un PEI mais qu'on n'a pas les droits de suppresion
        des tournées
     */
    PEI_FORBIDDEN_D_TOURNEE(
        1101,
        """
        Le PEI que vous tentez de supprimer est inclus dans une tournée contenant seulement ce PEI,
        mais vous n'avez pas les droits pour supprimer cette dernière.
        Cette opération ne peut donc aboutir, contactez le SDIS pour supprimer ce PEI
        """.trimIndent(),
        Status.FORBIDDEN,
    ),
    PEI_INDISPONIBILITE_TEMPORAIRE_EN_COURS(
        1102,
        """
        Le PEI que vous tentez de supprimer est contenu dans une indisponibilité temporaire en cours.
        Veuillez clore l'indisponibilité temporaire avant de supprimer le PEI
        """.trimIndent(),

    ),
    PEI_TOURNEE_LECTURE_SEULE(
        1103,
        "Le PEI fait partie d'une tournée réservée, impossible de le supprimer",
    ),

    FORBIDDEN(1300, "Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible", Status.FORBIDDEN),
    BAD_PATTERN(1010, "La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm"),

    //
    // ********************************************************************************
    // Visites
    // ********************************************************************************
    //
    CODE_TYPE_VISITE_INEXISTANT(2001, "Le type de visite spécifié n'existe pas"),
    VISITE_ANO_NON_DISPO(
        2002,
        "Une ou plusieurs anomalies contrôlées n'existent pas ou ne sont pas disponibles pour une visite de ce type",
    ),
    VISITE_INEXISTANTE(2003, "Aucune visite avec cet identifiant n'a été trouvée pour le numéro de PEI spécifié"),

    VISITE_EXISTE(2100, "Une visite est déjà présente à ce moment pour ce PEI"),
    VISITE_RECEPTION(2101, "Le type de visite doit être de type ${TypeVisite.RECEPTION} (première visite du PEI)"),
    VISITE_RECO_INIT(2102, "Le type de visite doit être de type ${TypeVisite.RECO_INIT} (deuxième visite du PEI)"),
    VISITE_MEME_TYPE_EXISTE(
        2103,
        "Une visite de ce type existe déjà. Veuillez utiliser une visite de type ${TypeVisite.NP}, ${TypeVisite.RECOP} ou ${TypeVisite.CTP}",
    ),
    VISITE_ANO_CONSTATEE(2104, "Une ou plusieurs anomalies ont été marquées constatées sans avoir été contrôlées"),
    VISITE_DEBIT_INF_0(2105, "Le débit ne peut être inférieur à 0"),
    VISITE_DEBIT_MAX_INF_0(2106, "Le débit maximum ne peut être inférieur à 0"),
    VISITE_PRESSION_INF_0(2107, "La pression ne peut être inférieure à 0"),
    VISITE_PRESSION_DYN_INF_0(2108, "La pression dynamique ne peut être inférieure à 0"),
    VISITE_PRESSION_DYN_DEBIT_MAX_INF_0(2109, "La pression dynamique au débit maximum ne peut être inférieure à 0"),
    VISITE_UPDATE_NOT_LAST(2110, "Modification de la visite impossible : une visite plus récente est présente"),
    VISITE_AFTER_NOW(2111, "Impossible de créer une visite dans le futur"),
    VISITE_CREATE_NOT_LAST(
        2112,
        "La date renseignée est antérieure à la dernière visite de ce PEI, les visites doivent être saisies par ordre chronologique.",
    ),
    VISITE_CDP_INVALIDE(
        2113,
        "Au moins une valeur technique doit être saisie pour un contrôle débit pression valide.",
        Status.BAD_REQUEST,
    ),
    VISITE_DELETE_NOT_LAST(2114, "La visite que vous essayez de supprimer n'est pas la dernière en date de ce PEI"),
    VISITE_RECEPTION_NOT_FIRST(2115, "La visite de type ${TypeVisite.RECEPTION} doit être la première visite du PEI"),
    VISITE_RECO_INIT_NOT_FIRST(2116, "La visite de type ${TypeVisite.RECO_INIT} doit être la deuxième visite du PEI"),
    VISITE_CDP_PENA(2117, "Il est impossible de saisir un Contrôle Débit Pression pour un PENA"),

    VISITE_TYPE_FORBIDDEN(
        2200,
        "Ce type de visite n'est pas accessible pour votre organisme sur ce PEI",
        Status.FORBIDDEN,
    ),
    VISITE_ORGANISME_FORBIDDEN(
        2201,
        "Votre organisme n'est pas autorisé à modifier une visite de ce type sur ce PEI",
        Status.FORBIDDEN,
    ),

    VISITE_C_FORBIDDEN(2300, "Vous n'avez pas les droits suffisant pour créer une visite", Status.FORBIDDEN),
    VISITE_D_FORBIDDEN(2301, "Vous n'avez pas les droits suffisant pour supprimer une visite", Status.FORBIDDEN),
    VISITE_C_CTP_FORBIDDEN(
        2311,
        "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.CTP}",
        Status.FORBIDDEN,
    ),
    VISITE_D_CTP_FORBIDDEN(
        2312,
        "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.CTP}",
        Status.FORBIDDEN,
    ),
    VISITE_C_NP_FORBIDDEN(
        2321,
        "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.NP}",
        Status.FORBIDDEN,
    ),
    VISITE_D_NP_FORBIDDEN(
        2322,
        "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.NP}",
        Status.FORBIDDEN,
    ),
    VISITE_C_RECEPTION_FORBIDDEN(
        2331,
        "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.RECEPTION}",
        Status.FORBIDDEN,
    ),
    VISITE_D_RECEPTION_FORBIDDEN(
        2332,
        "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.RECEPTION}",
        Status.FORBIDDEN,
    ),
    VISITE_C_RECOP_FORBIDDEN(
        2341,
        "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.RECOP}",
        Status.FORBIDDEN,
    ),
    VISITE_D_RECOP_FORBIDDEN(
        2342,
        "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.RECOP}",
        Status.FORBIDDEN,
    ),
    VISITE_C_RECO_INIT_FORBIDDEN(
        2351,
        "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.RECO_INIT}",
        Status.FORBIDDEN,
    ),
    VISITE_D_RECO_INIT_FORBIDDEN(
        2352,
        "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.RECO_INIT}",
        Status.FORBIDDEN,
    ),

    /**
     * Pour la gestion des études de la couverture hydraulique
     */
    ETUDE_TYPE_FORBIDDEN_C(4000, "Vous n'avez pas les droits de création d'une étude.", Status.FORBIDDEN),
    ETUDE_TYPE_FORBIDDEN_U(4001, "Vous n'avez pas les droits pour mettre à jour une étude.", Status.FORBIDDEN),
    ETUDE_DOCUMENT_MEME_NOM(4002, "Les documents d'une même étude ne doivent pas avoir le même nom."),
    ETUDE_CAPACITE_MANQUANTE(4003, "La capacité doit être renseignée."),
    ETUDE_DEBIT_MANQUANT(4004, "Le débit doit être renseigné."),
    ETUDE_DIAMETRE_MANQUANT(4005, "Le diamètre doit être renseigné."),
    ETUDE_DIAMETRE_CANALISATION_MANQUANT(4006, "Le diamètre doit être renseigné."),
    ETUDE_NUMERO_UNIQUE(4007, "Une étude avec ce numéro existe déjà."),
    CALCUL_COUVERTURE_PARAMETRE_PROFONDEUR_MANQUANT(4008, "Le paramètre ${ParametreEnum.PROFONDEUR_COUVERTURE} doit être renseigné."),
    CALCUL_COUVERTURE_DECI_DISTANCE_MAX_PARCOURS_MANQUANT(4009, "Le paramètre ${ParametreEnum.DECI_DISTANCE_MAX_PARCOURS} doit être renseigné."),
    CALCUL_COUVERTURE_DECI_ISODISTANCES_MANQUANT(4010, "Le paramètre ${ParametreEnum.DECI_ISODISTANCES} doit être renseigné."),

    IMPORT_SHP_ETUDE_SHP_INTROUVABLE(4100, "Aucun fichier .shp n'a été trouvé."),
    IMPORT_SHP_ETUDE_GEOMETRIE_NULLE(4101, "La géométrie ne doit pas être nulle."),
    IMPORT_SHP_ETUDE_GEOMETRIE_NULLE_POINT(4102, "La géométrie ne doit pas être nulle et doit être de type Point."),
    IMPORT_SHP_ETUDE_NATURE_DECI(4103, "Le code de la nature DECI doit être renseigné."),
    IMPORT_SHP_ETUDE_TYPE_PEI_PROJET(4104, "Le type du PEI doit être renseigné."),
    IMPORT_SHP_ETUDE_DIAMETRE_MANQUANT(4105, "Le diamètre de canalisation doit être renseigné pour un PIBI."),
    IMPORT_SHP_ETUDE_CAPACITE_MANQUANTE(4106, "La capacité doit être renseignée pour une réserve."),
    IMPORT_SHP_ETUDE_DEBIT_MANQUANT_PA(4107, "Le débit doit être renseigné pour un PA."),
    IMPORT_SHP_ETUDE_DEBIT_MANQUANT_RESERVE(4108, "Le débit doit être renseigné pour une réserve."),
    IMPORT_SHP_TYPE_PEI_ABSENT(4109, "Le type du PEI n'est pas dans la liste : $PLACEHOLDER_ERROR_TYPE"),
    IMPORT_SHP_CODE_NATURE_DECI_ABSENT(
        4110,
        "Le code de la nature DECI n'est pas présent dans la base. Les valeurs possibles sont : $PLACEHOLDER_ERROR_TYPE",
    ),
    IMPORT_SHP_CODE_DIAMETRE_ABSENT(
        4111,
        "Le code du diamètre n'est pas présent dans la base. Les valeurs possibles sont : $PLACEHOLDER_ERROR_TYPE",
    ),

    /***
     * ***********************************************************************
     * Courrier
     * ***********************************************************************
     */
    MODELE_COURRIER_DROIT_FORBIDDEN(5000, "Vous n'avez pas les droits pour générer ce courrier", Status.FORBIDDEN),
    COURRIER_SAISIR_COMMUNE(
        5001,
        "Veuillez spécifier la commune.",
    ),
    COURRIER_SAISIR_CIS(
        5002,
        "Veuillez spécifier le CIS.",
    ),
    COURRIER_SAISIR_GESTIONNAIRE(
        5003,
        "Veuillez spécifier le CIS.",
    ),
    COURRIER_GROUPEMENT_INTROUVABLE(
        5004,
        "Impossble de trouver le groupement associé à la commune.",
    ),
    COURRIER_ORGANISME_COMMUNE(
        5005,
        "Aucun organisme de type COMMUNE ne correspond à la commune sélectionnée. La géométrie de la commune doit être contenu dans celle de l'organisme",
    ),
    COURRIER_MANQUE_MAIRE(
        5006,
        "Aucun destinataire Maire pour cette commune. Vérifier que le contact existe et qu'il a bien le rôle '${GlobalConstants.ROLE_DESTINATAIRE_MAIRE_ROP}'",
        Status.BAD_REQUEST,
    ),

    //
    // ********************************************************************************
    // Tournées
    // ********************************************************************************
    //
    TOURNEE_GESTION_FORBIDDEN(
        3100,
        "Vous n'avez pas les droits de gestion des tournées, nécessaires pour réaliser cette opération",
        Status.FORBIDDEN,
    ),
    TOURNEE_ALREADY_EXISTS(3001, "Une tournée assignée à cet organisme existe déjà sous ce nom"),
    TOURNEE_LECTURE_SEULE(3002, "La tournée est réservée dans l'application mobile, elle ne peut être modifiée"),
    TOURNEE_REMOVE_AFFECTATION_FORBIDDEN(3003, "Vous ne disposez pas des droits suffisants pour désaffecter un utilisateur d'une tournée", Status.FORBIDDEN),
    TOURNEE_FORCER_AVANCEMENT_FORBIDDEN(3004, "Vous ne disposez pas des droits suffisants pour forcer l'avancement d'une tournée", Status.FORBIDDEN),
    TOURNEE_NATURE_DECI(
        3005,
        "Tous les PEI doivent avoir la même nature DECI.",
        Status.BAD_REQUEST,
    ),

    //
    // ********************************************************************************
    // Indisponibilite temporaire
    // ********************************************************************************
    //
    INDISPONIBILITE_TEMPORAIRE_FIN_AVANT_DEBUT(6000, "La date de fin est avant la date de début"),
    INDISPONIBILITE_TEMPORAIRE_INEXISTANTE(6001, "L'indisponibilité temporaire n'existe pas"),
    INDISPONIBILITE_TEMPORAIRE_EN_COURS(6002, "Impossible de supprimer une indisponibilité temporaire en cours"),
    INDISPONIBILITE_TEMPORAIRE_STATUT_INTROUVABLE(6003, "Le statut n'a pas pu être défini", Status.FORBIDDEN),
    INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_CREATE(
        6004,
        "Vous n'avez pas les droits de création des indisponibilités temporaires",
        Status.FORBIDDEN,
    ),
    INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_UPDATE(
        6005,
        "Vous n'avez pas les droits de modification des indisponibilités temporaires",
        Status.FORBIDDEN,
    ),
    INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_DELETE(
        6006,
        "Vous n'avez pas les droits de suppression des indisponibilités temporaires",
        Status.FORBIDDEN,
    ),
    INDISPONIBILITE_TEMPORAIRE_STATUT(6007, "Le statut renseigné n'est pas valide. Il doit correspondre à une de ces valeurs : EN_COURS, PLANIFIEE ou TERMINEE"),

    /*************************************************************************************
     * Carte
     * ***********************************************************************************
     */

    ZONE_COMPETENCE_INTROUVABLE_FORBIDDEN(
        8000,
        "Impossible de trouver la zone de compétence de l'utilisateur.",
        Status.FORBIDDEN,
    ),
    BBOX_GEOMETRIE(
        8001,
        "Impossible de transformer la BBOX en objet Geometry.",
    ),
    ZONE_COMPETENCE_GEOMETRIE_FORBIDDEN(
        8002,
        "La géométrie est hors de votre zone de compétence.",
        Status.FORBIDDEN,
    ),
    BAD_GEOMETRIE(
        8003,
        "Le format de la géométrie n'est pas reconnu.",
        Status.BAD_REQUEST,
    ),

    //
    // ********************************************************************************
    // Diametres
    // ********************************************************************************
    //
    ADMIN_DIAMETRE_FORBIDDEN_INSERT(7000, "Vous n'avez pas les droits de création des diamètres", Status.FORBIDDEN),
    ADMIN_DIAMETRE_FORBIDDEN_DELETE(7001, "Vous n'avez pas les droits de suppression des diamètres", Status.FORBIDDEN),
    ADMIN_DIAMETRE_FORBIDDEN_UPDATE(7002, "Vous n'avez pas les droits de modification des diamètres", Status.FORBIDDEN),
    ADMIN_DIAMETRE_NOTFOUND(7003, "Cette valeur n'existe pas"),
    ADMIN_DIAMETRE_IS_PROTECTED(7004, "Cette valeur est protégée"),

    //
    // ********************************************************************************
    // Nature
    // ********************************************************************************
    //
    ADMIN_NATURE_FORBIDDEN_INSERT(8100, "Vous n'avez pas les droits de création des natures", Status.FORBIDDEN),
    ADMIN_NATURE_FORBIDDEN_UPDATE(8101, "Vous n'avez pas les droits de modification des natures", Status.FORBIDDEN),
    ADMIN_NATURE_IS_PROTECTED(8102, "Cette nature est protégée"),
    ADMIN_NATURE_FORBIDDEN_REMOVAL(8103, "Vous n'avez pas les droits de suppression des natures", Status.FORBIDDEN),
    ADMIN_NATURE_FORBIDDEN_PEI_TYPE(8104, "Le type de PEI doit être PENA ou PENA"),
    ADMIN_FICHE_RESUME_FORBIDDEN(8105, "Vous n'avez pas les droits de modification de la fiche résumé.", Status.FORBIDDEN),
    ADMIN_MODULE_RESUME_FORBIDDEN(8106, "Vous n'avez pas les droits de modification de la page d'accueil.", Status.FORBIDDEN),

    //
    // ********************************************************************************
    // Nomenclatures code - libellé
    // ********************************************************************************
    //
    ADMIN_NOMENC_FORBIDDEN_INSERT(8200, "Vous n'avez pas les droits de création pour cette nomenclature", Status.FORBIDDEN),
    ADMIN_NOMENC_FORBIDDEN_UPDATE(8201, "Vous n'avez pas les droits de modification pour cette nomenclature", Status.FORBIDDEN),
    ADMIN_NOMENC_IS_PROTECTED(8202, "Cette nomenclature est protégée"),
    ADMIN_NOMENC_FORBIDDEN_REMOVAL(8203, "Vous n'avez pas les droits de suppression des nomenclatures", Status.FORBIDDEN),
    ADMIN_NOMENC_SAME_ELEMENT(8204, "Vous ne pouvez pas définir l'objet parent avec la valeur courante"),
    ADMIN_NOMENC_CODE_EXISTS(8205, "Ce code existe déjà pour un autre élément"),

    //
    // ********************************************************************************
    // Gestionnaire
    // ********************************************************************************
    //
    ADMIN_GESTIONNAIRE_CODE_EXISTS(8300, "Ce code existe déjà pour un autre élément"),

    //
    // ********************************************************************************
    // Organisme
    // ********************************************************************************
    //
    ADMIN_ORGANISME_FORBIDDEN_INSERT(9000, "Vous n'avez pas les droits de création des organimes", Status.FORBIDDEN),
    ADMIN_ORGANISME_FORBIDDEN_UPDATE(9001, "Vous n'avez pas les droits de modification des organimes", Status.FORBIDDEN),

    ADMIN_IMPORT_RESSOURCE_FORBIDDEN(9010, "Vous n'avez pas les droits d'importer des ressource", Status.FORBIDDEN),

    //
    // ********************************************************************************
    // Site et gestionnaire
    // ********************************************************************************
    //
    SITE_FORBIDDEN_UPDATE(9100, "Vous n'avez pas les droits de modification des sites", Status.FORBIDDEN),
    SITE_FORBIDDEN_DELETE(9101, "Vous n'avez pas les droits de suppression des sites", Status.FORBIDDEN),
    SITE_USED(9102, "Un ou plusieurs PEI sont rattachés à ce site."),
    GESTIONNAIRE_FORBIDDEN_UPDATE(9103, "Vous n'avez pas les droits de modification des gestionnaires", Status.FORBIDDEN),
    GESTIONNAIRE_FORBIDDEN_INSERT(9104, "Vous n'avez pas les droits de création des gestionnaires", Status.FORBIDDEN),
    GESTIONNAIRE_FORBIDDEN_DELETE(9105, "Vous n'avez pas les droits de suppression des gestionnaires", Status.FORBIDDEN),
    GESTIONNAIRE_USED_IN_PEI(9106, "Un ou plusieurs PEI sont rattachés à ce gestionnaire"),
    GESTIONNAIRE_USED_IN_SITE(9107, "Un ou plusieurs sites sont rattachés à ce gestionnaire"),

    IMPORT_SITES_SHP_INTROUVABLE(9108, "Aucun fichier .shp n'a été trouvé."),
    IMPORT_SITES_GEOMETRIE_NULLE(9109, "La géométrie ne doit pas être nulle."),
    IMPORT_SITES_GEOMETRIE_NULLE_POINT(9110, "La géométrie ne doit pas être nulle et doit être de type Point."),
    IMPORT_SITES_CODE_NULL(9111, "Le code ne doit pas être nul. $PLACEHOLDER_ERROR_TYPE"),
    IMPORT_SITES_LIBELLE_NULL(9112, "Le libellé ne doit pas être nul."),

    // Contact
    CONTACT_FORBIDDEN_UPDATE(9113, "Vous n'avez pas les droits de modification des contacts", Status.FORBIDDEN),
    CONTACT_FORBIDDEN_INSERT(9114, "Vous n'avez pas les droits de création des contacts", Status.FORBIDDEN),
    CONTACT_FORBIDDEN_DELETE(9115, "Vous n'avez pas les droits de suppression des contacts", Status.FORBIDDEN),

    //
    // ********************************************************************************
    // Zones d'intégration
    // ********************************************************************************
    //
    ZONE_INTEGRATION_FORBIDDEN_UPDATE(9200, "Vous n'avez pas les droits de modification des zones d'intégration", Status.FORBIDDEN),
    IMPORT_ZONES_INTEGRATION_SHP_INTROUVABLE(9208, "Aucun fichier .shp n'a été trouvé."),
    IMPORT_ZONES_INTEGRATION_GEOMETRIE_NULLE(9209, "La géométrie ne doit pas être nulle."),
    IMPORT_ZONES_INTEGRATION_GEOMETRIE_NULLE_POINT(9210, "La géométrie ne doit pas être nulle et doit être de type Point."),
    IMPORT_ZONES_INTEGRATION_CODE_NULL(9211, "Le code ne doit pas être nul."),
    IMPORT_ZONES_INTEGRATION_LIBELLE_NULL(9212, "Le libellé ne doit pas être nul."),

    //
    // ********************************************************************************
    // Document
    // ********************************************************************************
    //
    DOCUMENT_HABILITABLE_FORBIDDEN_INSERT(10011, "Vous n'avez pas les droits de création des blocs documents.", Status.FORBIDDEN),
    DOCUMENT_HABILITABLE_FORBIDDEN_UPDATE(10012, "Vous n'avez pas les droits de modification des blocs documents.", Status.FORBIDDEN),
    DOCUMENT_HABILITABLE_FORBIDDEN_DELETE(10013, "Vous n'avez pas les droits de suppression des blocs documents.", Status.FORBIDDEN),
    DOCUMENT_HABILITABLE_DOCUMENT_NOT_FOUND(10014, "Impossible de trouver le document associé au bloc document."),
    DOCUMENT_FORBIDDEN_INSERT(10024, "Vous n'avez pas les droits pour déclarer un PEI.", Status.FORBIDDEN),

    // Utilisateur
    UTILISATEUR_FORBIDDEN(10015, "Vous n'avez pas les droits de gestion des utilisateurs.", Status.FORBIDDEN),
    UTILISATEUR_USERNAME_LENGTH(10016, "L'identifiant doit avoir au minimum 3 caractères."),
    UTILISATEUR_ERROR_INSERT(
        10017,
        "Erreur lors de l'insertion de l'identifiant : $PLACEHOLDER_ERROR_TYPE. Vérifier que l'identifiant ne contient pas des caractères spéciaux.",
    ),
    UTILISATEUR_USERNAME_EXISTS(10018, "L'identifiant saisi est déjà utilisé par un autre utilisateur."),
    UTILISATEUR_EMAIL_EXISTS(10019, "L'adresse email saisie est déjà utilisée par un autre utilisateur."),
    UTILISATEUR_ACTION_EMAIL(10020, "Impossible de créer un utilisateur et de lui envoyer un mail d'initialisation."),
    UTILISATEUR_TOURNEE_RESERVEE(10021, "L'utilisateur a réservé une tournée."),
    UTILISATEUR_SUPPRESSION_KEYCLOAK(10022, "Impossible de supprimer l'utilisateur dans keycloak."),
    UTILISATEUR_MAJ_KEYCLOAK(10023, "Impossible de modifier l'utilisateur dans keycloak."),

    //
    // ********************************************************************************
    // Anomalie
    // ********************************************************************************
    //
    ADMIN_ANOMALIE_FORBIDDEN_INSERT(12001, "Vous n'avez pas les droits de création des anomalies", Status.FORBIDDEN),
    ADMIN_ANOMALIE_FORBIDDEN_UPDATE(12002, "Vous n'avez pas les droits de modification des anomalies", Status.FORBIDDEN),
    ADMIN_ANOMALIE_FORBIDDEN_DELETE(12003, "Vous n'avez pas les droits de suppression des anomalies", Status.FORBIDDEN),
    ADMIN_ANOMALIE_IS_PROTECTED(12004, "Cette anomalie est protégée"),
    ADMIN_ANOMALIE_IN_USE(12005, "Cette anomalie est utilisée"),

    //
    // ********************************************************************************
    // Gestion des droits
    // ********************************************************************************
    //
    PROFIL_DROIT_FORBIDDEN_UPDATE(13001, "Vous n'avez pas les droits de modification de profil de droit", Status.FORBIDDEN),
    PROFIL_DROIT_FORBIDDEN_INSERT(13002, "Vous n'avez pas les droits de création de profil de droit", Status.FORBIDDEN),
    PROFIL_DROIT_FORBIDDEN_DELETE(13003, "Vous n'avez pas les droits de suppression de profil de droit", Status.FORBIDDEN),

    LIEN_PROFIL_FONCTIONNALITE_EXISTS(13011, "La combinaison profil organisme / utilisateur existe déjà", Status.BAD_REQUEST),
    LIEN_PROFIL_FONCTIONNALITE_WRONG_TYPE(13012, "La combinaison profil organisme / utilisateur doit avoir le même type d'organisme", Status.BAD_REQUEST),

    //
    // ********************************************************************************
    // Débits simultanés
    // ********************************************************************************
    //
    DEBIT_SIMULTANE_FORBIDDEN(14000, "Vous n'avez pas les droits de gestion des débits simultanés", Status.FORBIDDEN),
    DEBIT_SIMULTANE_MESURE(14001, "Au moins une mesure doit être rensignée."),
    DEBIT_SIMULTANE_MESURE_PEI(14002, "Une mesure de débit simultané doit concernés au moins 2 PEI."),
    PEI_DELETE_DEBIT_SIMULTANE(
        14003,
        """
        Le PEI que vous tentez de supprimer est lié à un débit simultané.
        """.trimIndent(),
    ),

    ADMIN_RAPPORT_PERSO_FORBIDDEN(15000, "Vous n'avez pas les droits d'administration des rapports personnalisés.", Status.FORBIDDEN),
    ADMIN_RAPPORT_PERSO_CODE_UNIQUE(15001, "Le code doit être unique."),
    ADMIN_RAPPORT_PERSO_REQUETE_INVALID_CUD(15002, "La requête doit être un SELECT (et non CREATE / UPDATE / DELETE / DROP)."),
    ADMIN_RAPPORT_PERSO_REQUETE_INVALID(15003, "La requête n'est pas valide : $PLACEHOLDER_ERROR_TYPE"),
    ADMIN_RAPPORT_PERSO_REQUETE_PARAMETRE_INVALID(15004, " La requête du paramètre n'est pas valide : $PLACEHOLDER_ERROR_TYPE"),
    ADMIN_RAPPORT_PERSO_PARAMETRE_CODE_UNIQUE(15005, "Chaque code des paramètres doit être unique."),
    RAPPORT_PERSO_FORBIDDEN(15006, "Vous n'avez pas les droits pour générer ce rapport", Status.FORBIDDEN),
    RAPPORT_PERSO_SHP(15007, "Impossible de générer le fichier shape : $PLACEHOLDER_ERROR_TYPE"),

    ADMIN_TASK_FORBIDDEN(16000, "Vous n'avez pas les droits d'administration des tâches planifiées.", Status.FORBIDDEN),

    API_SYNCHRO_ERREUR_RESERVATION(20000, "Erreur lors de la réservation des tournées $PLACEHOLDER_ERROR_TYPE"),
    API_SYNCHRO_NO_COMMUNE(20001, "Impossible d'insérer le PEI : il n'est sur aucune commune connue."),

    API_SYNCHRO_PEI_EXISTE(20002, "Le PEI $PLACEHOLDER_ERROR_TYPE est déjà dans le schéma incoming."),
    API_SYNCHRO_PEI_ERROR(20003, "Impossible d'insérer le PEI $PLACEHOLDER_ERROR_TYPE dans incoming."),
    API_SYNCHRO_PEI_FORBIDDEN(20004, "Vous n'avez pas les droits de création de PEI depuis l'application mobile.", Status.FORBIDDEN),
    API_SYNCHRO_GESTIONNAIRE_EXISTE(20005, "Le gestionnaire $PLACEHOLDER_ERROR_TYPE est déjà dans le schéma incoming."),
    API_SYNCHRO_GESTIONNAIRE_ERROR(20006, "Impossible d'insérer le gestionnaire $PLACEHOLDER_ERROR_TYPE dans le schéma incoming."),
    API_SYNCHRO_GESTIONNAIRE_FORBIDDEN(20007, "Vous n'avez pas les droits de création de gestionnaire depuis l'application mobile"),
    API_SYNCHRO_GESTIONNAIRE_CONTACT_NO_EXISTE(20008, "Le gestionnaire associé au contact n'est pas dans REMOcRA : $PLACEHOLDER_ERROR_TYPE"),
    API_SYNCHRO_CONTACT_EXISTE(20009, "Le contact $PLACEHOLDER_ERROR_TYPE est déjà dans le schéma incoming."),
    API_SYNCHRO_CONTACT_ERROR(20010, "Impossible d'insérer le contact $PLACEHOLDER_ERROR_TYPE dans le schéma incoming."),
    API_SYNCHRO_CONTACT_ROLE_NO_EXISTE(20011, "Le contact associé au rôle n'est pas dans REMOcRA : $PLACEHOLDER_ERROR_TYPE"),
    API_SYNCHRO_CONTACT_ROLE_EXISTE(20012, "Le lien entre le contact et rôle est déjà dans le schéma incoming."),
    API_SYNCHRO_CONTACT_ROLE_ERROR(20013, "Impossible d'insérer le contact role dans le schéma incoming."),
    API_SYNCHRO_TOURNEE_EXISTE(20014, "La tournée $PLACEHOLDER_ERROR_TYPE est déjà dans le schéma incoming."),
    API_SYNCHRO_TOURNEE_ERROR(20015, "Impossible d'insérer la tournée $PLACEHOLDER_ERROR_TYPE dans le schéma incoming."),
    API_SYNCHRO_VISITE_EXISTE(20016, "La visite $PLACEHOLDER_ERROR_TYPE est déjà dans le schéma incoming."),
    API_SYNCHRO_VISITE_ERROR(20017, "Impossible d'insérer la visite $PLACEHOLDER_ERROR_TYPE dans le schéma incoming."),
    API_SYNCHRO_VISITE_PEI_NO_REMOCRA(20018, "Le PEI $PLACEHOLDER_ERROR_TYPE n'existe pas."),
    API_SYNCHRO_VISITE_ANOMALIE_NO_REMOCRA(20019, "L'anomalie $PLACEHOLDER_ERROR_TYPE n'existe pas."),
    API_SYNCHRO_VISITE_ANOMALIE_EXISTE(20020, "Le lien entre l'anomalie et la visite est déjà dans le schéma incoming : $PLACEHOLDER_ERROR_TYPE"),
    API_SYNCHRO_VISITE_ANOMALIE_ERROR(20021, "Impossible d'insérer le lien entre l'anomalie et la viste dans le schéma incoming : $PLACEHOLDER_ERROR_TYPE"),
    API_SYNCHRO_PHOTO_EXISTE(20022, "La photo est déjà dans le schéma incoming : $PLACEHOLDER_ERROR_TYPE"),
    API_SYNCHRO_PHOTO_ERROR(20023, "Impossible d'insérer la photo dans le schéma incoming : $PLACEHOLDER_ERROR_TYPE"),

    ADMIN_PARAMETRE_FORBIDDEN(21000, "Vous n'avez pas les droits d'administration", Status.FORBIDDEN),
    ADMIN_PARAMETRE_ISODISTANCE_FORMAT(21001, "Les isodistances n'ont pas le format attendu "),

    //
    // ********************************************************************************
    // Imports CTP
    // ********************************************************************************
    //
    IMPORT_CTP_CODE_INSEE_MANQUANT(22000, "Code INSEE manquant"),
    IMPORT_CTP_NOT_XLSX(22001, "Le fichier fourni n'est pas un .xlsx"),
    IMPORT_CTP_NO_TEMPLATE_PROVIDED(22002, "Aucun modèle d'export CTP n'a été fourni à l'application"),

    //
    // ********************************************************************************
    // Dashboard
    // ********************************************************************************
    //
    DASHBOARD_INVALID_KEYWORD(23000, "Requête invalide : contient un mot-clé interdit."),
    DASHBOARD_INVALID_FIRST_KEYWORD(23001, "Requête invalide : doit commencer par 'SELECT' ou 'WITH'."),
    DASHBOARD_INVALID_MULTIPLE_INSTRUCTION(23002, "Requête invalide : contient des instructions SQL dangereuses (ex. : commentaires ou commandes multiples)."),
    DASHBOARD_NO_TABLE_IN_PARSED_QUERY(23003, "Aucune table trouvée dans la requête"),
    DASHBOARD_NO_SELECT_KEYWORD(23004, "Impossible de trouver la clause SELECT dans la requête originale."),
    DASHBOARD_FIELD_REQUIRE(23005, "Aucune donnée"),
    DASHBOARD_VALIDATE_QUERY(23006, "Erreur de validation de la requête."),
    DASHBOARD_FORBIDDEN_CUD(23007, "Vous n'avez pas les droits de d'éditer", Status.FORBIDDEN),

    //
    // ********************************************************************************
    // Pei Prescrit
    // ********************************************************************************
    //
    PEI_PRESCRIT_FORBIDDEN_INSERT(24000, "Vous n'avez pas les droits de création de prescription de PEI.", Status.FORBIDDEN),
    PEI_PRESCRIT_FORBIDDEN_UPDATE(24001, "Vous n'avez pas les droits de modification de prescription de PEI.", Status.FORBIDDEN),
    PEI_PRESCRIT_FORBIDDEN_DELETE(24002, "Vous n'avez pas les droits de suppression de prescription de PEI.", Status.FORBIDDEN),

    //
    // ********************************************************************************
    // Couches
    // ********************************************************************************
    //
    ADMIN_COUCHES(25000, "Vous n'avez pas les droits de modification des couches", Status.FORBIDDEN),

    // Paramètres API
    DROIT_API_FORBIDDEN(26000, "Vous n'avez pas les droits de modification des droits API", Status.FORBIDDEN),

    //
    // ********************************************************************************
    // OLD
    // ********************************************************************************
    //
    OLDEB_PROPRIETAIRE_FORBIDDEN(27000, "Vous n'avez pas les droits de gestion des propriétaires", Status.FORBIDDEN),
    OLDEB_PROPRIETAIRE_FORBIDDEN_INSERT(27001, "Vous n'avez pas les droits de création des propriétaires", Status.FORBIDDEN),
    OLDEB_PROPRIETAIRE_FORBIDDEN_UPDATE(27002, "Vous n'avez pas les droits de modification des propriétaires", Status.FORBIDDEN),
    OLDEB_PROPRIETAIRE_FORBIDDEN_DELETE(27003, "Vous n'avez pas les droits de suppression des propriétaires", Status.FORBIDDEN),
    OLDEB_PROPRIETAIRE_IN_USE(27004, "Le propriétaire est associé à une ou plusieurs obligations légales de débroussaillement", Status.CONFLICT),
    OLDEB_FORBIDDEN(27005, "Vous n'avez pas les droits de gestion des obligations légales de débrousaillement", Status.FORBIDDEN),
    OLDEB_FORBIDDEN_INSERT(27006, "Vous n'avez pas les droits de création des obligations légales de débrousaillement", Status.FORBIDDEN),
    OLDEB_FORBIDDEN_UPDATE(27007, "Vous n'avez pas les droits de modification des obligations légales de débrousaillement", Status.FORBIDDEN),
    OLDEB_FORBIDDEN_DELETE(27008, "Vous n'avez pas les droits de suppression des obligations légales de débrousaillement", Status.FORBIDDEN),

    ;
    override fun toString(): String {
        return this.code.toString() + " : " + this.libelle
    }
}
