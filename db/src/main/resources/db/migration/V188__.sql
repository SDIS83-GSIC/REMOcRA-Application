INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'RECEPTION_RECO_INIT_OBLIGATOIRE', false, 'BOOLEAN'::remocra."TYPE_PARAMETRE");

INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'VALEUR_HAUTE_MINIMALE_HISTOGRAMME', null, 'INTEGER'::remocra."TYPE_PARAMETRE");
