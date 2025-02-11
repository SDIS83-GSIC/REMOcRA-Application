enum TYPE_DROIT {
  ADMIN_API = "ADMIN_API",
  ADMIN_DROITS = "ADMIN_DROITS",
  ADMIN_PARAM_APPLI = "ADMIN_PARAM_APPLI",
  ADMIN_PARAM_APPLI_MOBILE = "ADMIN_PARAM_APPLI_MOBILE",
  ADMIN_PARAM_TRAITEMENTS = "ADMIN_PARAM_TRAITEMENTS",
  ADMIN_RAPPORTS_PERSO = "ADMIN_RAPPORTS_PERSO",
  ADMIN_UTILISATEURS_A = "ADMIN_UTILISATEURS_A",
  ADMIN_UTILISATEURS_R = "ADMIN_UTILISATEURS_R",
  ADMIN_UTILISATEURS_ORGA_A = "ADMIN_UTILISATEURS_ORGA_A",
  ADMIN_UTILISATEURS_ORGA_R = "ADMIN_UTILISATEURS_ORGA_R",
  ADRESSES_C = "ADRESSES_C",
  ALERTES_EXPORT_C = "ALERTES_EXPORT_C",
  CARTOGRAPHIES_E = "CARTOGRAPHIES_E",
  COURRIER_ADMIN_R = "COURRIER_ADMIN_R",
  COURRIER_C = "COURRIER_C",
  COURRIER_ORGANISME_R = "COURRIER_ORGANISME_R",
  COURRIER_UTILISATEUR_R = "COURRIER_UTILISATEUR_R",
  CRISE_C = "CRISE_C",
  CRISE_D = "CRISE_D",
  CRISE_R = "CRISE_R",
  CRISE_U = "CRISE_U",
  DASHBOARD_A = "DASHBOARD_A",
  DASHBOARD_R = "DASHBOARD_R",
  DEBITS_SIMULTANES_A = "DEBITS_SIMULTANES_A",
  DEBITS_SIMULTANES_R = "DEBITS_SIMULTANES_R",
  DECLARATION_PEI = "DECLARATION_PEI",
  DEPOT_DELIB_C = "DEPOT_DELIB_C",
  DFCI_EXPORTATLAS_C = "DFCI_EXPORTATLAS_C",
  DFCI_R = "DFCI_R",
  DFCI_RECEPTRAVAUX_C = "DFCI_RECEPTRAVAUX_C",
  DOCUMENTS_R = "DOCUMENTS_R",
  DOCUMENTS_A = "DOCUMENTS_A",
  ETUDE_C = "ETUDE_C",
  ETUDE_D = "ETUDE_D",
  ETUDE_R = "ETUDE_R",
  ETUDE_U = "ETUDE_U",
  GEST_SITE_A = "GEST_SITE_A",
  GEST_SITE_R = "GEST_SITE_R",
  IMPORT_CTP_A = "IMPORT_CTP_A",
  IMPORT_CTP_PEI_DEPLACEMENT_U = "IMPORT_CTP_PEI_DEPLACEMENT_U",
  INDISPO_TEMP_C = "INDISPO_TEMP_C",
  INDISPO_TEMP_D = "INDISPO_TEMP_D",
  INDISPO_TEMP_R = "INDISPO_TEMP_R",
  INDISPO_TEMP_U = "INDISPO_TEMP_U",
  MOBILE_GESTIONNAIRE_C = "MOBILE_GESTIONNAIRE_C",
  MOBILE_PEI_C = "MOBILE_PEI_C",
  OLDEB_C = "OLDEB_C",
  OLDEB_D = "OLDEB_D",
  OLDEB_R = "OLDEB_R",
  OLDEB_U = "OLDEB_U",
  OPERATIONS_DIVERSES_E = "OPERATIONS_DIVERSES_E",
  PEI_ADRESSE_C = "PEI_ADRESSE_C",
  PEI_C = "PEI_C",
  PEI_CARACTERISTIQUES_U = "PEI_CARACTERISTIQUES_U",
  PEI_D = "PEI_D",
  PEI_DEPLACEMENT_U = "PEI_DEPLACEMENT_U",
  PEI_GESTIONNAIRE_C = "PEI_GESTIONNAIRE_C",
  PEI_NUMERO_INTERNE_U = "PEI_NUMERO_INTERNE_U",
  PEI_PRESCRIT_A = "PEI_PRESCRIT_A",
  PEI_PRESCRIT_R = "PEI_PRESCRIT_R",
  PEI_R = "PEI_R",
  PEI_U = "PEI_U",
  PERMIS_A = "PERMIS_A",
  PERMIS_DOCUMENTS_C = "PERMIS_DOCUMENTS_C",
  PERMIS_R = "PERMIS_R",
  PERMIS_TRAITEMENT_E = "PERMIS_TRAITEMENT_E",
  RAPPORT_PERSONNALISE_E = "RAPPORT_PERSONNALISE_E",
  RCCI_A = "RCCI_A",
  RCCI_R = "RCCI_R",
  RISQUES_KML_D = "RISQUES_KML_D",
  RISQUES_KML_R = "RISQUES_KML_R",
  TOURNEE_A = "TOURNEE_A",
  TOURNEE_FORCER_POURCENTAGE_E = "TOURNEE_FORCER_POURCENTAGE_E",
  TOURNEE_R = "TOURNEE_R",
  TOURNEE_RESERVATION_D = "TOURNEE_RESERVATION_D",
  TRAITEMENTS_E = "TRAITEMENTS_E",
  TRAITEMENTS_PEI_E = "TRAITEMENTS_PEI_E",
  VISITE_R = "VISITE_R",
  VISITE_CONTROLE_TECHNIQUE_C = "VISITE_CONTROLE_TECHNIQUE_C",
  VISITE_CTP_D = "VISITE_CTP_D",
  VISITE_NON_PROGRAMME_C = "VISITE_NON_PROGRAMME_C",
  VISITE_NP_D = "VISITE_NP_D",
  VISITE_RECEP_C = "VISITE_RECEP_C",
  VISITE_RECEP_D = "VISITE_RECEP_D",
  VISITE_RECO_C = "VISITE_RECO_C",
  VISITE_RECO_D = "VISITE_RECO_D",
  VISITE_RECO_INIT_C = "VISITE_RECO_INIT_C",
  VISITE_RECO_INIT_D = "VISITE_RECO_INIT_D",
  ZOOM_LIEU_R = "ZOOM_LIEU_R",
}
export default TYPE_DROIT;

