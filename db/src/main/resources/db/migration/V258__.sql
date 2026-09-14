ALTER TYPE remocra."DROIT" ADD VALUE 'ATLAS_A'; -- admin : tous les droits
ALTER TYPE remocra."DROIT" ADD VALUE 'ATLAS_C'; -- insérer les docs de l'atlas
ALTER TYPE remocra."DROIT" ADD VALUE 'ATLAS_D'; -- supprimer

CREATE TABLE remocra.atlas_document (
                                        atlas_document_id UUID PRIMARY KEY,
                                        atlas_document_document_id UUID REFERENCES remocra.document(document_id),
                                        atlas_document_actif BOOLEAN NOT NULL,
                                        atlas_document_geometrie GEOMETRY NOT NULL
);

CREATE TABLE remocra.atlas_annexe (
                                      atlas_annexe_id UUID PRIMARY KEY,
                                      atlas_annexe_document_id UUID REFERENCES remocra.document(document_id),
                                      atlas_annexe_actif BOOLEAN NOT NULL,
                                      atlas_annexe_name TEXT,
                                      atlas_annexe_order INT UNIQUE,
                                      atlas_annexe_is_visible BOOLEAN NOT NULL
);
