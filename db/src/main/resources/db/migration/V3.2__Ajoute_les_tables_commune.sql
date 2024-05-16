DROP TABLE IF EXISTS remocra.commune;
DROP TABLE IF EXISTS remocra.voie;
DROP TABLE IF EXISTS remocra.lieu_dit;


CREATE TABLE remocra.commune
(
    commune_id          UUID PRIMARY KEY,
    commune_libelle     TEXT     NOT NULL,
    commune_insee       TEXT     NOT NULL UNIQUE,
    commune_code_postal TEXT     NOT NULL UNIQUE,
    commune_geometrie   geometry NOT NULL,
    commune_pprif       BOOLEAN  NOT NULL
);
CREATE TABLE remocra.voie
(
    voie_id         UUID PRIMARY KEY,
    voie_libelle    TEXT     NOT NULL,
    voie_geometrie  geometry NOT NULL,
    voie_commune_id UUID     NOT NULL REFERENCES remocra.commune (commune_id)
);
ALTER TABLE remocra.voie
    ADD CONSTRAINT line_or_multiline_voie CHECK (
        GeometryType(voie_geometrie) = 'LINESTRING' OR GeometryType(voie_geometrie) = 'MULTILINESTRING'
        );

CREATE TABLE remocra.lieu_dit
(
    lieu_dit_id         UUID PRIMARY KEY,
    lieu_dit_libelle    TEXT     NOT NULL,
    lieu_dit_geometrie  geometry NOT NULL,
    lieu_dit_commune_id UUID     NOT NULL REFERENCES remocra.commune (commune_id)

);