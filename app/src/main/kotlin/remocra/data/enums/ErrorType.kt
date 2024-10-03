package remocra.data.enums

import jakarta.ws.rs.core.Response.Status
import remocra.db.jooq.remocra.enums.TypeVisite

/**
 * Enumération des erreurs pouvant être déclenchées au travers de l'utilisation de l'application. <br />
 * Le *code* servira dans l'API, qui le remonte systématiquement.
 * Le *status* permet d'interpréter proprement l'exception au moyen d'un *wrap* générique, il est par défaut à [Status.BAD_REQUEST]
 *
 */
enum class ErrorType(val code: Int, val libelle: String, val status: Status = Status.BAD_REQUEST) {
    // ********************************************************************************
    // Erreurs générales
    // ********************************************************************************
    BAD_UUID(100, "Cette chaîne ne représente pas un UUID valide"),

    /**
     * Pour la gestion des PEI
     */
    PEI_INEXISTANT(1000, "Le numéro spécifié ne correspond à aucun hydrant"),
    PEI_FORBIDDEN_C(1001, "Vous n'avez pas les droits de création de PEI.", Status.FORBIDDEN),
    PEI_FORBIDDEN_U(1002, "Vous n'avez pas les droits de modification de PEI", Status.FORBIDDEN),
    PEI_FORBIDDEN_D(1003, "Vous n'avez pas les droits de suppression de PEI", Status.FORBIDDEN),
    PEI_DOCUMENT_MEME_NOM(1004, "Les documents d'un même PEI ne doivent pas avoir le même nom."),
    PEI_DOCUMENT_PHOTO(1005, "Un seul document peut représenter la photo du PEI"),
    PEI_FORBIDDEN_ZONE_COMPETENCE(1006, "Le PEI n'est pas dans votre zone de compétence", Status.FORBIDDEN),
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

    IMPORT_SHP_ETUDE_SHP_INTROUVABLE(4100, "Aucun fichier .shp n'a été trouvé."),
    IMPORT_SHP_ETUDE_GEOMETRIE_NULLE(4101, "La géométrie ne doit pas être nulle."),
    IMPORT_SHP_ETUDE_GEOMETRIE_NULLE_POINT(4102, "La géométrie ne doit pas être nulle et doit être de type Point."),
    IMPORT_SHP_ETUDE_NATURE_DECI(4103, "Le code de la nature DECI doit être renseigné."),
    IMPORT_SHP_ETUDE_TYPE_PEI_PROJET(4104, "Le type du PEI doit être renseigné."),
    IMPORT_SHP_ETUDE_DIAMETRE_MANQUANT(4105, "Le diamètre de canalisation doit être renseigné pour un PIBI."),
    IMPORT_SHP_ETUDE_CAPACITE_MANQUANTE(4106, "La capacité doit être renseignée pour une réserve."),
    IMPORT_SHP_ETUDE_DEBIT_MANQUANT_PA(4107, "Le débit doit être renseigné pour un PA."),
    IMPORT_SHP_ETUDE_DEBIT_MANQUANT_RESERVE(4108, "Le débit doit être renseigné pour une réserve."),
    ETUDE_NUMERO_UNIQUE(4007, "Une étude avec ce numéro existe déjà."),

    ETUDE_CAPACITE_MANQUANTE(4003, "La capacité doit être renseignée."),
    ETUDE_DEBIT_MANQUANT(4004, "Le débit doit être renseigné."),
    ETUDE_DIAMETRE_MANQUANT(4005, "Le diamètre doit être renseigné."),
    ETUDE_DIAMETRE_CANALISATION_MANQUANT(4006, "Le diamètre doit être renseigné."),

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

    ADMIN_DIAMETRE_FORBIDDEN_INSERT(7000, "Vous n'avez pas les droits de création des diamètres", Status.FORBIDDEN),
    ADMIN_DIAMETRE_FORBIDDEN_DELETE(7001, "Vous n'avez pas les droits de suppression des diamètres", Status.FORBIDDEN),
    ADMIN_DIAMETRE_FORBIDDEN_UPDATE(7002, "Vous n'avez pas les droits de modification des diamètres", Status.FORBIDDEN),
    ADMIN_DIAMETRE_NOTFOUND(7003, "Cette valeur n'existe pas"),
    ADMIN_DIAMETRE_IS_PROTECTED(7004, "Cette valeur est protégée"),

    ;

    override fun toString(): String {
        return this.code.toString() + " : " + this.libelle
    }
}
