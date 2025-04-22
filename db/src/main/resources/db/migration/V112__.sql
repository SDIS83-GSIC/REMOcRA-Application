-- Retire la contrainte non nulle sur colonne couverturehydraulique.etude.etude_organisme_id
-- Pour permettre la création d'une étude pour un super-admin, qui n'a pas d'organisme
ALTER TABLE couverturehydraulique.etude ALTER COLUMN etude_organisme_id DROP NOT NULL;
