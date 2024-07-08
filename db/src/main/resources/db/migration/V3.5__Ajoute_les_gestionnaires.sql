DROP TABLE IF EXISTS remocra.gestionnaire;
DROP TABLE IF EXISTS remocra.site;


CREATE TABLE remocra.gestionnaire
(
    gestionnaire_id      UUID PRIMARY KEY,
    gestionnaire_actif   BOOLEAN NOT NULL,
    gestionnaire_code    TEXT    NOT NULL UNIQUE,
    gestionnaire_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.site
(
    site_id           UUID PRIMARY KEY,
    site_actif        BOOLEAN NOT NULL,
    site_code         TEXT    NOT NULL UNIQUE,
    site_libelle      TEXT    NOT NULL,
    site_geometrie    geometry        NOT NULL,
    site_gestionnaire_id UUID REFERENCES remocra.gestionnaire
);

ALTER TABLE remocra.site
    ADD CONSTRAINT polygon_multiPolygon_site CHECK (
        geometrytype(site_geometrie) = 'POLYGON'
            OR geometryType(site_geometrie) = 'MULTIPOLYGON'
        );

