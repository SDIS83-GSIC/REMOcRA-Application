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

INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'BUFFER_CARTE', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'MENTION_CNIL', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'MESSAGE_ENTETE', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PROFONDEUR_COUVERTURE', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'TITRE_PAGE', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'TOLERANCE_VOIES_METRES', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'AFFICHAGE_INDISPO', null, 'BOOLEAN');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'AFFICHAGE_SYMBOLES_NORMALISES', null, 'BOOLEAN');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'CARACTERISTIQUE_PENA', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'CARACTERISTIQUE_PIBI', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'DUREE_VALIDITE_TOKEN', 1200, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'GESTION_AGENT', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'MDP_ADMINISTRATEUR', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'MODE_DECONNECTE', null, 'BOOLEAN');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'CREATION_PEI_MOBILE', null, 'BOOLEAN');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'COORDONNEES_FORMAT_AFFICHAGE', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'DECI_DISTANCE_MAX_PARCOURS', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'DECI_ISODISTANCES', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PERMIS_TOLERANCE_CHARGEMENT_METRES', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_COLONNES', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_DELAI_CTRL_URGENT', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_DELAI_CTRL_WARN', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_DELAI_RECO_URGENT', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_DELAI_RECO_WARN', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_DEPLACEMENT_DIST_WARN', null, 'BOOLEAN');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_GENERATION_CARTE_TOURNEE', null, 'BOOLEAN');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_HIGHLIGHT_DUREE', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_METHODE_TRI_ALPHANUMERIQUE', null, 'BOOLEAN');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_NOMBRE_HISTORIQUE', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_RENOUVELLEMENT_CTRL_PRIVE', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_RENOUVELLEMENT_CTRL_PUBLIC', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_RENOUVELLEMENT_RECO_PRIVE', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_RENOUVELLEMENT_RECO_PUBLIC', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_TOLERANCE_COMMUNE_METRES', null, 'INTEGER');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type) values(gen_random_uuid(), 'PEI_RENUMEROTATION_INTERNE_AUTO', null, 'BOOLEAN');