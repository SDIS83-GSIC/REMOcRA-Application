ALTER TYPE historique.type_objet ADD VALUE 'PERMIS';
ALTER TYPE historique.type_objet ADD VALUE 'DOCUMENT_PERMIS';

-- Table remocra.type_permis_interservice
CREATE TABLE remocra.type_permis_interservice (
    type_permis_interservice_id     UUID    PRIMARY KEY,
    type_permis_interservice_code   TEXT    NOT NULL UNIQUE,
    type_permis_interservice_libelle    TEXT    NOT NULL UNIQUE,
    type_permis_interservice_pprif  BOOLEAN     NOT NULL,
    type_permis_interservice_actif  BOOLEAN     NOT NULL
);

INSERT INTO remocra.type_permis_interservice
(type_permis_interservice_id, type_permis_interservice_code, type_permis_interservice_libelle, type_permis_interservice_pprif, type_permis_interservice_actif)
VALUES
    (gen_random_uuid(), 'VALIDE', 'Valide', true, true),
    (gen_random_uuid(), 'NON_VALIDE', 'Non valide', true, true),
    (gen_random_uuid(), 'NON_CONCERNE', 'Non concerné', false, true);


-- Table remocra.type_permis_avis
CREATE TABLE remocra.type_permis_avis (
    type_permis_avis_id     UUID    PRIMARY KEY,
    type_permis_avis_code   TEXT    NOT NULL UNIQUE,
    type_permis_avis_libelle    TEXT    NOT NULL UNIQUE,
    type_permis_avis_pprif  BOOLEAN     NOT NULL,
    type_permis_avis_actif  BOOLEAN     NOT NULL,
    type_permis_avis_protected  BOOLEAN     NOT NULL
);

INSERT INTO remocra.type_permis_avis
(type_permis_avis_id,type_permis_avis_code,type_permis_avis_libelle,type_permis_avis_pprif,type_permis_avis_actif,type_permis_avis_protected)
values
    (gen_random_uuid(), 'ATTENTE', 'En attente', false, true, true);


-- Table remocra.permis
CREATE TABLE remocra.permis (
    permis_id                           UUID        PRIMARY KEY,
    permis_libelle                      TEXT        NOT NULL,
    permis_numero                       TEXT        NOT NULL,
    permis_instructeur_id               UUID        NOT NULL REFERENCES remocra.utilisateur(utilisateur_id),
    permis_service_instructeur_id       UUID        NOT NULL REFERENCES remocra.type_organisme(type_organisme_id),
    permis_type_permis_interservice_id  UUID        NOT NULL REFERENCES remocra.type_permis_interservice(type_permis_interservice_id),
    permis_type_permis_avis_id          UUID        NOT NULL REFERENCES remocra.type_permis_avis(type_permis_avis_id),
    permis_ri_receptionnee              BOOLEAN     NOT NULL,
    permis_dossier_ri_valide            BOOLEAN     NOT NULL,
    permis_observations                 TEXT,
    permis_voie_text                    TEXT,
    permis_voie_id                      UUID        REFERENCES remocra.voie(voie_id),
    permis_complement                   TEXT,
    permis_commune_id                   UUID        NOT NULL REFERENCES remocra.commune(commune_id),
    permis_annee                        INTEGER     NOT NULL,
    permis_date_permis                  TIMESTAMPTZ NOT NULL,
    permis_geometrie                    Geometry    NOT NULL
);

COMMENT
    ON COLUMN remocra.permis.permis_instructeur_id
    IS 'Utilisateur dépositaire de la demande de permis';


-- Table l_permis_cadastre_parcelle
CREATE TABLE remocra.l_permis_cadastre_parcelle (
    permis_id               UUID    REFERENCES remocra.permis(permis_id),
    cadastre_parcelle_id    UUID    REFERENCES remocra.cadastre_parcelle(cadastre_parcelle_id),

    PRIMARY KEY (permis_id, cadastre_parcelle_id)
);


-- Table remocra.l_permis_document
CREATE TABLE remocra.l_permis_document (
    permis_id       UUID    REFERENCES remocra.permis(permis_id),
    document_id     UUID    REFERENCES remocra.document(document_id),

    PRIMARY KEY (permis_id, document_id)
);
