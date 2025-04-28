ALTER TABLE anomalie_categorie ADD COLUMN anomalie_categorie_ordre int;
ALTER TABLE anomalie_categorie ALTER COLUMN anomalie_categorie_ordre SET DEFAULT 0;