export enum SECTION_DROIT {
  ADRESSE = "Adresse",
  CARTO = "Cartographie",
  COUVERTURE_HYDRAULIQUE = "Couverture hydraulique",
  CRISE = "Gestion de crise",
  DFCI = "DFCI",
  GENERAL = "Général",
  GESTIONNAIRE = "Gestionnaire",
  MOBILE = "Mobile",
  OLDEB = "Obligation Légale de Débroussaillement (OLDEB)",
  PEI = "Point d'eau",
  PERMIS = "Permis",
  RCCI = "Recherche des Causes et des Circonstances d'Incendie (RCCI)",
  RISQUE = "Risque",
}

export const TypeDroitSection = new Map<TYPE_DROIT, SECTION_DROIT>([
  [TYPE_DROIT.ADMIN_API, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ADMIN_DROITS, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ADMIN_PARAM_APPLI, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ADMIN_PARAM_APPLI_MOBILE, SECTION_DROIT.MOBILE],
  [TYPE_DROIT.ADMIN_PARAM_TRAITEMENTS, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ADMIN_RAPPORTS_PERSO, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ADMIN_UTILISATEURS_A, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ADMIN_UTILISATEURS_R, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ADMIN_UTILISATEURS_ORGA_A, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ADMIN_UTILISATEURS_ORGA_R, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ADRESSES_C, SECTION_DROIT.ADRESSE],
  [TYPE_DROIT.ALERTES_EXPORT_C, SECTION_DROIT.ADRESSE],
  [TYPE_DROIT.CARTOGRAPHIES_E, SECTION_DROIT.CARTO],
  [TYPE_DROIT.COURRIER_ADMIN_R, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.COURRIER_C, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.COURRIER_ORGANISME_R, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.COURRIER_UTILISATEUR_R, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.CRISE_C, SECTION_DROIT.CRISE],
  [TYPE_DROIT.CRISE_D, SECTION_DROIT.CRISE],
  [TYPE_DROIT.CRISE_R, SECTION_DROIT.CRISE],
  [TYPE_DROIT.CRISE_U, SECTION_DROIT.CRISE],
  [TYPE_DROIT.DASHBOARD_A, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.DEBITS_SIMULTANES_A, SECTION_DROIT.PEI],
  [TYPE_DROIT.DEBITS_SIMULTANES_R, SECTION_DROIT.PEI],
  [TYPE_DROIT.DECLARATION_PEI, SECTION_DROIT.PEI],
  [TYPE_DROIT.DEPOT_DELIB_C, SECTION_DROIT.ADRESSE],
  [TYPE_DROIT.DFCI_EXPORTATLAS_C, SECTION_DROIT.DFCI],
  [TYPE_DROIT.DFCI_R, SECTION_DROIT.DFCI],
  [TYPE_DROIT.DFCI_RECEPTRAVAUX_C, SECTION_DROIT.DFCI],
  [TYPE_DROIT.DOCUMENTS_R, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.ETUDE_C, SECTION_DROIT.COUVERTURE_HYDRAULIQUE],
  [TYPE_DROIT.ETUDE_D, SECTION_DROIT.COUVERTURE_HYDRAULIQUE],
  [TYPE_DROIT.ETUDE_R, SECTION_DROIT.COUVERTURE_HYDRAULIQUE],
  [TYPE_DROIT.ETUDE_U, SECTION_DROIT.COUVERTURE_HYDRAULIQUE],
  [TYPE_DROIT.GEST_SITE_A, SECTION_DROIT.GESTIONNAIRE],
  [TYPE_DROIT.GEST_SITE_R, SECTION_DROIT.GESTIONNAIRE],
  [TYPE_DROIT.IMPORT_CTP_A, SECTION_DROIT.PEI],
  [TYPE_DROIT.IMPORT_CTP_PEI_DEPLACEMENT_U, SECTION_DROIT.PEI],
  [TYPE_DROIT.INDISPO_TEMP_C, SECTION_DROIT.PEI],
  [TYPE_DROIT.INDISPO_TEMP_D, SECTION_DROIT.PEI],
  [TYPE_DROIT.INDISPO_TEMP_R, SECTION_DROIT.PEI],
  [TYPE_DROIT.INDISPO_TEMP_U, SECTION_DROIT.PEI],
  [TYPE_DROIT.MOBILE_GESTIONNAIRE_C, SECTION_DROIT.MOBILE],
  [TYPE_DROIT.MOBILE_PEI_C, SECTION_DROIT.MOBILE],
  [TYPE_DROIT.OLDEB_C, SECTION_DROIT.OLDEB],
  [TYPE_DROIT.OLDEB_D, SECTION_DROIT.OLDEB],
  [TYPE_DROIT.OLDEB_R, SECTION_DROIT.OLDEB],
  [TYPE_DROIT.OLDEB_U, SECTION_DROIT.OLDEB],
  [TYPE_DROIT.OPERATIONS_DIVERSES_E, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.PEI_ADRESSE_C, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_C, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_CARACTERISTIQUES_U, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_D, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_DEPLACEMENT_U, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_GESTIONNAIRE_C, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_NUMERO_INTERNE_U, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_PRESCRIT_A, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_PRESCRIT_R, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_R, SECTION_DROIT.PEI],
  [TYPE_DROIT.PEI_U, SECTION_DROIT.PEI],
  [TYPE_DROIT.PERMIS_A, SECTION_DROIT.PERMIS],
  [TYPE_DROIT.PERMIS_DOCUMENTS_C, SECTION_DROIT.PERMIS],
  [TYPE_DROIT.PERMIS_R, SECTION_DROIT.PERMIS],
  [TYPE_DROIT.PERMIS_TRAITEMENT_E, SECTION_DROIT.PERMIS],
  [TYPE_DROIT.RCCI_A, SECTION_DROIT.RCCI],
  [TYPE_DROIT.RCCI_R, SECTION_DROIT.RCCI],
  [TYPE_DROIT.RISQUES_KML_D, SECTION_DROIT.RISQUE],
  [TYPE_DROIT.RISQUES_KML_R, SECTION_DROIT.RISQUE],
  [TYPE_DROIT.TOURNEE_A, SECTION_DROIT.PEI],
  [TYPE_DROIT.TOURNEE_FORCER_POURCENTAGE_E, SECTION_DROIT.PEI],
  [TYPE_DROIT.TOURNEE_R, SECTION_DROIT.PEI],
  [TYPE_DROIT.TOURNEE_RESERVATION_D, SECTION_DROIT.PEI],
  [TYPE_DROIT.TRAITEMENTS_E, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.TRAITEMENTS_PEI_E, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_R, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_CONTROLE_TECHNIQUE_C, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_CTP_D, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_NON_PROGRAMME_C, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_NP_D, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_RECEP_C, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_RECEP_D, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_RECO_C, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_RECO_D, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_RECO_INIT_C, SECTION_DROIT.PEI],
  [TYPE_DROIT.VISITE_RECO_INIT_D, SECTION_DROIT.PEI],
  [TYPE_DROIT.ZOOM_LIEU_R, SECTION_DROIT.GENERAL],
  [TYPE_DROIT.DOCUMENTS_A, SECTION_DROIT.GENERAL],
]);

