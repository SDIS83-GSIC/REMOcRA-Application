ALTER TABLE remocra.couche
ALTER COLUMN couche_nom DROP NOT NULL;

ALTER TABLE remocra.couche
ALTER COLUMN couche_format DROP NOT NULL;

ALTER TABLE remocra.couche
ALTER COLUMN couche_projection DROP NOT NULL;

ALTER TABLE remocra.couche
ADD COLUMN couche_cross_origin text;

CREATE TYPE source_carto AS ENUM ('GEOJSON', 'OSM', 'WMS', 'WMTS', 'WFS');

ALTER TABLE couche
ALTER COLUMN couche_source TYPE source_carto
USING couche_source::source_carto;
