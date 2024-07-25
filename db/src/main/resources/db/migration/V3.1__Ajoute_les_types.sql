DROP TYPE IF EXISTS remocra.TYPE_PEI;
DROP TYPE IF EXISTS remocra.DISPONIBLITE;
DROP TYPE IF EXISTS remocra.TYPE_ZONE_INTEGRATION;

DROP TABLE IF EXISTS remocra.type_organisme;
DROP TABLE IF EXISTS remocra.type_canalisation;
DROP TABLE IF EXISTS remocra.type_reseau;
DROP TABLE IF EXISTS remocra.nature_deci;

CREATE TYPE remocra."TYPE_PEI" AS ENUM (
    'PIBI',
    'PENA'
    );


CREATE TYPE remocra."DISPONIBILITE" AS ENUM (
    'DISPONIBLE',
    'INDISPONIBLE',
    'NON_CONFORME'
    );
CREATE TYPE remocra."TYPE_ZONE_INTEGRATION" AS ENUM (
    'ZONE_SPECIALE',
    'ZONE_COMPETENCE'
    );

CREATE TABLE remocra.type_organisme
(
    type_organisme_id        UUID PRIMARY KEY,
    type_organisme_actif     BOOLEAN     NOT NULL,
    --Doit etre mis a false par défaut en kotlin car jooq le considère comme nullable sinon
    type_organisme_protected BOOLEAN     NOT NULL,
    type_organisme_code      TEXT UNIQUE NOT NULL,
    type_organisme_libelle   TEXT        NOT NULL,
    type_organisme_parent_id UUID REFERENCES remocra.type_organisme (type_organisme_id)
);

COMMENT
    ON COLUMN remocra.type_organisme.type_organisme_protected
    IS 'Permet d''avoir des valeurs protégées comme "SERVICE_EAU" par exemple';

COMMENT
    ON COLUMN remocra.type_organisme.type_organisme_code
    IS 'Code unique servant à identifier certains types utilisés dans l''application';


INSERT INTO remocra.type_organisme
(type_organisme_id, type_organisme_actif, type_organisme_protected, type_organisme_code, type_organisme_libelle,
 type_organisme_parent_id)
VALUES (gen_random_uuid(), true, true, 'SERVICE_EAUX', 'Service des eaux', null),
       (gen_random_uuid(), true, true, 'PRESTATAIRE_TECHNIQUE', 'Prestataire technique', null),
       (gen_random_uuid(), true, true, 'AUTRE_SERVICE_PUBLIC_DECI', 'Autre Services Public DECI', null),
       (gen_random_uuid(), true, true, 'COMMUNE', 'Commune', null),
       (gen_random_uuid(), true, true, 'EPCI', 'EPCI', null),
       (gen_random_uuid(), true, true, 'PREFECTURE', 'Préfecture', null),
       (gen_random_uuid(), true, true, 'REMOCRA', 'Organisme des dev', NULL);



CREATE TABLE remocra.type_canalisation
(
    type_canalisation_id      UUID PRIMARY KEY,
    type_canalisation_actif   BOOLEAN NOT NULL,
    type_canalisation_code    TEXT    NOT NULL UNIQUE,
    type_canalisation_libelle TEXT    NOT NULL
);

INSERT INTO remocra.type_canalisation
(type_canalisation_id, type_canalisation_actif, type_canalisation_code, type_canalisation_libelle)
VALUES (gen_random_uuid(), true, 'FONTE', 'Fonte'),
       (gen_random_uuid(), true, 'PLOMB', 'Plomb'),
       (gen_random_uuid(), true, 'ACIER', 'Acier'),
       (gen_random_uuid(), true, 'NON_RENSEIGNE', 'Non renseigné');


CREATE TABLE remocra.type_reseau
(
    type_reseau_id      UUID PRIMARY KEY,
    type_reseau_actif   BOOLEAN NOT NULL,
    type_reseau_code    TEXT    NOT NULL UNIQUE,
    type_reseau_libelle TEXT    NOT NULL

);

INSERT INTO remocra.type_reseau
(type_reseau_id, type_reseau_actif, type_reseau_code, type_reseau_libelle)
VALUES (gen_random_uuid(), true, 'ANTENNE', 'Antenne'),
       (gen_random_uuid(), true, 'RAMIFIE', 'Ramifié'),
       (gen_random_uuid(), true, 'BOUCLE', 'Boucle'),
       (gen_random_uuid(), true, 'MAILLE', 'Maillé'),
       (gen_random_uuid(), true, 'NC', 'Non-classé');


CREATE TABLE remocra.nature_deci
(
    nature_deci_id   UUID PRIMARY KEY,
    nature_deci_actif  BOOLEAN NOT NULL,
    nature_deci_code VARCHAR NOT NULL UNIQUE,
    nature_deci_libelle  VARCHAR NOT NULL,
    nature_deci_protected  BOOLEAN NOT NULL
);

INSERT INTO remocra.nature_deci
(nature_deci_id, nature_deci_code, nature_deci_libelle, nature_deci_protected,nature_deci_actif)
VALUES(gen_random_uuid(), 'PRIVE', 'Privé', true, true);
INSERT INTO remocra.nature_deci
(nature_deci_id, nature_deci_code, nature_deci_libelle, nature_deci_protected,nature_deci_actif)
VALUES(gen_random_uuid(), 'PUBLIC', 'Public', true, true);

INSERT INTO remocra.nature_deci
(nature_deci_id, nature_deci_code, nature_deci_libelle, nature_deci_protected,nature_deci_actif)
VALUES(gen_random_uuid(), 'PRIVE_CONVENTIONNE', 'Privé sous convention', false, true);
INSERT INTO remocra.nature_deci
(nature_deci_id, nature_deci_code, nature_deci_libelle, nature_deci_protected,nature_deci_actif)
VALUES(gen_random_uuid(), 'PUBLIC_CONVENTIONNE', 'Public sous convention', false, true);