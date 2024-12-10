package remocra.data.enums

/**
 * Différentes erreurs pouvant être levées lors de l'import CTP
 */
enum class ErreurImportCtp(val libelleCourt: String, val gravite: Gravite, val libelleLong: String) {
    ERR_FICHIER_INNAC("Fichier inaccessible", Gravite.ERROR, "Impossible d'ouvrir le fichier"),
    ERR_MAUVAIS_EXT("Mauvaise extension", Gravite.ERROR, "Impossible d'ouvrir le fichier : mauvaise extension"),
    ERR_ONGLET_ABS("Onglet \"Saisies_resultats_CT\" manquant", Gravite.ERROR, "L'onglet \"Saisies_resultats_CT\" n'est pas présent dans le fichier"),
    ERR_MAUVAIS_NUM_PEI("Incohérence entre id PEI et numéro global", Gravite.ERROR, "Le PEI avec le code SDIS renseigné ne correspond à aucun PEI pour cette commune et ce numéro"),
    INFO_IGNORE("Ligne ignorée", Gravite.INFO, "Le contrôle technique de ce PEI n'est pas renseigné."),
    ERR_DEHORS_ZC("PEI en dehors de la zone de compétence de l’AP", Gravite.ERROR, "Le PEI n'est pas dans votre zone de compétence"),
    WARN_DEPLACEMENT("Distance entre ancienne et nouvelle position trop importante", Gravite.WARNING, "Le déplacement d'un PEI est limité, supprimer l'ancien PEI et créer un nouveau PEI"),
    ERR_COORD_GPS("Mauvais format des coordonnées GPS", Gravite.ERROR, "Coordonnées incompatibles au format annoncé"),
    WARN_DATE_ANTE("Date antérieure ou égale à un autre CTP", Gravite.WARNING, "Attention, il existe un CT réalisé plus récent ou à la même date"),
    ERR_DATE_POST("Date postérieure à date du jour", Gravite.ERROR, "La date du CT ne doit pas être postérieure à la date du jour"),
    ERR_FORMAT_DATE("Mauvais format de date", Gravite.ERROR, "La date du CT n'est pas au bon format"),
    ERR_DATE_MANQ("Date de CTP manquante", Gravite.ERROR, "La date du CT doit être renseignée"),
    ERR_AGENT1_ABS("Organisme vérificateur manquant", Gravite.ERROR, "L'organisme doit être renseigné"),
    WARN_PRESS_VIDE("Débit rempli mais pression statique vide", Gravite.WARNING, "La pression statique est obligatoire"),
    ERR_PRESS_ELEVEE("Mauvaise extension", Gravite.ERROR, "Erreur de saisie pression trop élevée"),
    ERR_FORMAT_PRESS("Valeur de pression statique impossible (lettre, signe, …)", Gravite.ERROR, "La pression statique n'est pas au bon format"),
    WARN_DEBIT_VIDE("Pression remplie mais débit vide", Gravite.WARNING, "Débit obligatoire (débit mesuré à 1 bar ou plus)"),
    ERR_FORMAT_DEBIT("Valeur de débit impossible (lettre, signe, …)", Gravite.ERROR, "Le débit mesuré à 1 bar ou plus n'est pas au bon format"),
    WARN_DEB_PRESS_VIDE("Date remplie mais débit/pression vide", Gravite.WARNING, "Sans mesures (Q et Pstat), le CT n'est pas recevable mais sera intégré comme contrôle fonctionnel (CF)"),
    INFO_TRONC_DEBIT("Troncature de la valeur saisie (décimal renseigné)", Gravite.INFO, "Débit avec décimale : la valeur du débit sera arrondie à l'unité inférieure"),
    ERR_ANO_INCONNU("Anomalies inconnues", Gravite.ERROR, "Au moins une anomalie renseignée est inconnue"),
    ERR_VISITES_MANQUANTES("Visites manquantes", Gravite.ERROR, "Le PEI ne dispose pas de visite de réception et de visite de reconnaissance opérationnelle initiale"),
    ERR_VISITE_EXISTANTE("Visite inexistante", Gravite.ERROR, "Une visite existe déjà pour un PEI à une même date et heure"),

    ;

    enum class Gravite { OK, INFO, WARNING, ERROR }
}
