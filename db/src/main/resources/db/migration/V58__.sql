DROP TABLE remocra.l_debit_simultane_document;

ALTER TABLE remocra.debit_simultane_mesure
ADD COLUMN debit_simultane_mesure_document_id UUID REFERENCES remocra.document(document_id);

INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'VITESSE_EAU', '', 'INTEGER'::remocra."TYPE_PARAMETRE");