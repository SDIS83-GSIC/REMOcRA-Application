DROP TABLE IF EXISTS remocra.l_contact_role;
DROP TABLE IF EXISTS remocra.l_contact_gestionnaire;
DROP TABLE IF EXISTS remocra.l_contact_organisme;
DROP TABLE IF EXISTS remocra.contact;



CREATE TABLE remocra.contact (
    contact_id                      UUID                        PRIMARY KEY,
    contact_actif                   BOOLEAN                     NOT NULL,
    contact_civilite                remocra.TYPE_CIVILITE,
    contact_fonction                remocra.TYPE_FONCTION,
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

CREATE TABLE remocra.l_contact_gestionnaire (
    contact_id              UUID            UNIQUE NOT NULL REFERENCES remocra.contact(contact_id),
    gestionnaire_id         UUID            NOT NULL REFERENCES remocra.gestionnaire(gestionnaire_id),
    site_id    UUID            REFERENCES remocra.site(site_id),

    PRIMARY KEY (contact_id, gestionnaire_id)
);

CREATE TABLE remocra.l_contact_organisme (
    contact_id           UUID            UNIQUE NOT NULL REFERENCES remocra.contact(contact_id),
    organisme_id         UUID            NOT NULL REFERENCES remocra.organisme(organisme_id),

    PRIMARY KEY (contact_id, organisme_id)
);

CREATE TABLE remocra.l_contact_role (
    contact_id              UUID            NOT NULL REFERENCES remocra.contact(contact_id),
    role_id                 UUID            NOT NULL REFERENCES remocra.role(role_id),

    PRIMARY KEY (contact_id, role_id)
);

ALTER TYPE historique.type_objet ADD VALUE 'CONTACT';