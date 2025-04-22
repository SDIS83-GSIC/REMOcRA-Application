-- on ajoute l'ID technique du client Keycloak dans la table organisme
-- le but est de garder une référence si l'email de l'organisme change
ALTER TABLE remocra.organisme ADD COLUMN organisme_keycloak_id text;
ALTER TABLE remocra.organisme ADD CONSTRAINT organisme_keycloak_id_unique UNIQUE (organisme_keycloak_id);
