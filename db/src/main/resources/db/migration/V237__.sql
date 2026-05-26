--Ajout de paramètre pour les types organisme utilisés dans le code pour les rcci
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES
  (gen_random_uuid(), 'LISTE_TYPE_ORGA_DDTM_ONF', COALESCE((SELECT array_to_json(array_agg(type_organisme_code))::text FROM type_organisme WHERE type_organisme_code IN ('DDTM', 'ONF')), NULL), 'STRING'::remocra."TYPE_PARAMETRE"),
  (gen_random_uuid(), 'LISTE_TYPE_ORGA_SDIS', COALESCE((SELECT array_to_json(array_agg(type_organisme_code))::text FROM type_organisme WHERE type_organisme_code IN ('SDIS', 'CIS', 'CIS-ETAPE-1', 'CIS-ETAPE-2')), NULL), 'STRING'::remocra."TYPE_PARAMETRE"),
  (gen_random_uuid(), 'LISTE_TYPE_ORGA_GENDARMERIE', COALESCE((SELECT array_to_json(array_agg(type_organisme_code))::text FROM type_organisme WHERE type_organisme_code = 'GENDARMERIE'), NULL), 'STRING'::remocra."TYPE_PARAMETRE"),
  (gen_random_uuid(), 'LISTE_TYPE_ORGA_POLICE', COALESCE((SELECT array_to_json(array_agg(type_organisme_code))::text FROM type_organisme WHERE type_organisme_code = 'POLICE'), NULL), 'STRING'::remocra."TYPE_PARAMETRE");
