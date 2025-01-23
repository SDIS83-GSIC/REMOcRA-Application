DROP TABLE IF EXISTS remocra.visite_ctrl_debit_pression;
DROP TABLE IF EXISTS remocra.visite;
DROP TYPE IF EXISTS remocra."TYPE_VISITE";



CREATE TYPE remocra."TYPE_VISITE" AS ENUM (
    'RECEPTION',
    'RECO_INIT',
    'CTP',
    'RECOP',
    'NP'
);



CREATE TABLE remocra.visite (
    visite_id                   UUID PRIMARY KEY,
    visite_pei_id               UUID NOT NULL REFERENCES remocra.pei (pei_id),
    visite_date                 TIMESTAMPTZ NOT NULL,
    visite_type_visite          "TYPE_VISITE" NOT NULL,
    visite_agent1               TEXT,
    visite_agent2               TEXT,
    visite_observation          TEXT
);

COMMENT
    ON COLUMN remocra.visite.visite_agent1
    IS 'Personne ou organisation ayant réalisé la visite (principal)';
COMMENT
    ON COLUMN remocra.visite.visite_agent2
    IS 'Personne ou organisation ayant réalisé la visite (secondaire)';



CREATE TABLE remocra.visite_ctrl_debit_pression (
    visite_ctrl_debit_pression_visite_id        UUID PRIMARY KEY REFERENCES remocra.visite (visite_id),
    visite_ctrl_debit_pression_debit            INTEGER,
    visite_ctrl_debit_pression_pression         DECIMAL(5, 2),
    visite_ctrl_debit_pression_pression_dyn     DECIMAL(5, 2)
);

COMMENT
    ON COLUMN remocra.visite_ctrl_debit_pression.visite_ctrl_debit_pression_visite_id
    IS 'UUID de la visite pour laquelle ce contrôle a été réalisé';
