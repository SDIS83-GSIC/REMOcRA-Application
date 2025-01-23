DROP TABLE IF EXISTS historique.tracabilite;
DROP TYPE IF EXISTS historique.TYPE_OPERATION;
DROP TYPE IF EXISTS historique.TYPE_OBJET;

CREATE SCHEMA IF NOT EXISTS  historique;

CREATE TYPE historique.TYPE_OPERATION AS ENUM ('INSERT', 'UPDATE', 'DELETE');

CREATE TYPE historique.TYPE_OBJET AS ENUM (
    'PEI',
    'VISITE',
    'PARAMETRE',
    'GESTIONNAIRE',
    'SITE'
    -- D'autres idées ?
);

INSERT INTO remocra.utilisateur(utilisateur_id, utilisateur_actif, utilisateur_email, utilisateur_username, utilisateur_nom, utilisateur_prenom)
VALUES (
    gen_random_uuid(),
    true,
    'null',
    'UTILISATEUR_SYSTEME',
    'UTILISATEUR_SYSTEME',
    'UTILISATEUR_SYSTEME'
)ON CONFLICT DO NOTHING;

CREATE TABLE historique.tracabilite (
    tracabilite_id                  UUID                        PRIMARY KEY,
    tracabilite_type_operation      historique.TYPE_OPERATION   NOT NULL,
    tracabilite_date                TIMESTAMPTZ                 NOT NULL,
    tracabilite_objet_id            UUID                        NOT NULL,
    tracabilite_type_objet          historique.TYPE_OBJET       NOT NULL,
    tracabilite_objet_data          JSONB                       NOT NULL,
    tracabilite_auteur_id           UUID                        NOT NULL,
    tracabilite_auteur_data         JSONB                       NOT NULL
);
