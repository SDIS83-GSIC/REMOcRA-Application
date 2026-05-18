CREATE TYPE type_aire AS ENUM ('RETOURNEMENT','CROISEMENT');

CREATE TABLE dfci_aire(
    dfci_aire_id UUID PRIMARY KEY,
    dfci_aire_amenagement BOOL NOT NULL ,
    dfci_aire_date_gps TIMESTAMPTZ NOT NULL ,
    dfci_aire_grande_dimension DOUBLE PRECISION,
    dfci_aire_petite_dimension DOUBLE PRECISION,
    dfci_aire_type type_aire NOT NULL,
    dfci_aire_dfci_piste_id UUID REFERENCES dfci_piste(dfci_piste_id),
    dfci_aire_geometrie GEOMETRY NOT NULL,
    dfci_aire_remarque TEXT,
    dfci_aire_code TEXT UNIQUE NOT NULL,
    dfci_aire_version INTEGER NOT NULL
);

ALTER TYPE historique.type_objet ADD VALUE 'DFCI_AIRE';