export const TypeDroitLabel = new Map<string, string>([
  [TYPE_DROIT.ADMIN_API, "Administrer l'API"],
  [TYPE_DROIT.ADMIN_DROITS, "Gestion des droits"],
  [TYPE_DROIT.ADMIN_PARAM_APPLI, "Administrer les paramètres de l'application"],
  [
    TYPE_DROIT.ADMIN_PARAM_APPLI_MOBILE,
    "Administrer les paramètres de l'application mobile",
  ],
  [
    TYPE_DROIT.ADMIN_PARAM_TRAITEMENTS,
    "Administrer les transferts automatisés",
  ],
  [TYPE_DROIT.ADMIN_RAPPORTS_PERSO, "Administrer les rapports personnalisés"],
  [TYPE_DROIT.ADMIN_UTILISATEURS_A, "Administrer tous les utilisateurs"],
  [TYPE_DROIT.ADMIN_UTILISATEURS_R, "Consulter tous les utilisateurs"],
  [
    TYPE_DROIT.ADMIN_UTILISATEURS_ORGA_A,
    "Administrer les utilisateurs de son organisme",
  ],
  [
    TYPE_DROIT.ADMIN_UTILISATEURS_ORGA_R,
    "Consulter les utilisateurs de son organisme",
  ],

  [TYPE_DROIT.ADRESSES_C, "Créer, éditer les adresses"],
  [TYPE_DROIT.ALERTES_EXPORT_C, "Créer, exporter les alertes"],
  [TYPE_DROIT.CARTOGRAPHIES_E, "Accéder au module cartographie"],
  [TYPE_DROIT.COURRIER_ADMIN_R, "Consulter tous les courriers"],
  [TYPE_DROIT.COURRIER_C, "Créer, éditer des courriers"],
  [TYPE_DROIT.COURRIER_ORGANISME_R, "Consulter les courriers de son organisme"],
  [TYPE_DROIT.COURRIER_UTILISATEUR_R, "Consulter ses courriers"],
  [TYPE_DROIT.CRISE_C, "Créer une nouvelle crise"],
  [TYPE_DROIT.CRISE_D, "Supprimer une crise"],
  [TYPE_DROIT.CRISE_R, "Lister les crises"],
  [TYPE_DROIT.CRISE_U, "Modifier les informations associées à une crise"],
  [TYPE_DROIT.DASHBOARD_A, "Consulter le tableau de bord"],
  [TYPE_DROIT.DEBITS_SIMULTANES_A, "Créer, éditer les débits simultanés"],
  [TYPE_DROIT.DEBITS_SIMULTANES_R, "Consulter les débits simultanés"],
  [TYPE_DROIT.DECLARATION_PEI, "Déposer un dossier de déclaration de PEI"],
  [TYPE_DROIT.DEPOT_DELIB_C, "Déposer des délibérations"],
  [TYPE_DROIT.DFCI_EXPORTATLAS_C, "Télécharger l'Atlas DFCI"],
  [TYPE_DROIT.DFCI_R, "Consulter la carte DFCI"],
  [
    TYPE_DROIT.DFCI_RECEPTRAVAUX_C,
    "Déposer des dossiers de réception de travaux",
  ],
  [TYPE_DROIT.DOCUMENTS_R, "Consulter les documents"],
  [TYPE_DROIT.ETUDE_C, "Créer une étude"],
  [TYPE_DROIT.ETUDE_D, "Supprimer une étude"],
  [TYPE_DROIT.ETUDE_R, "Consulter une étude"],
  [TYPE_DROIT.ETUDE_U, "Modifier une étude"],
  [
    TYPE_DROIT.GEST_SITE_A,
    "Administrer les gestionnaires et leurs sites dans les écrans de gestion",
  ],
  [
    TYPE_DROIT.GEST_SITE_R,
    "Consulter les gestionnaires et leurs sites dans les écrans de gestion",
  ],
  [TYPE_DROIT.IMPORT_CTP_A, "Importer des fichiers CTP"],
  [
    TYPE_DROIT.IMPORT_CTP_PEI_DEPLACEMENT_U,
    "Modification des coordonnées dans l'import des CTP",
  ],
  [
    TYPE_DROIT.INDISPO_TEMP_C,
    "Créer des indisponibilités temporaires (soumis au territoire, organisé par commune)",
  ],
  [
    TYPE_DROIT.INDISPO_TEMP_D,
    "Supprimer des indisponibilités temporaires (soumis au territoire, organisé par commune)",
  ],
  [
    TYPE_DROIT.INDISPO_TEMP_R,
    "Consulter les indisponibilités temporaires (soumis au territoire, organisé par commune)",
  ],
  [
    TYPE_DROIT.INDISPO_TEMP_U,
    "Activer/lever des indisponibilités temporaires (soumis au territoire, organisé par commune)",
  ],
  [
    TYPE_DROIT.MOBILE_GESTIONNAIRE_C,
    "Créer, éditer les gestonnaires pour l'application mobile",
  ],
  [TYPE_DROIT.MOBILE_PEI_C, "Créer, éditer un PEI pour l'application mobile"],
  [TYPE_DROIT.OLDEB_C, "Créer des obligations"],
  [TYPE_DROIT.OLDEB_D, "Supprimer des obligations"],
  [TYPE_DROIT.OLDEB_R, "Consulter des obligations"],
  [TYPE_DROIT.OLDEB_U, "Modifier des obligations"],
  [
    TYPE_DROIT.OPERATIONS_DIVERSES_E,
    "Accéder au module des opérations diverses",
  ],
  [TYPE_DROIT.PEI_ADRESSE_C, "Créer, éditer l'adresse des PEI"],
  [TYPE_DROIT.PEI_C, "Créer un PEI"],
  [
    TYPE_DROIT.PEI_CARACTERISTIQUES_U,
    "Modifier les caractéristiques techniques",
  ],
  [TYPE_DROIT.PEI_D, "Supprimer des PEI"],
  [TYPE_DROIT.PEI_DEPLACEMENT_U, "Déplacer des PEI"],
  [TYPE_DROIT.PEI_GESTIONNAIRE_C, "Créer des gestionnaires des PEI"],
  [TYPE_DROIT.PEI_NUMERO_INTERNE_U, "Saisir le numéro interne d'un PEI"],
  [
    TYPE_DROIT.PEI_PRESCRIT_A,
    "Créer, éditer, supprimer les points d'eau prescrits",
  ],
  [TYPE_DROIT.PEI_PRESCRIT_R, "Consulter les points d'eau prescrits"],
  [TYPE_DROIT.PEI_R, "Consulter les PEI"],
  [TYPE_DROIT.PEI_U, "Modifier un PEI"],
  [TYPE_DROIT.PERMIS_A, "Créer, éditer, supprimer les permis"],
  [
    TYPE_DROIT.PERMIS_DOCUMENTS_C,
    "Déposer des documents rattachés à un permis",
  ],
  [TYPE_DROIT.PERMIS_R, "Consulter les permis (carte et recherches)"],
  [
    TYPE_DROIT.PERMIS_TRAITEMENT_E,
    "Exécuter des traitements en lien avec la thématique Permis",
  ],
  [TYPE_DROIT.RCCI_A, "Créer, éditer, supprimer les RCCI"],
  [TYPE_DROIT.RCCI_R, "Consulter les RCCI"],
  [TYPE_DROIT.RISQUES_KML_D, "TODO"],
  [TYPE_DROIT.RISQUES_KML_R, "TODO"],
  [TYPE_DROIT.TOURNEE_A, "Créer, éditer, supprimer des tournées"],
  [
    TYPE_DROIT.TOURNEE_FORCER_POURCENTAGE_E,
    "Forcer le pourcentage des tournées",
  ],
  [TYPE_DROIT.TOURNEE_R, "Consulter les tournées"],
  [TYPE_DROIT.TOURNEE_RESERVATION_D, "Supprimer la réservation d'une tournée"],
  [TYPE_DROIT.TRAITEMENTS_E, "Exécuter des traitements  "],
  [
    TYPE_DROIT.TRAITEMENTS_PEI_E,
    "Exécuter des traitements en lien avec la thématique PEI",
  ],
  [TYPE_DROIT.VISITE_R, "Consulter les visites"],
  [
    TYPE_DROIT.VISITE_CONTROLE_TECHNIQUE_C,
    "Réaliser des contrôles techniques périodiques",
  ],
  [TYPE_DROIT.VISITE_CTP_D, "Supprimer des contrôles techniques périodiques"],
  [TYPE_DROIT.VISITE_NON_PROGRAMME_C, "Réaliser des visites non programmées"],
  [TYPE_DROIT.VISITE_NP_D, "Supprimer des visites non programmées"],
  [TYPE_DROIT.VISITE_RECEP_C, "Réaliser des visites de réception"],
  [TYPE_DROIT.VISITE_RECEP_D, "Supprimer des visites de réception "],
  [
    TYPE_DROIT.VISITE_RECO_C,
    "Réaliser des visites de reconnaissance opérationnelle périodique",
  ],
  [
    TYPE_DROIT.VISITE_RECO_D,
    "Supprimer des visites de reconnaissance opérationnelle périodique",
  ],
  [
    TYPE_DROIT.VISITE_RECO_INIT_C,
    "Réaliser des visites de reconnaissance opérationnelle initiale",
  ],
  [
    TYPE_DROIT.VISITE_RECO_INIT_D,
    "Supprimer des visites de reconnaissance opérationnelle initiale",
  ],
  [TYPE_DROIT.ZOOM_LIEU_R, "Zoomer sur un lieu"],
  [TYPE_DROIT.DOCUMENTS_A, "Administrer les documents"],
]);

export enum TYPE_DROIT_API {
  ADMINISTRER = "ADMINISTRER",
  RECEVOIR = "RECEVOIR",
  TRANSMETTRE = "TRANSMETTRE",
}

export const TypeDroitApiLabel = new Map<TYPE_DROIT_API, string>([
  [
    TYPE_DROIT_API.ADMINISTRER,
    "Permet d'administer les données indépendamment de la zone de compétence de l'organisme.",
  ],
  [
    TYPE_DROIT_API.RECEVOIR,
    "Donne à l'organisme concerné les droits d'accès aux informations en lecture seule sur sa zone de compétence",
  ],
  [
    TYPE_DROIT_API.TRANSMETTRE,
    "Permet à l'organisme concerné de modifier des informations sur sa zone de compétence",
  ],
]);
