CREATE TYPE remocra.DIRECTION as ENUM (
    'N',
    'E',
    'S',
    'O',
    'NE',
    'SE',
    'SO',
    'NO'
    );

CREATE TABLE remocra.rcci_type_degre_certitude
(
    rcci_type_degre_certitude_id      UUID PRIMARY KEY,
    rcci_type_degre_certitude_actif   BOOLEAN NOT NULL,
    rcci_type_degre_certitude_code    TEXT    NOT NULL UNIQUE,
    rcci_type_degre_certitude_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.rcci_type_origine_alerte
(
    rcci_type_origine_alerte_id      UUID PRIMARY KEY,
    rcci_type_origine_alerte_actif   BOOLEAN NOT NULL,
    rcci_type_origine_alerte_code    TEXT    NOT NULL UNIQUE,
    rcci_type_origine_alerte_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.rcci_type_promethee_famille
(
    rcci_type_promethee_famille_id      UUID PRIMARY KEY,
    rcci_type_promethee_famille_actif   BOOLEAN NOT NULL,
    rcci_type_promethee_famille_code    TEXT    NOT NULL UNIQUE,
    rcci_type_promethee_famille_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.rcci_type_promethee_partition
(
    rcci_type_promethee_partition_id                             UUID PRIMARY KEY,
    rcci_type_promethee_partition_actif                          BOOLEAN NOT NULL,
    rcci_type_promethee_partition_code                           TEXT    NOT NULL UNIQUE,
    rcci_type_promethee_partition_libelle                        TEXT    NOT NULL,
    rcci_type_promethee_partition_rcci_type_promethee_famille_id UUID REFERENCES remocra.rcci_type_promethee_famille (rcci_type_promethee_famille_id)
);

CREATE TABLE remocra.rcci_type_promethee_categorie
(
    rcci_type_promethee_categorie_id                               UUID PRIMARY KEY,
    rcci_type_promethee_categorie_actif                            BOOLEAN NOT NULL,
    rcci_type_promethee_categorie_code                             TEXT    NOT NULL UNIQUE,
    rcci_type_promethee_categorie_libelle                          TEXT    NOT NULL,
    rcci_type_promethee_categorie_rcci_type_promethee_partition_id UUID REFERENCES remocra.rcci_type_promethee_partition (rcci_type_promethee_partition_id)
);

CREATE TABLE remocra.rcci
(
    rcci_id                               UUID PRIMARY KEY,
    rcci_commentaire_conclusion           TEXT,
    rcci_complement                       TEXT,
    rcci_carroyage_dfci                   TEXT,
    rcci_date_incendie                    TIMESTAMPTZ NOT NULL,
    rcci_date_modification                TIMESTAMPTZ NOT NULL,
    rcci_direction_vent                   remocra.DIRECTION,
    rcci_force_vent                       INTEGER,
    rcci_forces_ordre                     TEXT,
    rcci_gdh                              TIMESTAMPTZ,
    rcci_gel_lieux                        BOOLEAN,
    rcci_geometrie                        geometry    NOT NULL,
    rcci_hygrometrie                      INTEGER,
    rcci_indice_rothermel                 INTEGER,
    rcci_point_eclosion                   TEXT        NOT NULL,
    rcci_premier_cos                      TEXT,
    rcci_premier_engin                    TEXT,
    rcci_superficie_finale                DOUBLE PRECISION,
    rcci_superficie_referent              DOUBLE PRECISION,
    rcci_superficie_secours               DOUBLE PRECISION,
    rcci_temperature                      DOUBLE PRECISION,
    rcci_vent_local                       BOOLEAN,
    rcci_voie                             TEXT,
    rcci_commune_id                       UUID REFERENCES remocra.commune (commune_id),
    rcci_rcci_type_promethee_categorie_id UUID REFERENCES remocra.rcci_type_promethee_categorie (rcci_type_promethee_categorie_id),
    rcci_rcci_type_degre_certitude_id     UUID REFERENCES remocra.rcci_type_degre_certitude (rcci_type_degre_certitude_id),
    rcci_rcci_type_origine_alerte_id      UUID        NOT NULL REFERENCES remocra.rcci_type_origine_alerte (rcci_type_origine_alerte_id),
    rcci_rcci_arrivee_ddtm_onf_id         UUID REFERENCES remocra.utilisateur (utilisateur_id),
    rcci_rcci_arrivee_sdis_id             UUID REFERENCES remocra.utilisateur (utilisateur_id),
    rcci_rcci_arrivee_gendarmerie_id      UUID REFERENCES remocra.utilisateur (utilisateur_id),
    rcci_rcci_arrivee_police_id           UUID REFERENCES remocra.utilisateur (utilisateur_id),
    rcci_utilisateur_id                   UUID        NOT NULL REFERENCES remocra.utilisateur (utilisateur_id)
);

CREATE TABLE remocra.rcci_document
(
    rcci_document_id          UUID PRIMARY KEY,
    rcci_document_document_id UUID NOT NULL REFERENCES remocra.document (document_id),
    rcci_document_rcci_id     UUID NOT NULL REFERENCES remocra.rcci (rcci_id)
);
