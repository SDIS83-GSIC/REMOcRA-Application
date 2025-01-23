CREATE TYPE remocra."TYPE_DESTINATAIRE" AS ENUM (
    'UTILISATEUR'
    --Ajouter au besoin
    );

CREATE TABLE remocra.courrier
(
    courrier_id                UUID PRIMARY KEY,
    courrier_document_id       UUID NOT NULL REFERENCES remocra.document,
    courrier_reference         TEXT NOT NULL,
    courrier_type_destinataire remocra."TYPE_DESTINATAIRE",
    courrier_objet             TEXT NOT NULL,
    courrier_expediteur        UUID REFERENCES remocra.organisme
);
COMMENT
    ON COLUMN remocra.courrier.courrier_expediteur
    IS 'Nom de l''organisme de l''utilisateur connecté (qui envoie le courrier)';

CREATE TABLE remocra.l_thematique_courrier
(
    courrier_id   UUID NOT NULL REFERENCES remocra.courrier,
    thematique_id UUID NOT NULL REFERENCES remocra.thematique,
    PRIMARY KEY (courrier_id, thematique_id)
);

CREATE TABLE remocra.l_courrier_utilisateur
(
    courrier_id      UUID NOT NULL REFERENCES remocra.courrier,
    utilisateur_id   UUID NOT NULL REFERENCES remocra.utilisateur,
    accuse_reception TIMESTAMPTZ,
    PRIMARY KEY (courrier_id, utilisateur_id)
);
