ALTER TABLE remocra.courrier
DROP COLUMN courrier_type_destinataire;

DROP type remocra."TYPE_DESTINATAIRE";

CREATE TABLE remocra.l_courrier_organisme
(
    courrier_id      UUID NOT NULL REFERENCES remocra.courrier,
    organisme_id   UUID NOT NULL REFERENCES remocra.organisme,
    PRIMARY KEY (courrier_id, organisme_id)
);

CREATE TABLE remocra.l_courrier_contact_organisme
(
    courrier_id      UUID NOT NULL REFERENCES remocra.courrier,
    contact_id   UUID NOT NULL REFERENCES remocra.contact,
    PRIMARY KEY (courrier_id, contact_id)
);

CREATE TABLE remocra.l_courrier_contact_gestionnaire
(
    courrier_id      UUID NOT NULL REFERENCES remocra.courrier,
    contact_id   UUID NOT NULL REFERENCES remocra.contact,
    PRIMARY KEY (courrier_id, contact_id)
);

ALTER TYPE historique."type_objet" ADD VALUE 'COURRIER';
