CREATE TABLE remocra.cadastre_section
(
    cadastre_section_id         UUID PRIMARY KEY,
    cadastre_section_geometrie  geometry NOT NULL,
    cadastre_section_numero     TEXT     NOT NULL,
    cadastre_section_commune_id UUID     NOT NULL REFERENCES remocra.commune (commune_id)
);

CREATE TABLE remocra.cadastre_parcelle
(
    cadastre_parcelle_id                  UUID PRIMARY KEY,
    cadastre_parcelle_geometrie           geometry NOT NULL,
    cadastre_parcelle_numero              TEXT     NOT NULL,
    cadastre_parcelle_cadastre_section_id UUID     NOT NULL REFERENCES remocra.cadastre_section (cadastre_section_id)
);
