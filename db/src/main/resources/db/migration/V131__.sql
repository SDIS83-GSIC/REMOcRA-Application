ALTER TABLE remocra.crise_categorie
ADD COLUMN crise_categorie_actif BOOLEAN DEFAULT true;

ALTER TABLE remocra.type_crise
ADD COLUMN type_crise_actif BOOLEAN DEFAULT true;

ALTER TYPE historique.type_objet ADD VALUE 'CRISE_CATEGORIE';
ALTER TYPE historique.type_objet ADD VALUE 'TYPE_CRISE';
