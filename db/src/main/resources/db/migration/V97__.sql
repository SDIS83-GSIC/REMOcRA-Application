-- Table type_crise
CREATE TABLE remocra.type_crise (
    type_crise_id UUID PRIMARY KEY,
    type_crise_code TEXT NOT NULL UNIQUE,
    type_crise_libelle TEXT NOT NULL
);

CREATE TYPE TYPE_CRISE_STATUT AS ENUM ('EN_COURS', 'TERMINEE', 'FUSIONNEE');

-- Table : crise
CREATE TABLE remocra.crise (
    crise_id UUID PRIMARY KEY,
    crise_libelle TEXT NOT NULL,
    crise_description TEXT,
    crise_date_debut TIMESTAMPTZ NOT NULL,
    crise_date_fin TIMESTAMPTZ,
    crise_type_crise_id UUID NOT NULL REFERENCES remocra.type_crise(type_crise_id),
    crise_statut_type TYPE_CRISE_STATUT NOT NULL DEFAULT 'EN_COURS'
);

-- Table crise_commune
CREATE TABLE remocra.l_crise_commune (
    crise_id UUID NOT NULL REFERENCES crise(crise_id),
    commune_id UUID NOT NULL REFERENCES commune(commune_id)
);





CREATE TABLE remocra.type_toponymie (
	type_toponymie_id UUID PRIMARY KEY NOT NULL,
	type_toponymie_actif BOOL NOT NULL,
	type_toponymie_protected BOOL NOT NULL,
	type_toponymie_code TEXT NOT NULL UNIQUE,
	type_toponymie_libelle TEXT NOT NULL
);

CREATE TABLE l_toponymie_crise (
	type_toponymie_id UUID NOT NULL REFERENCES remocra.type_toponymie(type_toponymie_id),
	crise_id UUID NOT NULL REFERENCES crise(crise_id)

);

CREATE TABLE remocra.toponymie (
	toponymie_id UUID PRIMARY KEY NOT NULL,
	toponymie_libelle TEXT,
	toponymie_code TEXT NOT NULL UNIQUE,
	toponymie_geometrie public.geometry NOT NULL,
	type_toponymie_id UUID NOT NULL REFERENCES remocra.type_toponymie(type_toponymie_id)
);

INSERT INTO remocra.type_toponymie (type_toponymie_id,type_toponymie_actif,type_toponymie_protected,type_toponymie_code,type_toponymie_libelle) VALUES
	(gen_random_uuid(),true,true,'LIEU_DIT','Lieux-dits'),
	(gen_random_uuid(),true,true,'COMMUNE','Communes'),
	(gen_random_uuid(),true,true,'PEI','Points d''eau'),
	(gen_random_uuid(),true,true,'ROUTES','Routes'),
	(gen_random_uuid(),true,true,'CADASTRE','Cadastre')