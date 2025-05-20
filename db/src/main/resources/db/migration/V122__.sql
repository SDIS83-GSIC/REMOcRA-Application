CREATE TYPE CRISE_INDICATEUR_MODE AS ENUM('MOYENS', 'INTERVENTIONS');

CREATE TABLE crise_indicateur (
    crise_indicateur_id UUID PRIMARY KEY,
    crise_indicateur_cle TEXT,
    crise_indicateur_valeur TEXT,
    crise_indicateur_mode CRISE_INDICATEUR_MODE NOT NULL,
    crise_indicateur_statut EVENEMENT_STATUT_MODE NOT NULL,
    crise_id UUID REFERENCES crise(crise_id)
);
