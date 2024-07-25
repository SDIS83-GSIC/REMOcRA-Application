package remocra

object GlobalConstants {
    const val UTILISATEUR_SYSTEME_USERNAME = "UTILISATEUR_SYSTEME"

    // Code du type organisme Service des eaux
    const val SERVICE_EAUX = "SERVICE_EAUX"

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

    const val DOSSIER_DOCUMENT = DOSSIER_DATA + "documents/"

    const val DOSSIER_DOCUMENT_PEI = DOSSIER_DOCUMENT + "pei/"
}
