package remocra.data.enums

enum class ParametreEnum(val section: ParametreSection) {
    // Général
    MENTION_CNIL(ParametreSection.GENERAL),
    MESSAGE_ENTETE(ParametreSection.GENERAL),
    TITRE_PAGE(ParametreSection.GENERAL),
    TOLERANCE_VOIES_METRES(ParametreSection.GENERAL),
    VITESSE_EAU(ParametreSection.GENERAL),
    BANNIERE_CHEMIN(ParametreSection.GENERAL),
    LOGO_CHEMIN(ParametreSection.GENERAL),

    // Mobile
    AFFICHAGE_INDISPO(ParametreSection.MOBILE),
    AFFICHAGE_SYMBOLES_NORMALISES(ParametreSection.MOBILE),
    CARACTERISTIQUE_PENA(ParametreSection.MOBILE),
    CARACTERISTIQUE_PIBI(ParametreSection.MOBILE),
    DUREE_VALIDITE_TOKEN(ParametreSection.MOBILE),
    GESTION_AGENT(ParametreSection.MOBILE),
    MDP_ADMINISTRATEUR(ParametreSection.MOBILE),
    MODE_DECONNECTE(ParametreSection.MOBILE),
    CREATION_PEI_MOBILE(ParametreSection.MOBILE),

    // Cartographie
    COORDONNEES_FORMAT_AFFICHAGE(ParametreSection.CARTOGRAPHIE),

    // Couverture hydraulique
    DECI_DISTANCE_MAX_PARCOURS(ParametreSection.COUVERTURE_HYDRAULIQUE),
    DECI_ISODISTANCES(ParametreSection.COUVERTURE_HYDRAULIQUE),
    PROFONDEUR_COUVERTURE(ParametreSection.COUVERTURE_HYDRAULIQUE),

    // Permis
    PERMIS_TOLERANCE_CHARGEMENT_METRES(ParametreSection.PERMIS),

    // Module Points d'eau
    BUFFER_CARTE(ParametreSection.GENERAL),
    PEI_COLONNES(ParametreSection.PEI),
    PEI_DELAI_CTRL_URGENT(ParametreSection.PEI),
    PEI_DELAI_CTRL_WARN(ParametreSection.PEI),
    PEI_DELAI_RECO_URGENT(ParametreSection.PEI),
    PEI_DELAI_RECO_WARN(ParametreSection.PEI),
    PEI_DEPLACEMENT_DIST_WARN(ParametreSection.PEI),
    PEI_GENERATION_CARTE_TOURNEE(ParametreSection.PEI),
    PEI_HIGHLIGHT_DUREE(ParametreSection.PEI),
    PEI_METHODE_TRI_ALPHANUMERIQUE(ParametreSection.PEI),
    PEI_NOMBRE_HISTORIQUE(ParametreSection.PEI),
    PEI_RENOUVELLEMENT_CTRL_PRIVE(ParametreSection.PEI),
    PEI_RENOUVELLEMENT_CTRL_PUBLIC(ParametreSection.PEI),
    PEI_RENOUVELLEMENT_RECO_PRIVE(ParametreSection.PEI),
    PEI_RENOUVELLEMENT_RECO_PUBLIC(ParametreSection.PEI),
    PEI_TOLERANCE_COMMUNE_METRES(ParametreSection.PEI),
    PEI_RENUMEROTATION_INTERNE_AUTO(ParametreSection.PEI),
    VOIE_SAISIE_LIBRE(ParametreSection.PEI),
    TYPE_VISITE_CDP(ParametreSection.PEI),

    ;

    enum class ParametreSection {
        CARTOGRAPHIE, COUVERTURE_HYDRAULIQUE, GENERAL, MOBILE, PERMIS, PEI
    }
}
