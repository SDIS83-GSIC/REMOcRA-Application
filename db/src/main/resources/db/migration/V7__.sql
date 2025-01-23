DROP TABLE IF EXISTS remocra.l_pei_document;
DROP TABLE IF EXISTS remocra.document;

CREATE TABLE remocra.document (
    document_id              UUID                        PRIMARY KEY,
    document_date            TIMESTAMPTZ                 NOT NULL,
    document_nom_fichier     TEXT                        NOT NULL,
    document_repertoire      TEXT                        NOT NULL
);

COMMENT
    ON COLUMN remocra.document.document_date
    IS 'Date d''ajout du document dans l''application';

CREATE TABLE remocra.l_pei_document (
    pei_id           UUID           REFERENCES remocra.pei(pei_id),
    document_id      UUID           REFERENCES remocra.document(document_id),
    is_photo_pei     BOOLEAN        NOT NULL,

    PRIMARY KEY (pei_id, document_id)
);

ALTER TYPE historique.TYPE_OBJET ADD VALUE IF NOT EXISTS 'DOCUMENT_PEI';