DROP TABLE IF EXISTS remocra.fiche_resume_bloc;
DROP TYPE IF EXISTS remocra.type_resume_element;

CREATE TYPE remocra.type_resume_element as ENUM(
    'TOURNEE',
    'DISPONIBILITE',
    'ANOMALIES',
    'LOCALISATION',
    'CARACTERISTIQUES',
    'CIS',
    'CASERNE',
    'OBSERVATION'
);

CREATE TABLE remocra.fiche_resume_bloc(
    fiche_resume_bloc_id                 UUID                            PRIMARY KEY,
    fiche_resume_bloc_type_resume_data   remocra.type_resume_element     NOT NULL,
    fiche_resume_bloc_titre              text                            NOT NULL,
    fiche_resume_bloc_colonne            integer                         NOT NULL,
    fiche_resume_bloc_ligne              integer                         NOT NULL
);