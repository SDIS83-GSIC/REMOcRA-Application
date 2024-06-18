DROP TABLE IF EXISTS remocra.organisme;
DROP TABLE IF EXISTS remocra.api;
DROP TABLE IF EXISTS remocra.zone_integration;
DROP TABLE IF EXISTS remocra.profil_organisme;


CREATE TABLE remocra.zone_integration
(
    zone_integration_id        UUID PRIMARY KEY,
    zone_integration_actif     BOOLEAN                 NOT NULL,
    zone_integration_code      TEXT UNIQUE             NOT NULL,
    zone_integration_libelle   TEXT,
    zone_integration_geometrie geometry                NOT NULL,
    zone_integration_type      "TYPE_ZONE_INTEGRATION" NOT NULL


);

ALTER TABLE remocra.zone_integration
    ADD CONSTRAINT polygon_multiPolygon_zone_integration CHECK (
        geometrytype(zone_integration_geometrie) = 'POLYGON'::text
            OR geometryType(zone_integration_geometrie) = 'MULTIPOLYGON'
        );

CREATE TABLE remocra.profil_organisme
(
    profil_organisme_id                UUID PRIMARY KEY,
    profil_organisme_actif             BOOLEAN NOT NULL,
    profil_organisme_code              TEXT    NOT NULL UNIQUE,
    profil_organisme_libelle           TEXT    NOT NULL,
    profil_organisme_type_organisme_id UUID REFERENCES remocra.type_organisme (type_organisme_id)
);
INSERT INTO remocra.profil_organisme
(profil_organisme_id, profil_organisme_actif, profil_organisme_code, profil_organisme_libelle,
 profil_organisme_type_organisme_id)
VALUES (gen_random_uuid(), true, 'REMOCRA', 'Remocra-projet',
        (SELECT type_organisme_id FROM remocra.type_organisme WHERE type_organisme_code = 'REMOCRA'))
;
CREATE TABLE remocra.organisme
(
    organisme_id                  UUID PRIMARY KEY,
    organisme_actif               BOOLEAN NOT NULL,
    organisme_code                TEXT    NOT NULL UNIQUE,
    organisme_libelle             TEXT    NOT NULL,
    organisme_email_contact       TEXT,
    organisme_profil_organisme_id UUID    NOT NULL REFERENCES remocra.profil_organisme (profil_organisme_id),
    organisme_type_organisme_id   UUID    NOT NULL REFERENCES remocra.type_organisme (type_organisme_id),
    organisme_zone_integration_id UUID    NOT NULL REFERENCES remocra.zone_integration (zone_integration_id),
    organisme_parent_id           UUID REFERENCES remocra.organisme (organisme_id)

);

COMMENT
    ON COLUMN remocra.organisme.organisme_email_contact
    IS '"adresse mail, si possible générique", permettant de contacter l''organisme';

CREATE TABLE remocra.api
(
    api_organisme_id       UUID PRIMARY KEY REFERENCES remocra.organisme (organisme_id),
    api_password           TEXT NOT NULL,
    api_derniere_connexion TIMESTAMP
)
