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
    PEI_INEXISTANT(1000, "Le numéro spécifié ne correspond à aucun hydrant"),
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

    VISITE_TYPE_FORBIDDEN(2200, "Ce type de visite n'est pas accessible pour votre organisme sur ce PEI", Status.FORBIDDEN),
    VISITE_ORGANISME_FORBIDDEN(2201, "Votre organisme n'est pas autorisé à modifier une visite de ce type sur ce PEI", Status.FORBIDDEN),

    ;

    override fun toString(): String {
        return this.code.toString() + " : " + this.libelle
    }
}
