-- Ajoute un paramètre permettant de définir quels types de visites peuvent donner lieu à un Contrôle Débit Pression (CDP)
-- La valeur donnée correspond à ce qui était défini en V2
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'TYPE_VISITE_CDP', '["RECEPTION", "CTP"]', 'STRING'::remocra."TYPE_PARAMETRE")
ON CONFLICT DO NOTHING;
