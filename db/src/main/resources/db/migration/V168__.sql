-- Ajoute d'une colonne "derniere_connexion" dans la table utilisateur
ALTER TABLE remocra.utilisateur ADD COLUMN utilisateur_derniere_connexion TIMESTAMP WITH TIME ZONE null;
