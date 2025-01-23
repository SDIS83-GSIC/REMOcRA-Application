DROP TABLE IF EXISTS remocra.modele_courrier_droit;
DROP TABLE IF EXISTS remocra.modele_courrier_parametre;
DROP TABLE IF EXISTS remocra.modele_courrier;
DROP TYPE IF EXISTS remocra.TYPE_PARAMETRE_COURRIER;

CREATE TABLE remocra.modele_courrier (
    modele_courrier_id                      UUID                        PRIMARY KEY,
    modele_courrier_actif                   BOOLEAN                     NOT NULL,
    modele_courrier_code                    TEXT                        UNIQUE NOT NULL,
    modele_courrier_libelle                 TEXT                        NOT NULL,
    modele_courrier_description             TEXT,
    modele_courrier_chemin                  TEXT                        NOT NULL,
    modele_courrier_subreports              JSONB
    -- TODO comment gérer les templates de mail pour les courriers ?
);

COMMENT
    ON COLUMN remocra.modele_courrier.modele_courrier_subreports
    IS 'Un JSON contenant tous les sous rapports. Chaque sous rapport devra avoir une propriété "nom" et une propriété "chemin". Le "chemin" sera relatif à "/var/lib/remocra/modeles/courriers"  Exemple: [{"nom":"subReport1", "chemin":"le/chemin/fichier.jrxml"}, {"nom":"subReport2", "chemin":"le/chemin2/fichier2.jrxml"}]';

-- TODO table de droits


CREATE TYPE remocra.TYPE_PARAMETRE_COURRIER as ENUM(
    'COMMUNE_ID',
    'GESTIONNAIRE_ID',
    'IS_ONLY_PUBLIC',
    'IS_EPCI',
    'PROFIL_UTILISATEUR_ID',
    'ANNEE',
    'EXPEDITEUR_GRADE',
    'EXPEDITEUR_STATUT',
    'REFERENCE',
    'CIS_ID'
);

CREATE TABLE remocra.modele_courrier_parametre (
    modele_courrier_parametre_modele_courrier_id        UUID   NOT NULL REFERENCES modele_courrier(modele_courrier_id),
    modele_courrier_parametre_type_parametre_courrier   remocra.TYPE_PARAMETRE_COURRIER     NOT NULL,
    modele_courrier_parametre_libelle                   TEXT                                NOT NULL,
    modele_courrier_parametre_description               TEXT,
    modele_courrier_parametre_ordre                     NUMERIC,

    PRIMARY KEY (modele_courrier_parametre_modele_courrier_id, modele_courrier_parametre_type_parametre_courrier)
);
