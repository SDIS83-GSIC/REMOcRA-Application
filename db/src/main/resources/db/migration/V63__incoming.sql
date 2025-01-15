CREATE SCHEMA IF NOT EXISTS incoming;

CREATE TABLE incoming.gestionnaire (
   gestionnaire_id  UUID      PRIMARY KEY,
   gestionnaire_libelle TEXT  NOT NULL,
   gestionnaire_code TEXT     NOT NULL
);

CREATE TABLE incoming.contact (
    contact_id                      UUID                        PRIMARY KEY,
    gestionnaire_id                 UUID references incoming.gestionnaire(gestionnaire_id),
    contact_actif                   BOOLEAN                     NOT NULL,
    contact_civilite                remocra.TYPE_CIVILITE,
    contact_fonction_contact_id     UUID references remocra.fonction_contact(fonction_contact_id),
    contact_nom                     TEXT,
    contact_prenom                  TEXT,
    contact_numero_voie             TEXT,
    contact_suffixe_voie            TEXT,
    contact_lieu_dit_text           TEXT,
    contact_lieu_dit_id             UUID  REFERENCES remocra.lieu_dit(lieu_dit_id),
    contact_voie_text               TEXT,
    contact_voie_id                 UUID  REFERENCES remocra.voie(voie_id),
    contact_code_postal             TEXT,
    contact_commune_text            TEXT,
    contact_commune_id              UUID  REFERENCES remocra.commune(commune_id),
    contact_pays                    TEXT,
    contact_telephone               TEXT,
    contact_email                   TEXT
);

CREATE TABLE incoming.l_contact_role (
    contact_id UUID references incoming.contact(contact_id),
    role_id  UUID references remocra.role_contact(role_contact_id),

    PRIMARY KEY(role_id, contact_id)
);

CREATE TABLE incoming.new_pei(
   new_pei_id                      UUID            PRIMARY KEY,
   new_pei_type_pei                "TYPE_PEI"      NOT NULL,
   new_pei_voie_id                 UUID REFERENCES remocra.voie (voie_id),
   new_pei_geometrie               geometry        NOT NULL,
   new_pei_lieu_dit_id             UUID REFERENCES remocra.lieu_dit (lieu_dit_id),
   new_pei_observation             TEXT,
   new_pei_commune_id              UUID            NOT NULL REFERENCES remocra.commune (commune_id),
   new_pei_nature_id               UUID            NOT NULL REFERENCES remocra.nature (nature_id),
   new_pei_nature_deci_id          UUID            NOT NULL REFERENCES remocra.nature_deci (nature_deci_id),
   new_pei_gestionnaire_id         UUID REFERENCES remocra.gestionnaire (gestionnaire_id)
);

--Permet de bloquer les géométries des PEI a des points uniques, impossible de donner un multipolygone ou une ligne
ALTER TABLE incoming.new_pei
    ADD CONSTRAINT geometrie_new_pei CHECK (geometrytype(new_pei_geometrie) = 'POINT'::text);

CREATE TABLE incoming.tournee(
    tournee_id  UUID PRIMARY KEY,
    tournee_libelle  TEXT,
    tournee_date_debut_synchro  TIMESTAMPTZ,
    tournee_date_fin_synchro  TIMESTAMPTZ
);

CREATE TABLE incoming.visite (
    visite_id                   UUID    PRIMARY KEY,
    visite_pei_id               UUID    NOT NULL references remocra.pei(pei_id),
    visite_tournee_id           UUID    NOT NULL references incoming.tournee(tournee_id),
    visite_date                 TIMESTAMPTZ  NOT NULL,
    visite_type_visite          remocra."TYPE_VISITE"   NOT NULL,
    visite_agent1               TEXT,
    visite_agent2               TEXT,
    visite_observation          TEXT,
    has_anomalie_changes        BOOLEAN
);

CREATE TABLE incoming.visite_ctrl_debit_pression (
    visite_ctrl_debit_pression_visite_id        UUID    REFERENCES incoming.visite (visite_id),
    visite_ctrl_debit_pression_debit            INTEGER,
    visite_ctrl_debit_pression_pression         DECIMAL(5, 2),
    visite_ctrl_debit_pression_pression_dyn     DECIMAL(5, 2)
);

CREATE TABLE incoming.l_visite_anomalie (
    visite_id   UUID NOT NULL REFERENCES incoming.visite (visite_id),
    anomalie_id UUID NOT NULL REFERENCES remocra.anomalie (anomalie_id),

    PRIMARY KEY (visite_id, anomalie_id)
);

CREATE TABLE incoming.photo_pei(
    photo_id        UUID         PRIMARY KEY,
    pei_id          UUID         NOT NULL REFERENCES remocra.pei (pei_id),
    photo_libelle   TEXT         NOT NULL,
    photo_date      TIMESTAMPTZ  NOT NULL,
    photo_path      TEXT         NOT NULL
);
