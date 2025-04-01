ALTER TABLE remocra.commune ADD commune_code TEXT NULL;

COMMENT ON COLUMN remocra.commune.commune_code IS 'Permet une dénomination spécifique, en particulier pour la numérotation des PEI.';
