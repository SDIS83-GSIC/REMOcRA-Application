-- Suppression de la contrainte NOT NULL sur le champ parcelle des OLDEBs
ALTER TABLE oldeb
ALTER COLUMN oldeb_cadastre_parcelle_id DROP NOT NULL;