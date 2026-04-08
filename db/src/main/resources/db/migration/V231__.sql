ALTER TABLE remocra.couche
    ADD COLUMN couche_from_geoserver boolean;

UPDATE remocra.couche
    SET couche_from_geoserver = true;

ALTER TABLE remocra.couche
    ALTER COLUMN couche_from_geoserver SET NOT NULL;
