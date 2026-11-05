-- Table de liaison pour différencier les visites des documents :
-- permet de faire un lien entre l'objet visite et le doc qui en découle.
CREATE TABLE remocra.l_visite_document (
    visite_id UUID REFERENCES remocra.visite(visite_id),
    document_id UUID REFERENCES remocra.document(document_id),
    PRIMARY KEY (visite_id, document_id)
);
