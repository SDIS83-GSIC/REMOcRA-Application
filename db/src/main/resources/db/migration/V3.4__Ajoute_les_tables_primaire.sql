DROP TABLE IF EXISTS remocra.domaine;
DROP TABLE IF EXISTS remocra.nature;
DROP TABLE IF EXISTS remocra.niveau;
DROP TABLE IF EXISTS remocra.materiau;
DROP TABLE IF EXISTS remocra.diametre;
DROP TABLE IF EXISTS remocra.nature;
DROP TABLE IF EXISTS remocra.marque_pibi;
DROP TABLE IF EXISTS remocra.modele_pibi;
DROP TABLE IF EXISTS remocra.reservoir;

CREATE TABLE remocra.domaine
(
    domaine_id      UUID PRIMARY KEY,
    domaine_actif   BOOLEAN     NOT NULL,
    domaine_code    TEXT UNIQUE NOT NULL,
    domaine_libelle TEXT        NOT NULL
);

INSERT INTO remocra.domaine
    (domaine_id, domaine_actif, domaine_code, domaine_libelle)
VALUES (gen_random_uuid(), true, 'COMMUNAL', 'Communal'),
       (gen_random_uuid(), true, 'DOMAINE', 'Domanial'),
       (gen_random_uuid(), true, 'DEPARTEMENT', 'Départemental'),
       (gen_random_uuid(), true, 'MILITAIRE', 'Militaire'),
       (gen_random_uuid(), true, 'PRIVE', 'Privé');

CREATE TABLE remocra.niveau
(
    niveau_id      UUID PRIMARY KEY,
    niveau_actif   BOOLEAN     NOT NULL,
    niveau_code    TEXT UNIQUE NOT NULL,
    niveau_libelle TEXT        NOT NULL
);
INSERT INTO remocra.niveau
    (niveau_id, niveau_actif, niveau_code, niveau_libelle)
VALUES (gen_random_uuid(), true, 'VP', 'Voie publique'),
       (gen_random_uuid(), true, 'INFRA', 'Infrastructure'),
       (gen_random_uuid(), true, 'SUPER', 'Superstructure');

CREATE TABLE remocra.nature
(
    nature_id       UUID PRIMARY KEY,
    nature_actif    BOOLEAN     NOT NULL,
    nature_code     TEXT UNIQUE NOT NULL,
    nature_libelle  TEXT        NOT NULL,
    nature_type_pei "TYPE_PEI"  NOT NULL
);
--Les deux nature dont on est sûr le reste c'est en fonction des SDIS
INSERT INTO remocra.nature
    (nature_id, nature_actif, nature_code, nature_libelle, nature_type_pei)
VALUES (gen_random_uuid(), true, 'BI', 'BI', 'PIBI'),
       (gen_random_uuid(), true, 'PI', 'PI', 'PIBI');

CREATE TABLE remocra.materiau
(
    materiau_id      UUID PRIMARY KEY,
    materiau_actif   BOOLEAN NOT NULL,
    materiau_code    TEXT    NOT NULL UNIQUE,
    materiau_libelle TEXT    NOT NULL
);

INSERT INTO remocra.materiau
    (materiau_id, materiau_actif, materiau_code, materiau_libelle)
VALUES (gen_random_uuid(), true, 'INCONNU', 'Inconnu');
INSERT INTO remocra.materiau
    (materiau_id, materiau_actif, materiau_code, materiau_libelle)
VALUES (gen_random_uuid(), true, 'BETONNE', 'Bétonné');
INSERT INTO remocra.materiau
    (materiau_id, materiau_actif, materiau_code, materiau_libelle)
VALUES (gen_random_uuid(), true, 'METALLIQUE', 'Métallique');
INSERT INTO remocra.materiau
    (materiau_id, materiau_actif, materiau_code, materiau_libelle)
VALUES (gen_random_uuid(), true, 'PLASTIQUE', 'Plastique');
INSERT INTO remocra.materiau
    (materiau_id, materiau_actif, materiau_code, materiau_libelle)
VALUES (gen_random_uuid(), true, 'SOUPLE', 'Souple');


CREATE TABLE remocra.diametre
(
    diametre_id        UUID PRIMARY KEY,
    diametre_actif     BOOLEAN NOT NULL,
    diametre_code      TEXT    NOT NULL UNIQUE,
    diametre_libelle   TEXT    NOT NULL,
    diametre_protected BOOLEAN NOT NULL

);

COMMENT
    ON COLUMN remocra.diametre.diametre_code
    IS 'Des codes uniques et immuables qui peuvent être utilisés dans l''appli pour des calculs ou autres';

COMMENT
    ON COLUMN remocra.diametre.diametre_libelle
    IS 'Libellé permettant au SDIS de personnaliser l''affichage en front de chaque diamètre';

