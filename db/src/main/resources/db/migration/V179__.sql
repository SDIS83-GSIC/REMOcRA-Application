ALTER TABLE couche_style RENAME TO couche_metadata;
ALTER TABLE remocra.couche_metadata ADD couche_metadata_public BOOl NOT NULL DEFAULT false;

ALTER TABLE remocra.couche_metadata RENAME COLUMN couche_style_id TO couche_metadata_id;
ALTER TABLE remocra.couche_metadata RENAME COLUMN couche_style_actif TO couche_metadata_actif;
ALTER TABLE remocra.couche_metadata RENAME COLUMN couche_style_style TO couche_metadata_style;
ALTER TABLE remocra.couche_metadata RENAME COLUMN couche_style_couche_id TO couche_metadata_couche_id;

ALTER TABLE remocra.l_groupe_fonctionnalites_couche_style RENAME TO l_groupe_fonctionnalites_couche_metadata;
ALTER TABLE remocra.l_groupe_fonctionnalites_couche_metadata RENAME COLUMN couche_style_id TO couche_metadata_id;
