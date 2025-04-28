ALTER TABLE remocra.anomalie_categorie ADD anomalie_categorie_protected bool NOT NULL DEFAULT false;

UPDATE remocra.anomalie_categorie
SET anomalie_categorie_ordre=-1, anomalie_categorie_protected=true
WHERE anomalie_categorie_code='SYSTEME';
