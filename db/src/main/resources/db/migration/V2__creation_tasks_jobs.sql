CREATE TYPE remocra.etat_job AS ENUM ('EN_COURS', 'TERMINE', 'NOTIFIE', 'EN_ERREUR');

CREATE TYPE remocra.log_line_gravity AS ENUM ('INFO', 'WARN', 'ERROR');

CREATE TYPE remocra.type_task AS ENUM (
        'BASCULE_AUTO_INDISPO_TEMP',
        'DEPARTS_DE_FEU',
        'ETAT_DES_HYDRANTS_PENA_INDISPONIBLES',
        'ETAT_DES_HYDRANTS_PENA',
        'ETAT_DES_HYDRANTS_PIBI_INDISPONIBLES',
        'ETAT_DES_HYDRANTS_PIBI',
        'ETAT_DES_PEI_NON_RECEPTIONNES',
        'EXPORTER_CTP',
        'EXPORTER_DONNEES_FROM_MODELE',
        'EXPORTER_REMOCRA_VERS_SIG',
        'GENERER_EXPORT_REQUETES_PERSO',
        'IMPORTER_FICHIER_SHP_COUV_HYDRAU',
        'IMPORTER_SIG',
        'IMPRESSION_FICHE_OLDEB',
        'INSDISPO_SUR_LE_CARRE_DES_9',
        'LISTE_DES_PERMIS',
        'MAJ_LES_POSITIONS_PEI',
        'MAJ_ZONE_COMPETENCE_COMMUNE',
        'MISE_EN_INDISPO_PEI__COURRIER_INFO',
        'NB_ALERTES_PAR_UTILISATEUR_CASERNE',
        'NOTIFIER_BDECI_RECOP_DE_LA_JOURNEE',
        'NOTIFIER_CHANGEMENTS_ETAT',
        'NOTIFIER_CIS_ROI',
        'NOTIFIER_MAIRES_PEI_INDISPO',
        'NOTIFIER_MAIRES_ROI',
        'NOTIFIER_ROP',
        'NOTIFIER_UTILISATEURS',
        'PEI_A_NUMEROTER',
        'PURGER',
        'REFERENCER_MODELES',
        'ROP__COURRIER_DE_RAPPORT',
        'ROP__COURRIER_INFO_PREALABLE',
        'SUPPR_FICHIER_KML_RISQUES_TECHNO',
        'SYNCHRONISER_AVEC_PREVARISC',
        'TELECHARGEMENT_DES_FICHES_ATLAS',
        'SYNCHRO_UTILISATEUR'
);

CREATE TABLE remocra.task (
    task_id             UUID                        NOT NULL PRIMARY KEY,
    task_type           remocra.type_task   UNIQUE  NOT NULL,
    task_actif          BOOLEAN                     NOT NULL DEFAULT FALSE,
    task_planification  TEXT                            NULL,
    task_exec_manuelle  BOOLEAN                     NOT NULL DEFAULT TRUE,
    task_parametres     TEXT                            NULL,
    task_notification   TEXT                            NULL
);

CREATE TABLE remocra.job (
    job_id                  UUID                         NOT NULL PRIMARY KEY,
    job_task_id             UUID                         NOT NULL REFERENCES remocra.task(task_id),
    job_etat_job            remocra.etat_job             NOT NULL,
    job_date_debut          TIMESTAMP WITH TIME ZONE     NOT NULL,
    job_date_fin            TIMESTAMP WITH TIME ZONE         NULL,
    job_parametres          TEXT                             NULL
);

CREATE TABLE remocra.log_line
(
    log_line_id        UUID                     NOT NULL PRIMARY KEY,
    log_line_job_id    UUID                     NOT NULL REFERENCES remocra.job (job_id),
    log_line_gravity   remocra.log_line_gravity NOT NULL,
    log_line_date      TIMESTAMPTZ              NOT NULL,
    log_line_object_id UUID                         NULL,
    log_line_message   TEXT                     NOT NULL
);
