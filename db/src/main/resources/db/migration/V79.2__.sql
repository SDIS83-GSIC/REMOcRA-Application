CREATE TABLE remocra.oldeb_type_categorie_anomalie
(
    oldeb_type_categorie_anomalie_id      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_categorie_anomalie_actif   BOOLEAN NOT NULL,
    oldeb_type_categorie_anomalie_code    TEXT    NOT NULL UNIQUE,
    oldeb_type_categorie_anomalie_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.oldeb_type_anomalie
(
    oldeb_type_anomalie_id                               UUID    NOT NULL PRIMARY KEY,
    oldeb_type_anomalie_actif                            BOOLEAN NOT NULL,
    oldeb_type_anomalie_code                             TEXT    NOT NULL UNIQUE,
    oldeb_type_anomalie_libelle                          TEXT    NOT NULL,
    oldeb_type_anomalie_oldeb_type_categorie_anomalie_id UUID    NOT NULL REFERENCES remocra.oldeb_type_categorie_anomalie (oldeb_type_categorie_anomalie_id)

);

CREATE TABLE remocra.oldeb_type_categorie_caracteristique
(
    oldeb_type_categorie_caracteristique_id      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_categorie_caracteristique_actif   BOOLEAN NOT NULL,
    oldeb_type_categorie_caracteristique_code    TEXT    NOT NULL UNIQUE,
    oldeb_type_categorie_caracteristique_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.oldeb_type_caracteristique
(
    oldeb_type_caracteristique_id                      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_caracteristique_actif                   BOOLEAN NOT NULL,
    oldeb_type_caracteristique_code                    TEXT    NOT NULL UNIQUE,
    oldeb_type_caracteristique_libelle                 TEXT    NOT NULL,
    oldeb_type_caracteristique_oldeb_type_categorie_id UUID    NOT NULL REFERENCES remocra.oldeb_type_categorie_caracteristique (oldeb_type_categorie_caracteristique_id)
);

CREATE TABLE remocra.oldeb_type_suite
(
    oldeb_type_suite_id      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_suite_actif   BOOLEAN NOT NULL,
    oldeb_type_suite_code    TEXT    NOT NULL UNIQUE,
    oldeb_type_suite_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.oldeb_type_residence
(
    oldeb_type_residence_id      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_residence_actif   BOOLEAN NOT NULL,
    oldeb_type_residence_code    TEXT    NOT NULL UNIQUE,
    oldeb_type_residence_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.oldeb_type_debroussaillement
(
    oldeb_type_debroussaillement_id      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_debroussaillement_actif   BOOLEAN NOT NULL,
    oldeb_type_debroussaillement_code    TEXT    NOT NULL UNIQUE,
    oldeb_type_debroussaillement_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.oldeb_type_avis
(
    oldeb_type_avis_id      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_avis_actif   BOOLEAN NOT NULL,
    oldeb_type_avis_code    TEXT    NOT NULL UNIQUE,
    oldeb_type_avis_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.oldeb_type_action
(
    oldeb_type_action_id      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_action_actif   BOOLEAN NOT NULL,
    oldeb_type_action_code    TEXT    NOT NULL UNIQUE,
    oldeb_type_action_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.oldeb_type_acces
(
    oldeb_type_acces_id      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_acces_actif   BOOLEAN NOT NULL,
    oldeb_type_acces_code    TEXT    NOT NULL UNIQUE,
    oldeb_type_acces_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.oldeb_type_zone_urbanisme
(
    oldeb_type_zone_urbanisme_id      UUID    NOT NULL PRIMARY KEY,
    oldeb_type_zone_urbanisme_actif   BOOLEAN NOT NULL,
    oldeb_type_zone_urbanisme_code    TEXT    NOT NULL UNIQUE,
    oldeb_type_zone_urbanisme_libelle TEXT    NOT NULL
);

CREATE TABLE remocra.oldeb
(
    oldeb_id                           UUID PRIMARY KEY,
    oldeb_geometrie                    geometry NOT NULL,
    oldeb_commune_id                   UUID     NOT NULL REFERENCES remocra.commune (commune_id),
    oldeb_cadastra_section_id          UUID     NOT NULL REFERENCES remocra.cadastre_section (cadastre_section_id),
    oldeb_cadastre_parcelle_id         UUID     NOT NULL REFERENCES remocra.cadastre_parcelle (cadastre_parcelle_id),
    oldeb_oldeb_type_acces_id          UUID REFERENCES remocra.oldeb_type_acces (oldeb_type_acces_id),
    oldeb_oldeb_type_zone_urbanisme_id UUID REFERENCES remocra.oldeb_type_zone_urbanisme (oldeb_type_zone_urbanisme_id),
    oldeb_num_voie                     TEXT,
    oldeb_voie_id                      UUID REFERENCES remocra.voie (voie_id),
    oldeb_lieu_dit_id                  UUID REFERENCES remocra.lieu_dit (lieu_dit_id),
    oldeb_volume                       INTEGER  NOT NULL,
    oldeb_largeur_acces                INTEGER,
    oldeb_portail_electrique           BOOLEAN  NOT NULL,
    oldeb_code_portail                 TEXT,
    oldeb_actif                        BOOLEAN
);

CREATE TABLE remocra.oldeb_caracteristique
(
    oldeb_id                      UUID NOT NULL REFERENCES remocra.oldeb (oldeb_id),
    oldeb_type_caracteristique_id UUID NOT NULL REFERENCES remocra.oldeb_type_caracteristique (oldeb_type_caracteristique_id),
    PRIMARY KEY (oldeb_id, oldeb_type_caracteristique_id)
);

CREATE TABLE remocra.oldeb_locataire
(
    oldeb_locataire_id             UUID PRIMARY KEY,
    oldeb_locataire_organisme      BOOLEAN               NOT NULL,
    oldeb_locataire_raison_sociale TEXT,
    oldeb_locataire_civilite       remocra.TYPE_CIVILITE NOT NULL,
    oldeb_locataire_nom            TEXT                  NOT NULL,
    oldeb_locataire_prenom         TEXT                  NOT NULL,
    oldeb_locataire_telephone      TEXT,
    oldeb_locataire_email          TEXT,
    oldeb_locataire_oldeb_id       UUID                  NOT NULL UNIQUE REFERENCES remocra.oldeb (oldeb_id)
);

CREATE TABLE remocra.oldeb_proprietaire
(
    oldeb_proprietaire_id             UUID PRIMARY KEY,
    oldeb_proprietaire_organisme      BOOLEAN               NOT NULL,
    oldeb_proprietaire_raison_sociale TEXT,
    oldeb_proprietaire_civilite       remocra.TYPE_CIVILITE NOT NULL,
    oldeb_proprietaire_nom            TEXT                  NOT NULL,
    oldeb_proprietaire_prenom         TEXT                  NOT NULL,
    oldeb_proprietaire_telephone      TEXT,
    oldeb_proprietaire_email          TEXT,
    oldeb_proprietaire_num_voie       TEXT,
    oldeb_proprietaire_voie           TEXT,
    oldeb_proprietaire_lieu_dit       TEXT,
    oldeb_proprietaire_code_postal    TEXT                  NOT NULL,
    oldeb_proprietaire_ville          TEXT                  NOT NULL,
    oldeb_proprietaire_pays           TEXT                  NOT NULL
);

CREATE TABLE remocra.oldeb_propriete
(
    oldeb_propriete_id                      UUID PRIMARY KEY,
    oldeb_propriete_oldeb_id                UUID NOT NULL REFERENCES remocra.oldeb (oldeb_id),
    oldeb_propriete_oldeb_proprietaire_id   UUID NOT NULL REFERENCES remocra.oldeb_proprietaire (oldeb_proprietaire_id),
    oldeb_propriete_oldeb_type_residence_id UUID NOT NULL REFERENCES remocra.oldeb_type_residence (oldeb_type_residence_id)
);

CREATE TABLE remocra.oldeb_visite
(
    oldeb_visite_id                            UUID PRIMARY KEY,
    oldeb_visite_code                          TEXT NOT NULL UNIQUE,
    oldeb_visite_date_visite                   TIMESTAMPTZ,
    oldeb_visite_agent                         TEXT NOT NULL,
    oldeb_visite_observation                   TEXT,
    oldeb_visite_oldeb_id                      UUID NOT NULL REFERENCES remocra.oldeb (oldeb_id),
    oldeb_visite_debroussaillement_parcelle_id UUID NOT NULL REFERENCES remocra.oldeb_type_debroussaillement (oldeb_type_debroussaillement_id),
    oldeb_visite_debroussaillement_acces_id    UUID NOT NULL REFERENCES remocra.oldeb_type_debroussaillement (oldeb_type_debroussaillement_id),
    oldeb_visite_oldeb_type_avis_id            UUID NOT NULL REFERENCES remocra.oldeb_type_avis (oldeb_type_avis_id),
    oldeb_visite_oldeb_type_action_id          UUID NOT NULL REFERENCES remocra.oldeb_type_action (oldeb_type_action_id)
);

CREATE TABLE remocra.oldeb_visite_anomalie
(
    oldeb_visite_id        UUID NOT NULL REFERENCES remocra.oldeb_visite (oldeb_visite_id),
    oldeb_type_anomalie_id UUID NOT NULL REFERENCES remocra.oldeb_type_anomalie (oldeb_type_anomalie_id),
    PRIMARY KEY (oldeb_visite_id, oldeb_type_anomalie_id)
);

CREATE TABLE remocra.oldeb_visite_suite
(
    oldeb_visite_suite_id                  UUID NOT NULL PRIMARY KEY,
    oldeb_visite_suite_oldeb_visite_id     UUID NOT NULL REFERENCES remocra.oldeb_visite (oldeb_visite_id),
    oldeb_visite_suite_oldeb_type_suite_id UUID NOT NULL REFERENCES remocra.oldeb_type_suite (oldeb_type_suite_id),
    oldeb_visite_suite_date                TIMESTAMPTZ,
    oldeb_visite_suite_observation         TEXT
);

CREATE TABLE remocra.oldeb_visite_document
(
    oldeb_visite_document_id              UUID NOT NULL PRIMARY KEY,
    oldeb_visite_document_oldeb_visite_id UUID NOT NULL REFERENCES remocra.oldeb_visite (oldeb_visite_id),
    oldeb_visite_document_document_id     UUID NOT NULL REFERENCES remocra.document (document_id)
);
