CREATE TABLE l_crise_document (
    crise_id UUID REFERENCES crise(crise_id) ON DELETE CASCADE,
    document_id UUID REFERENCES document(document_id) ON DELETE CASCADE,
    PRIMARY KEY (crise_id, document_id)
);

ALTER TYPE historique.type_objet ADD VALUE 'CRISE_DOCUMENT';
