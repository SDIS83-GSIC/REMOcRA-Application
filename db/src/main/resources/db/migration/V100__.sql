CREATE TABLE remocra.message_evenement (
    message_id UUID PRIMARY KEY,
    message_date_constat TIMESTAMPTZ NOT NULL,
    message_objet TEXT NOT NULL,
    message_description TEXT NOT NULL,
    message_origine TEXT NOT NULL,
    message_tag TEXT NOT NULL,
    message_importance INT CHECK (message_importance BETWEEN 0 AND 5),
    utilisateur_id UUID REFERENCES utilisateur(utilisateur_id),
    evenement_id UUID REFERENCES evenement(evenement_id) NOT NULL
);

ALTER TYPE historique.type_objet ADD VALUE 'MESSAGE';
