-- Retire la contrainte not null pour le point d'éclosion qui peut etre null
ALTER TABLE remocra.rcci
ALTER COLUMN rcci_point_eclosion DROP NOT NULL;
