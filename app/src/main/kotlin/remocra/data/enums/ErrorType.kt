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

    PEI_INEXISTANT(1000, "Le numéro spécifié ne correspond à aucun hydrant"),
    PEI_FORBIDDEN_C(1001, "Vous n'avez pas les droits de création de PEI.", Status.FORBIDDEN),
    PEI_FORBIDDEN_U(1002, "Vous n'avez pas les droits de modification de PEI", Status.FORBIDDEN),

    FORBIDDEN(1300, "Le numéro spécifié ne correspond à aucun hydrant qui vous est accessible", Status.FORBIDDEN),
    BAD_PATTERN(1010, "La date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm"),

    //
    // ********************************************************************************
    // Visites
    // ********************************************************************************
    //
    CODE_TYPE_VISITE_INEXISTANT(2001, "Le type de visite spécifié n'existe pas"),
    VISITE_ANO_NON_DISPO(2002, "Une ou plusieurs anomalies contrôlées n'existent pas ou ne sont pas disponibles pour une visite de ce type"),
    VISITE_INEXISTANTE(2003, "Aucune visite avec cet identifiant n'a été trouvée pour le numéro de PEI spécifié"),

    VISITE_EXISTE(2100, "Une visite est déjà présente à ce moment pour ce PEI"),
    VISITE_RECEPTION(2101, "Le type de visite doit être de type ${TypeVisite.RECEPTION} (première visite du PEI)"),
    VISITE_RECO_INIT(2102, "Le type de visite doit être de type ${TypeVisite.RECO_INIT} (deuxième visite du PEI)"),
    VISITE_MEME_TYPE_EXISTE(2103, "Une visite de ce type existe déjà. Veuillez utiliser une visite de type ${TypeVisite.NP}, ${TypeVisite.RECOP} ou ${TypeVisite.CTP}"),
    VISITE_ANO_CONSTATEE(2104, "Une ou plusieurs anomalies ont été marquées constatées sans avoir été contrôlées"),
    VISITE_DEBIT_INF_0(2105, "Le débit ne peut être inférieur à 0"),
    VISITE_DEBIT_MAX_INF_0(2106, "Le débit maximum ne peut être inférieur à 0"),
    VISITE_PRESSION_INF_0(2107, "La pression ne peut être inférieure à 0"),
    VISITE_PRESSION_DYN_INF_0(2108, "La pression dynamique ne peut être inférieure à 0"),
    VISITE_PRESSION_DYN_DEBIT_MAX_INF_0(2109, "La pression dynamique au débit maximum ne peut être inférieure à 0"),
    VISITE_UPDATE_NOT_LAST(2110, "Modification de la visite impossible : une visite plus récente est présente"),
    VISITE_AFTER_NOW(2111, "Impossible de créer une visite dans le futur"),
    VISITE_CREATE_NOT_LAST(2112, "La date renseignée est antérieure à la dernière visite de ce PEI, les visites doivent être saisies par ordre chronologique."),
    VISITE_CDP_INVALIDE(2113, "Au moins une valeur technique doit être saisie pour un contrôle débit pression valide.", Status.BAD_REQUEST),
    VISITE_DELETE_NOT_LAST(2114, "La visite que vous essayez de supprimer n'est pas la dernière en date de ce PEI"),

    VISITE_TYPE_FORBIDDEN(2200, "Ce type de visite n'est pas accessible pour votre organisme sur ce PEI", Status.FORBIDDEN),
    VISITE_ORGANISME_FORBIDDEN(2201, "Votre organisme n'est pas autorisé à modifier une visite de ce type sur ce PEI", Status.FORBIDDEN),

    VISITE_C_FORBIDDEN(2101, "Vous n'avez pas les droits suffisant pour créer une visite", Status.FORBIDDEN),
    VISITE_D_FORBIDDEN(2102, "Vous n'avez pas les droits suffisant pour supprimer une visite", Status.FORBIDDEN),
    VISITE_C_CTP_FORBIDDEN(2311, "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.CTP}", Status.FORBIDDEN),
    VISITE_D_CTP_FORBIDDEN(2312, "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.CTP}", Status.FORBIDDEN),
    VISITE_C_NP_FORBIDDEN(2321, "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.NP}", Status.FORBIDDEN),
    VISITE_D_NP_FORBIDDEN(2322, "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.NP}", Status.FORBIDDEN),
    VISITE_C_RECEPTION_FORBIDDEN(2331, "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.RECEPTION}", Status.FORBIDDEN),
    VISITE_D_RECEPTION_FORBIDDEN(2332, "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.RECEPTION}", Status.FORBIDDEN),
    VISITE_C_RECOP_FORBIDDEN(2341, "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.RECOP}", Status.FORBIDDEN),
    VISITE_D_RECOP_FORBIDDEN(2342, "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.RECOP}", Status.FORBIDDEN),
    VISITE_C_RECO_INIT_FORBIDDEN(2351, "Vous n'avez pas les droits suffisant pour créer une visite de type ${TypeVisite.RECO_INIT}", Status.FORBIDDEN),
    VISITE_D_RECO_INIT_FORBIDDEN(2352, "Vous n'avez pas les droits suffisant pour supprimer une visite de type ${TypeVisite.RECO_INIT}", Status.FORBIDDEN),

    MODELE_COURRIER_DROIT_FORBIDDEN(5000, "Vous n'avez pas les droits pour générer ce courrier"),

    //
    // ********************************************************************************
    // Tournées
    // ********************************************************************************
    //
    TOURNEE_GESTION_FORBIDDEN(3100, "Vous n'avez pas les droits de gestion des tournées, nécessaires pour réaliser cette opération", Status.FORBIDDEN),
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
    INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_CREATE(6004, "Vous n'avez pas les droits de création des indisponibilités temporaires", Status.FORBIDDEN),
    INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_UPDATE(6005, "Vous n'avez pas les droits de modification des indisponibilités temporaires", Status.FORBIDDEN),
    INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_DELETE(6006, "Vous n'avez pas les droits de suppression des indisponibilités temporaires", Status.FORBIDDEN),
    ;

    override fun toString(): String {
        return this.code.toString() + " : " + this.libelle
    }
}
