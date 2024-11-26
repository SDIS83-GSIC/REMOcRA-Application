CREATE TYPE remocra.TYPE_PARAMETRE_RAPPORT_PERSONNALISE as ENUM(
    'CHECKBOX_INPUT',
    'DATE_INPUT',
    'NUMBER_INPUT',
    'SELECT_INPUT',
    'TEXT_INPUT'
);

ALTER TYPE remocra.TYPE_MODULE ADD VALUE 'RAPPORT_PERSONNALISE';

CREATE TABLE remocra.rapport_personnalise (
    rapport_personnalise_id          UUID           NOT NULL PRIMARY KEY,
    rapport_personnalise_actif       BOOLEAN        NOT NULL,
    rapport_personnalise_code        TEXT UNIQUE    NOT NULL,
    rapport_personnalise_libelle     TEXT           NOT NULL,
    rapport_personnalise_protected   BOOLEAN        NOT NULL,
    rapport_personnalise_champ_geometrie  TEXT,
    rapport_personnalise_description TEXT,
    rapport_personnalise_source_sql  TEXT               NOT NULL,
    rapport_personnalise_module  remocra.TYPE_MODULE    NOT NULL
);

CREATE TABLE remocra.rapport_personnalise_parametre (
    rapport_personnalise_parametre_id                       UUID   NOT NULL PRIMARY KEY,
    rapport_personnalise_parametre_rapport_personnalise_id  UUID   NOT NULL REFERENCES remocra.rapport_personnalise(rapport_personnalise_id),
    rapport_personnalise_parametre_code                     TEXT   NOT NULL,
    rapport_personnalise_parametre_libelle                  TEXT   NOT NULL,
    rapport_personnalise_parametre_source_sql               TEXT,
    rapport_personnalise_parametre_description              TEXT,
    rapport_personnalise_parametre_source_sql_id            TEXT,
    rapport_personnalise_parametre_source_sql_libelle       TEXT,
    rapport_personnalise_parametre_valeur_defaut            TEXT,
    rapport_personnalise_parametre_is_required              BOOLEAN NOT NULL,
    rapport_personnalise_parametre_type                     remocra.TYPE_PARAMETRE_RAPPORT_PERSONNALISE NOT NULL,
    rapport_personnalise_parametre_ordre                    INTEGER NOT NULL
);

CREATE TABLE remocra.l_rapport_personnalise_profil_droit (
    profil_droit_id           UUID     NOT NULL REFERENCES remocra.profil_droit(profil_droit_id),
    rapport_personnalise_id   UUID     NOT NULL REFERENCES remocra.rapport_personnalise(rapport_personnalise_id),

    PRIMARY KEY (profil_droit_id, rapport_personnalise_id)
);


ALTER TYPE historique.type_objet ADD VALUE 'RAPPORT_PERSONNALISE';