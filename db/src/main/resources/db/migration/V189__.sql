ALTER TABLE remocra.couche ADD COLUMN couche_tuilage BOOLEAN;
UPDATE remocra.couche SET couche_tuilage = FALSE;
ALTER TABLE remocra.couche ALTER COLUMN couche_tuilage SET NOT NULL;

COMMENT ON COLUMN remocra.couche.couche_tuilage IS 'Indique si la couche utilise le tuilage pour l''affichage des couches cartographiques.';
