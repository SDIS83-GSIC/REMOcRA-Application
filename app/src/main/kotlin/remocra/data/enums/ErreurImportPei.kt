package remocra.data.enums

import remocra.data.enums.ErreurImportPei.Gravite

/**
 * Différentes erreurs pouvant être levées lors de l'import PEI
 */
enum class ErreurImportPei(val libelleCourt: String, val gravite: Gravite, val libelleLong: String) {
    ERR_FICHIER_INACCESSIBLE("Fichier inaccessible", Gravite.ERROR, "Impossible d'ouvrir le fichier"),
    ERR_EPSG_MANQUANT("EPSG manquant", Gravite.ERROR, "L'EPSG est manquant ou invalide."),
    ERR_EPSG_NORMALISATION("EPSG normalisation", Gravite.ERROR, "Erreur lors de la transformation de SRID."),
    ERR_COMPUTE_NUMERO("Numéro à recalculer", Gravite.WARNING, "Le numéro du PEI sera recalculé si nécessaire"),
    ERR_DATE_MAL_FORMEE("Date mal formée", Gravite.WARNING, "La date du PEI est mal formée"),
    ERR_PEI_INEXISTANT("PEI inexistant", Gravite.ERROR, "Le PEI n'existe pas"),
    ERR_PEI_ZONE_COMPETENCE("Le PEI sort de la zone de compétence", Gravite.ERROR, "Le déplacement du PEI sort de la zone de compétence de l'utilisateur."),
    ;

    enum class Gravite { OK, WARNING, ERROR }
}
