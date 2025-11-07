--Ajout nature paramètres natures DECI ICPE

INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES (gen_random_uuid(), 'PEI_RENOUVELLEMENT_CTRL_ICPE', 1095, 'INTEGER'),
(gen_random_uuid(), 'PEI_RENOUVELLEMENT_CTRL_ICPE_CONVENTIONNE', 1095, 'INTEGER'),
(gen_random_uuid(), 'PEI_RENOUVELLEMENT_RECO_ICPE', 1095, 'INTEGER'),
(gen_random_uuid(), 'PEI_RENOUVELLEMENT_RECO_ICPE_CONVENTIONNE', 1095, 'INTEGER');
