CREATE TYPE remocra.OUI_NON_NA as ENUM(
    'OUI',
    'NON',
    'NA'
);

CREATE TYPE remocra.RISQUE_METEO as ENUM(
    'NA',
    'FAIBLE',
    'LEGER',
    'MODERE',
    'SEVERE',
    'TRES_SEVERE',
    'EXTREME'
);

CREATE TABLE remocra.rcci_indice_rothermel (
    rcci_indice_rothermel_id UUID PRIMARY KEY,
    rcci_indice_rothermel_actif BOOL,
    rcci_indice_rothermel_code TEXT UNIQUE NOT NULL,
    rcci_indice_rothermel_libelle TEXT NOT NULL
);

INSERT INTO remocra.rcci_indice_rothermel
(rcci_indice_rothermel_id, rcci_indice_rothermel_actif, rcci_indice_rothermel_code, rcci_indice_rothermel_libelle)
VALUES
(gen_random_uuid(), true, 'NA', 'Non renseigné'),
(gen_random_uuid(), true, '0', '0'),
(gen_random_uuid(), true, '10', '10'),
(gen_random_uuid(), true, '20', '20'),
(gen_random_uuid(), true, '30', '30'),
(gen_random_uuid(), true, '40', '40'),
(gen_random_uuid(), true, '50', '50'),
(gen_random_uuid(), true, '60', '60'),
(gen_random_uuid(), true, '70', '70'),
(gen_random_uuid(), true, '80', '80'),
(gen_random_uuid(), true, '90', '90'),
(gen_random_uuid(), true, '100', '100');

ALTER TABLE remocra.rcci
ADD COLUMN rcci_risque_meteo RISQUE_METEO;

ALTER TABLE remocra.rcci
DROP COLUMN rcci_indice_rothermel;

ALTER TABLE remocra.rcci
ADD COLUMN rcci_rcci_indice_rothermel_id UUID REFERENCES rcci_indice_rothermel(rcci_indice_rothermel_id);


ALTER TABLE remocra.rcci
DROP COLUMN rcci_vent_local;

ALTER TABLE remocra.rcci
DROP COLUMN rcci_gel_lieux;

ALTER TABLE remocra.rcci
ADD COLUMN rcci_vent_local OUI_NON_NA;

ALTER TABLE remocra.rcci
ADD COLUMN rcci_gel_lieux OUI_NON_NA;

ALTER TABLE remocra.rcci
RENAME COLUMN rcci_voie TO rcci_voie_texte;

ALTER TABLE remocra.rcci
ADD COLUMN rcci_voie_id UUID REFERENCES voie(voie_id);
