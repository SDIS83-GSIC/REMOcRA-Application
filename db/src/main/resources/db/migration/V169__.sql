ALTER TABLE remocra.utilisateur
    ADD COLUMN utilisateur_keycloak_id TEXT UNIQUE;

UPDATE remocra.utilisateur SET utilisateur_keycloak_id = utilisateur_id::TEXT;

ALTER TABLE remocra.utilisateur
    ALTER COLUMN utilisateur_keycloak_id SET NOT NULL;
