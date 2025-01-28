INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'PEI_LONGUE_INDISPONIBILITE_MESSAGE', 'Un ou plusieurs PEI situé(s) sur votre territoire est/sont indisponible(s) depuis plus de #MOIS# mois et #JOURS# jours.', 'STRING'::remocra."TYPE_PARAMETRE"),
(gen_random_uuid(), 'PEI_LONGUE_INDISPONIBILITE_JOURS', '31', 'INTEGER'::remocra."TYPE_PARAMETRE"),
(gen_random_uuid(), 'PEI_LONGUE_INDISPONIBILITE_TYPE_ORGANISME', '', 'STRING'::remocra."TYPE_PARAMETRE");