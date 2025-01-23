CREATE TABLE remocra.thematique(
    thematique_id                 UUID            PRIMARY KEY,
    thematique_actif              BOOLEAN         NOT NULL,
    thematique_protected          BOOLEAN         NOT NULL,
    thematique_code               TEXT            UNIQUE NOT NULL,
    thematique_libelle            TEXT            NOT NULL
);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'POINT_EAU', 'Point d''eau', true);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'COUVERTURE_HYDRAULIQUE', 'Couverture hydraulique', true);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'CARTOGRAPHIE', 'Carthotèque', true);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'OLDEBS', 'Obligation légale de débroussaillement', true);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'PERMIS', 'Permis', true);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'RCI', 'Recherche des Causes Incendie', true);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'DFCI', 'DFCI', true);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'ADRESSES', 'Adresses', true);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'RISQUES', 'Risques', true);

INSERT INTO remocra.thematique
    (thematique_id, thematique_actif, thematique_code, thematique_libelle, thematique_protected)
VALUES (gen_random_uuid(), true, 'DIVERS', 'Divers', true);


CREATE TABLE remocra.bloc_document (
    bloc_document_id                    UUID    PRIMARY KEY,
    document_id                         UUID     NOT NULL REFERENCES remocra.document(document_id),
    bloc_document_libelle       TEXT,
    bloc_document_description   TEXT,
    bloc_document_date_maj      TIMESTAMPTZ
);

CREATE TABLE remocra.l_thematique_bloc_document (
    thematique_id           UUID     NOT NULL REFERENCES remocra.thematique(thematique_id),
    bloc_document_id        UUID     NOT NULL REFERENCES remocra.bloc_document(bloc_document_id),

    PRIMARY KEY (thematique_id, bloc_document_id)
);

CREATE TABLE remocra.l_profil_droit_bloc_document (
    profil_droit_id       UUID     NOT NULL REFERENCES remocra.profil_droit(profil_droit_id),
    bloc_document_id      UUID     NOT NULL REFERENCES remocra.bloc_document(bloc_document_id),

    PRIMARY KEY (profil_droit_id, bloc_document_id)
);

ALTER TYPE historique.type_objet ADD VALUE IF NOT EXISTS 'THEMATIQUE';
ALTER TYPE historique.type_objet ADD VALUE IF NOT EXISTS 'BLOC_DOCUMENT';
ALTER TYPE remocra."DROIT" ADD VALUE IF NOT EXISTS 'DOCUMENTS_A';