CREATE TYPE remocra."TYPE_PARAMETRE" AS ENUM (
    'INTEGER',
    'STRING',
    'GEOMETRY',
    'BINARY',
    'DOUBLE',
    'BOOLEAN'
    );

CREATE TABLE remocra.parametre
(
    parametre_id                UUID PRIMARY KEY,
    parametre_code              TEXT NOT NULL UNIQUE,
    parametre_valeur            TEXT NULL,
    parametre_type              "TYPE_PARAMETRE" NOT NULL
);