INSERT INTO remocra.diametre
(diametre_id, diametre_actif, diametre_code, diametre_libelle, diametre_protected)
VALUES (gen_random_uuid(), true, 'DIAM100', '100', true);
INSERT INTO remocra.diametre
(diametre_id, diametre_actif, diametre_code, diametre_libelle, diametre_protected)
VALUES (gen_random_uuid(), true, 'DIAM80', '80', true);
INSERT INTO remocra.diametre
(diametre_id, diametre_actif, diametre_code, diametre_libelle, diametre_protected)
VALUES (gen_random_uuid(), true, 'DIAM150', '150', true);



CREATE TABLE remocra.marque_pibi
(
    marque_pibi_id      UUID PRIMARY KEY,
    marque_pibi_actif   BOOLEAN NOT NULL,
    marque_pibi_code    TEXT    NOT NULL UNIQUE,
    marque_pibi_libelle TEXT    NOT NULL
);
INSERT INTO remocra.marque_pibi
(marque_pibi_id, marque_pibi_actif, marque_pibi_code, marque_pibi_libelle)
VALUES (gen_random_uuid(), true, 'AVK', 'AVK');

INSERT INTO remocra.marque_pibi
(marque_pibi_id, marque_pibi_actif, marque_pibi_code, marque_pibi_libelle)
VALUES (gen_random_uuid(), true, 'BAYARD', 'BAYARD');

INSERT INTO remocra.marque_pibi
(marque_pibi_id, marque_pibi_actif, marque_pibi_code, marque_pibi_libelle)
VALUES (gen_random_uuid(), true, 'CHAPPEE', 'CHAPPEE');

INSERT INTO remocra.marque_pibi
(marque_pibi_id, marque_pibi_actif, marque_pibi_code, marque_pibi_libelle)
VALUES (gen_random_uuid(), true, 'PONT_A_MOUSSON', 'PONT-A-MOUSSON');

INSERT INTO remocra.marque_pibi
(marque_pibi_id, marque_pibi_actif, marque_pibi_code, marque_pibi_libelle)
VALUES (gen_random_uuid(), true, 'GHM', 'GHM');
INSERT INTO remocra.marque_pibi
(marque_pibi_id, marque_pibi_actif, marque_pibi_code, marque_pibi_libelle)
VALUES (gen_random_uuid(), true, 'INCONNUE', 'INCONNUE');

CREATE TABLE remocra.modele_pibi
(
    modele_pibi_id        UUID PRIMARY KEY,
    modele_pibi_actif     BOOLEAN NOT NULL,
    modele_pibi_code      TEXT    NOT NULL UNIQUE,
    modele_pibi_libelle   TEXT    NOT NULL,
    modele_pibi_marque_id UUID NOT NULL REFERENCES remocra.marque_pibi (marque_pibi_id)

);
INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'DAUPHINE', 'DAUPHINE',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'AVK'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'ORION', 'ORION',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'AVK'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'ORION2', 'ORION 2',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'AVK'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'PEGASE', 'PEGASE',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'AVK'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'PHENIX', 'PHENIX',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'AVK'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'VEGA', 'VEGA',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'AVK'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'RETRO', 'RETRO',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'BAYARD'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'NON-INCONGELABLE', 'NON INCONGELABLE',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'BAYARD'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'INCONGELABLE', 'INCONGELABLE',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'BAYARD'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'EMERAUDE_PARSEC', 'EMERAUDE PARSEC',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'BAYARD'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'EMERAUDE', 'EMERAUDE',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'BAYARD'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'DAUPHIN', 'DAUPHIN',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'BAYARD'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'SAPHIR_PARSEC', 'SAPHIR PARSEC',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'BAYARD'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'SAPHIR', 'SAPHIR',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'BAYARD'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'AJAX', 'AJAX',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'PONT_A_MOUSSON'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'ATLAS', 'ATLAS',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'PONT_A_MOUSSON'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'ATLAS_PLUS', 'ATLAS PLUS',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'PONT_A_MOUSSON'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'C9', 'C9',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'PONT_A_MOUSSON'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'ELANCIO', 'ELANCIO',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'PONT_A_MOUSSON'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'HERMES', 'HERMES',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'PONT_A_MOUSSON'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'HERMES_PLUS', 'HERMES PLUS',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'PONT_A_MOUSSON'));

INSERT INTO remocra.modele_pibi
(modele_pibi_id, modele_pibi_actif, modele_pibi_code, modele_pibi_libelle, modele_pibi_marque_id)
VALUES (gen_random_uuid(), true, 'RATIONNEL', 'RATIONNEL',
        (SELECT marque_pibi_id FROM remocra.marque_pibi WHERE marque_pibi_code = 'PONT_A_MOUSSON'));

CREATE TABLE remocra.reservoir
(
    reservoir_id       UUID PRIMARY KEY,
    reservoir_actif    BOOLEAN NOT NULL,
    reservoir_code     TEXT    NOT NULL UNIQUE,
    reservoir_libelle  TEXT    NOT NULL,
    reservoir_capacite INTEGER NOT NULL
);

COMMENT
    ON COLUMN remocra.reservoir.reservoir_capacite
    IS 'En m3 (mètre cube)';



