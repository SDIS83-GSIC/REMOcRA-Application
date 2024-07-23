DROP TABLE IF EXISTS remocra.pena_aspiration;
DROP TABLE IF EXISTS remocra.type_pena_aspiration;

CREATE TABLE remocra.type_pena_aspiration (
    type_pena_aspiration_id                  UUID                        PRIMARY KEY,
    type_pena_aspiration_type_actif          BOOLEAN                     NOT NULL,
    type_pena_aspiration_code                TEXT                        UNIQUE NOT NULL,
    type_pena_aspiration_libelle             TEXT                        NOT NULL
);


CREATE TABLE remocra.pena_aspiration (
    pena_aspiration_id                                UUID           PRIMARY KEY,
    pena_aspiration_pena_id                           UUID           REFERENCES remocra.pena(pena_id),
    pena_aspiration_geometrie                         Geometry,
    pena_aspiration_numero                            TEXT           UNIQUE NOT NULL,
    pena_aspiration_est_normalise                     BOOLEAN        NOT NULL,
    pena_aspiration_hauteur_superieure_3_metres       BOOLEAN        NOT NULL,
    pena_aspiration_type_pena_aspiration_id           UUID           REFERENCES remocra.type_pena_aspiration(type_pena_aspiration_id),
    pena_aspiration_est_deporte                       BOOLEAN        NOT NULL
);

COMMENT
    ON COLUMN remocra.pena_aspiration.pena_aspiration_numero
    IS 'Identifiant interne';

COMMENT
    ON COLUMN remocra.pena_aspiration.pena_aspiration_est_normalise
    IS 'Indique si le PEI est normalisé ou non';

COMMENT
    ON COLUMN remocra.pena_aspiration.pena_aspiration_hauteur_superieure_3_metres
    IS 'Indique si la hauteur de l''aspiration est supérieure à 3 mètres';

COMMENT
    ON COLUMN remocra.pena_aspiration.pena_aspiration_est_deporte
    IS 'Indique si le dispositif d''aspiration est à proximité ou déporté';

ALTER TABLE remocra.pena_aspiration
    ADD CONSTRAINT geometrie_point_pena_aspiration CHECK (geometrytype(pena_aspiration_geometrie) = 'POINT'::text);

ALTER TYPE historique.type_objet ADD VALUE 'PENA_ASPIRATION';