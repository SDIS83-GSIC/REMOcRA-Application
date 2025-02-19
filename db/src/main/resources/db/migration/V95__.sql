DROP TABLE remocra.l_modele_courrier_profil_droit;
DROP TABLE remocra.modele_courrier_parametre;
DROP TABLE remocra.modele_courrier;
DROP TYPE remocra.TYPE_PARAMETRE_COURRIER;

ALTER TYPE TYPE_PARAMETRE_RAPPORT_PERSONNALISE RENAME TO TYPE_PARAMETRE_RAPPORT_COURRIER;

CREATE TABLE remocra.modele_courrier (
    modele_courrier_id          UUID           NOT NULL PRIMARY KEY,
    modele_courrier_actif       BOOLEAN        NOT NULL,
    modele_courrier_code        TEXT UNIQUE    NOT NULL,
    modele_courrier_libelle     TEXT           NOT NULL,
    modele_courrier_protected   BOOLEAN        NOT NULL,
    modele_courrier_description TEXT,
    modele_courrier_source_sql  TEXT               NOT NULL,
    modele_courrier_module  remocra.TYPE_MODULE    NOT NULL,
    modele_courrier_corps_email TEXT NOT NULL,
    modele_courrier_objet_email TEXT NOT NULL
);

CREATE TABLE remocra.modele_courrier_parametre (
    modele_courrier_parametre_id                       UUID   NOT NULL PRIMARY KEY,
    modele_courrier_parametre_modele_courrier_id  UUID   NOT NULL REFERENCES remocra.modele_courrier(modele_courrier_id),
    modele_courrier_parametre_code                     TEXT   NOT NULL,
    modele_courrier_parametre_libelle                  TEXT   NOT NULL,
    modele_courrier_parametre_source_sql               TEXT,
    modele_courrier_parametre_description              TEXT,
    modele_courrier_parametre_source_sql_id            TEXT,
    modele_courrier_parametre_source_sql_libelle       TEXT,
    modele_courrier_parametre_valeur_defaut            TEXT,
    modele_courrier_parametre_is_required              BOOLEAN NOT NULL,
    modele_courrier_parametre_type                     remocra.TYPE_PARAMETRE_RAPPORT_COURRIER NOT NULL,
    modele_courrier_parametre_ordre                    INTEGER NOT NULL
);

CREATE TABLE remocra.l_modele_courrier_profil_droit (
    profil_droit_id      UUID     NOT NULL REFERENCES remocra.profil_droit(profil_droit_id),
    modele_courrier_id   UUID     NOT NULL REFERENCES remocra.modele_courrier(modele_courrier_id),

    PRIMARY KEY (profil_droit_id, modele_courrier_id)
);

CREATE TABLE remocra.l_modele_courrier_document (
    modele_courrier_id   UUID     NOT NULL REFERENCES remocra.modele_courrier(modele_courrier_id),
    document_id      UUID     NOT NULL REFERENCES remocra.document(document_id),
    is_main_report         BOOLEAN,

    PRIMARY KEY (modele_courrier_id, document_id)
);

COMMENT ON COLUMN remocra.l_modele_courrier_document.is_main_report IS 'Précise s''il s''agit du rapport principal.';

ALTER TYPE historique.type_objet ADD VALUE 'MODELE_COURRIER';
ALTER TYPE historique.type_objet ADD VALUE 'DOCUMENT_MODELE_COURRIER';
