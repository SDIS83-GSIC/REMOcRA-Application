DROP TABLE remocra.l_modele_courrier_document;

ALTER TABLE modele_courrier
ADD COLUMN modele_courrier_document_id UUID references document(document_id);
