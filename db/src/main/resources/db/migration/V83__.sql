CREATE TYPE remocra."ETAT_ADRESSE" AS ENUM (
    'EN_COURS',
    'ACCEPTEE',
    'REFUSEE'
    );

CREATE TYPE remocra."TYPE_GEOMETRY" AS ENUM (
    'POINT',
    'LINESTRING',
    'POLYGON'
    );

CREATE TABLE remocra.adresse
(
    adresse_id                UUID PRIMARY KEY,
    adresse_description       TEXT,
    adresse_utilisateur       UUID REFERENCES remocra.utilisateur NOT NULL,
    adresse_date_constat      TIMESTAMPTZ,
    adresse_date_modification TIMESTAMPTZ,
    adresse_type              "ETAT_ADRESSE"                      NOT NULL,
    adresse_geometrie         geometry                            NOT NULL
);

ALTER TABLE remocra.adresse
    ADD CONSTRAINT geometrie_adresse CHECK (geometrytype(adresse_geometrie) = 'POINT'::text);

COMMENT ON COLUMN remocra.adresse.adresse_geometrie
    IS 'Centroïde de toutes les géométries des éléments de l''adresse';

CREATE TABLE remocra.l_adresse_document
(
    document_id UUID REFERENCES remocra.document,
    adresse_id  UUID REFERENCES remocra.adresse,
    PRIMARY KEY (document_id, adresse_id)
);

CREATE TABLE remocra.adresse_type_element
(
    adresse_type_element_id      UUID PRIMARY KEY,
    adresse_type_element_actif   BOOLEAN     NOT NULL,
    adresse_type_element_code    TEXT UNIQUE NOT NULL,
    adresse_type_element_libelle TEXT
);

CREATE TABLE remocra.adresse_sous_type_element
(
    adresse_sous_type_element_id             UUID PRIMARY KEY,
    adresse_sous_type_element_actif          BOOLEAN                 NOT NULL,
    adresse_sous_type_element_code           TEXT UNIQUE             NOT NULL,
    adresse_sous_type_element_libelle        TEXT,
    adresse_sous_type_element_type_geometrie remocra."TYPE_GEOMETRY" NOT NULL,
    adresse_sous_type_element_type_element   UUID REFERENCES remocra.adresse_type_element
);
CREATE TABLE remocra.adresse_type_anomalie
(
    adresse_type_anomalie_id      UUID PRIMARY KEY,
    adresse_type_anomalie_actif   BOOLEAN     NOT NULL,
    adresse_type_anomalie_code    TEXT UNIQUE NOT NULL,
    adresse_type_anomalie_libelle TEXT
);

CREATE TABLE remocra.adresse_element
(
    adresse_element_id          UUID PRIMARY KEY,
    adresse_element_description TEXT,
    adresse_element_geometrie   geometry NOT NULL,
    adresse_element_sous_type   UUID     NOT NULL REFERENCES remocra.adresse_sous_type_element,
    adresse_element_adresse_id  UUID     NOT NULL REFERENCES remocra.adresse

);

CREATE TABLE remocra.l_adresse_element_adresse_type_anomalie
(
    adresse_type_anomalie_id UUID REFERENCES adresse_type_anomalie ,
    element_id               UUID REFERENCES adresse_element,
    PRIMARY KEY (adresse_type_anomalie_id, element_id)
);
