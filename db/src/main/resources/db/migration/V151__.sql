INSERT INTO remocra.nature
    (nature_id, nature_actif, nature_code, nature_libelle, nature_type_pei, nature_protected)
VALUES (gen_random_uuid(), true, 'PEN', 'PEN', 'PENA', true),
       (gen_random_uuid(), true, 'PEA', 'PEA', 'PENA', true),
       (gen_random_uuid(), true, 'PI', 'PI', 'PIBI', true),
       (gen_random_uuid(), true, 'BI', 'BI', 'PIBI', true)
ON CONFLICT (nature_code)
DO UPDATE SET nature_protected = EXCLUDED.nature_protected;
