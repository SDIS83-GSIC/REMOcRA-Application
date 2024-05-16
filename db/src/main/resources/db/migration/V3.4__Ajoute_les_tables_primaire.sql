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
VALUES(gen_random_uuid(), true, 'VP', 'Voie publique'),
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
    materiau_id    UUID PRIMARY KEY,
    materiau_actif BOOLEAN NOT NULL,
    materiau_code  TEXT    NOT NULL UNIQUE,
    materiau_nom   TEXT    NOT NULL
);


CREATE TABLE remocra.diametre
(
    diametre_id      UUID PRIMARY KEY,
    diametre_actif   BOOLEAN NOT NULL,
    diametre_code    TEXT    NOT NULL UNIQUE,
    diametre_libelle TEXT    NOT NULL

);

COMMENT
    ON COLUMN remocra.diametre.diametre_code
    IS 'Des codes uniques et immuables qui peuvent être utilisés dans l''appli pour des calculs ou autres';

COMMENT
    ON COLUMN remocra.diametre.diametre_libelle
    IS 'Libellé permettant au SDIS de personnaliser l''affichage en front de chaque diamètre';


CREATE TABLE remocra.marque_pibi
(
    marque_pibi_id    UUID PRIMARY KEY,
    marque_pibi_actif BOOLEAN NOT NULL,
    marque_pibi_code  TEXT    NOT NULL UNIQUE,
    marque_pibi_nom   TEXT    NOT NULL
);

CREATE TABLE remocra.modele_pibi
(
    modele_pibi_id        UUID PRIMARY KEY,
    modele_pibi_actif     BOOLEAN NOT NULL,
    modele_pibi_code      TEXT    NOT NULL UNIQUE,
    modele_pibi_nom       TEXT    NOT NULL,
    modele_pibi_marque_id UUID REFERENCES remocra.marque_pibi (marque_pibi_id)

);


CREATE TABLE remocra.reservoir
(
    reservoir_id       UUID PRIMARY KEY,
    reservoir_actif    BOOLEAN NOT NULL,
    reservoir_nom      TEXT    NOT NULL,
    reservoir_capacite INTEGER NOT NULL
);

COMMENT
    ON COLUMN remocra.reservoir.reservoir_capacite
    IS 'En m3 (mètre cube)';



