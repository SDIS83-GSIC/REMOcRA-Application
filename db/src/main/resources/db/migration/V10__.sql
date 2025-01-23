DROP TABLE IF EXISTS remocra.l_contact_role;
DROP TABLE IF EXISTS remocra.role;
DROP TABLE IF EXISTS remocra.l_contact_gestionnaire;
DROP TABLE IF EXISTS remocra.l_contact_organisme;
DROP TABLE IF EXISTS remocra.contact;
DROP TYPE IF EXISTS remocra.TYPE_APPARTENANCE;
DROP TYPE IF EXISTS remocra.TYPE_CIVILITE;
DROP TYPE IF EXISTS remocra.TYPE_FONCTION;

CREATE TYPE remocra.TYPE_CIVILITE as ENUM(
    'MADAME',
    'MONSIEUR',
    'AUTRE'
);

CREATE TYPE remocra.TYPE_FONCTION as ENUM(
    'MAIRE',
    'PRESIDENT',
    'RESPONSABLE',
    'DIRECTEUR'
);

CREATE TABLE remocra.contact (
    contact_id                      UUID                        PRIMARY KEY,
    contact_actif                   BOOLEAN                     NOT NULL,
    contact_civilite                remocra.TYPE_CIVILITE,
    contact_fonction                remocra.TYPE_FONCTION,
    contact_nom                     TEXT,
    contact_prenom                  TEXT,
    contact_numero_voie             TEXT,
    contact_suffixe_voie            TEXT,
    contact_lieu_dit                TEXT,
    contact_voie                    TEXT,
    contact_code_postal             TEXT,
    contact_ville                   TEXT,
    contact_pays                    TEXT,
    contact_telephone               TEXT,
    contact_email                   TEXT
);

CREATE TABLE remocra.l_contact_gestionnaire (
    contact_id              UUID            UNIQUE NOT NULL REFERENCES remocra.contact(contact_id),
    gestionnaire_id         UUID            NOT NULL REFERENCES remocra.gestionnaire(gestionnaire_id),
    gestionnaire_site_id    UUID            REFERENCES remocra.gestionnaire(gestionnaire_id),

    PRIMARY KEY (contact_id, gestionnaire_id)
);

CREATE TABLE remocra.l_contact_organisme (
    contact_id           UUID            UNIQUE NOT NULL REFERENCES remocra.contact(contact_id),
    organisme_id         UUID            NOT NULL REFERENCES remocra.organisme(organisme_id),

    PRIMARY KEY (contact_id, organisme_id)
);

CREATE TABLE remocra.role (
    role_id                 UUID            PRIMARY KEY,
    role_actif              BOOLEAN         NOT NULL,
    role_code               TEXT            UNIQUE NOT NULL,
    role_libelle            TEXT            NOT NULL
);


CREATE TABLE remocra.l_contact_role (
    contact_id              UUID            NOT NULL REFERENCES remocra.contact(contact_id),
    role_id                 UUID            NOT NULL REFERENCES remocra.role(role_id),

    PRIMARY KEY (contact_id, role_id)
);

INSERT INTO remocra.role (role_id, role_actif, role_code, role_libelle)
VALUES (gen_random_uuid(), true, 'DESTINATAIRE_MAIRE_ROP', 'Maire destinataire des ROP')
on CONFLICT (role_code) DO NOTHING;