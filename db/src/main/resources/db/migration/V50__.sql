ALTER TABLE remocra.utilisateur ADD COLUMN utilisateur_is_super_admin boolean;

UPDATE remocra.utilisateur set utilisateur_is_super_admin = true where utilisateur_username= 'remocra';