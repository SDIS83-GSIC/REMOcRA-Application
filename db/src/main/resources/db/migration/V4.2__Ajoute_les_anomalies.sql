DROP TABLE IF EXISTS remocra.l_pei_anomalie;
DROP TABLE IF EXISTS remocra.l_visite_anomalie;
DROP TABLE IF EXISTS remocra.poids_anomalie;
DROP TABLE IF EXISTS remocra.anomalie;
DROP TABLE IF EXISTS remocra.anomalie_categorie;



CREATE TABLE remocra.anomalie_categorie (
    anomalie_categorie_id           UUID PRIMARY KEY,
    anomalie_categorie_code         TEXT UNIQUE NOT NULL,
    anomalie_categorie_libelle      TEXT NOT NULL,
    anomalie_categorie_actif        BOOLEAN NOT NULL
);

INSERT INTO remocra.anomalie_categorie(anomalie_categorie_id, anomalie_categorie_code, anomalie_categorie_libelle,
    anomalie_categorie_actif)
VALUES (gen_random_uuid(), 'SYSTEME', 'Système', TRUE);



CREATE TABLE remocra.anomalie (
    anomalie_id                     UUID PRIMARY KEY,
    anomalie_code                   TEXT UNIQUE NOT NULL,
    anomalie_libelle                TEXT NOT NULL,
    anomalie_commentaire            TEXT,
    anomalie_anomalie_categorie_id  UUID REFERENCES remocra.anomalie_categorie,
    anomalie_actif                  BOOLEAN NOT NULL,
    anomalie_protected              BOOLEAN NOT NULL,
    anomalie_rend_non_conforme      BOOLEAN NOT NULL
);

COMMENT
    ON COLUMN remocra.anomalie.anomalie_code
    IS 'Code unique servant à identifier certains types utilisés dans l''application';
COMMENT
    ON COLUMN remocra.anomalie.anomalie_commentaire
    IS 'Permet de définir un peu de context à l''emploi de cette anomalie';
COMMENT
    ON COLUMN remocra.anomalie.anomalie_protected
    IS 'Indique si l''anomalie est protégée ou non';
COMMENT
    ON COLUMN remocra.anomalie.anomalie_rend_non_conforme
    IS 'Indique si l''anomalie doit rendre non conforme (TRUE) ou indisponible (FALSE)';

INSERT INTO remocra.anomalie (anomalie_id, anomalie_code, anomalie_libelle, anomalie_commentaire,
    anomalie_anomalie_categorie_id, anomalie_actif, anomalie_protected, anomalie_rend_non_conforme)
VALUES
    (gen_random_uuid(), 'INDISPONIBILITE_TEMP', 'Indisponibilité temporaire', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, FALSE),

    (gen_random_uuid(), 'DEBIT_INSUFF', 'Débit insuffisant', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, FALSE),
    (gen_random_uuid(), 'DEBIT_NON_CONFORME', 'Débit non conforme', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, TRUE),
    (gen_random_uuid(), 'DEBIT_TROP_ELEVE', 'Débit trop élevé', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, FALSE),

    (gen_random_uuid(), 'PRESSION_DYN_INSUFF', 'Pression dynamique insuffisante', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, FALSE),
    (gen_random_uuid(), 'PRESSION_DYN_NON_CONFORME', 'Pression dynamique non conforme', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, TRUE),
    (gen_random_uuid(), 'PRESSION_DYN_TROP_ELEVEE', 'Pression dynamique trop élevée', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, FALSE),

    (gen_random_uuid(), 'PRESSION_INSUFF', 'Pression insuffisante', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, FALSE),
    (gen_random_uuid(), 'PRESSION_NON_CONFORME', 'Pression non conforme', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, TRUE),
    (gen_random_uuid(), 'PRESSION_TROP_ELEVEE', 'Pression trop élevée', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, FALSE),

    (gen_random_uuid(), 'VOLUME_INSUFF', 'Volume insuffisant', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, FALSE),
    (gen_random_uuid(), 'VOLUME_NON_CONFORME', 'Volume non conforme', 'Anomalie système, ne pas modifier', (SELECT anomalie_categorie_id FROM remocra.anomalie_categorie WHERE anomalie_categorie_code = 'SYSTEME'), TRUE, TRUE, TRUE);



CREATE TABLE remocra.poids_anomalie (
    poids_anomalie_id                       UUID PRIMARY KEY,
    poids_anomalie_anomalie_id              UUID NOT NULL REFERENCES remocra.anomalie (anomalie_id),
    poids_anomalie_nature_id                UUID NOT NULL REFERENCES remocra.nature (nature_id),
    poids_anomalie_type_visite              remocra."TYPE_VISITE"[],
    poids_anomalie_val_indispo_hbe          INTEGER,
    poids_anomalie_val_indispo_terrestre    INTEGER,

    UNIQUE (poids_anomalie_anomalie_id, poids_anomalie_nature_id)
);

-- Insertion des degrés de blocage pour l'anomalie 'INDISPONIBILITE_TEMP'
WITH value_to_insert AS (
    SELECT
        gen_random_uuid(),
        anomalie_id,
        nature_id,
        5, 5
    FROM remocra.anomalie
        JOIN remocra.nature ON TRUE
    WHERE anomalie_code = 'INDISPONIBILITE_TEMP'
)
INSERT INTO remocra.poids_anomalie (poids_anomalie_id, poids_anomalie_anomalie_id, poids_anomalie_nature_id,
    poids_anomalie_val_indispo_hbe, poids_anomalie_val_indispo_terrestre)
SELECT * FROM value_to_insert;



CREATE TABLE l_visite_anomalie (
    visite_id   UUID NOT NULL REFERENCES remocra.visite (visite_id),
    anomalie_id UUID NOT NULL REFERENCES remocra.anomalie (anomalie_id),

    PRIMARY KEY (visite_id, anomalie_id)
);



CREATE TABLE l_pei_anomalie (
    pei_id          UUID NOT NULL REFERENCES remocra.pei (pei_id),
    anomalie_id     UUID NOT NULL REFERENCES remocra.anomalie (anomalie_id),

    PRIMARY KEY (pei_id, anomalie_id)
);
