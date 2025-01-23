-- Ajout / Modification de la nature CONVENTIONNE
INSERT INTO remocra.nature_deci (nature_deci_id, nature_deci_actif, nature_deci_code, nature_deci_libelle, nature_deci_protected)
VALUES((SELECT gen_random_uuid()), true, 'CONVENTIONNE', 'Conventionné', TRUE)
ON CONFLICT (nature_deci_code) DO UPDATE
SET 
    nature_deci_actif = TRUE,
    nature_deci_libelle = 'Conventionné',
    nature_deci_protected = TRUE;


-- Update global pei_nature_deci_id vers la nouvelle nature 'Conventionné'
---- Pour les PEI de nature différente de 'PRIVE' et 'PUBLIC'
UPDATE remocra.pei
SET pei_nature_deci_id = (SELECT nature_deci_id FROM remocra.nature_deci WHERE nature_deci_code = 'CONVENTIONNE')
WHERE pei.pei_nature_deci_id NOT IN (SELECT nature_deci_id FROM remocra.nature_deci WHERE nature_deci_code in ('PRIVE','PUBLIC'));


-- Suppression des valeurs en trop
DELETE FROM remocra.nature_deci 
WHERE nature_deci_code NOT IN ('PRIVE','PUBLIC','CONVENTIONNE');
