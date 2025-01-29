-- Renommage bloc document --> document habilitable

DROP TABLE remocra.l_profil_droit_bloc_document;
DROP TABLE remocra.l_thematique_bloc_document;
DROP TABLE remocra.bloc_document;

CREATE TABLE remocra.document_habilitable (
    document_habilitable_id                    UUID    PRIMARY KEY,
    document_id                         UUID     NOT NULL REFERENCES remocra.document(document_id),
    document_habilitable_libelle       TEXT,
    document_habilitable_description   TEXT,
    document_habilitable_date_maj      TIMESTAMPTZ
);

CREATE TABLE remocra.l_thematique_document_habilitable (
    thematique_id           UUID     NOT NULL REFERENCES remocra.thematique(thematique_id),
    document_habilitable_id        UUID     NOT NULL REFERENCES remocra.document_habilitable(document_habilitable_id),

    PRIMARY KEY (thematique_id, document_habilitable_id)
);

CREATE TABLE remocra.l_profil_droit_document_habilitable (
    profil_droit_id       UUID     NOT NULL REFERENCES remocra.profil_droit(profil_droit_id),
    document_habilitable_id      UUID     NOT NULL REFERENCES remocra.document_habilitable(document_habilitable_id),

    PRIMARY KEY (profil_droit_id, document_habilitable_id)
);

ALTER TYPE historique.type_objet RENAME VALUE 'BLOC_DOCUMENT' TO 'DOCUMENT_HABILITABLE';
