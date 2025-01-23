INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'BANNIERE_CHEMIN', '/var/remocra/banniere_bspp_notitle.png', 'STRING'::remocra."TYPE_PARAMETRE"),
(gen_random_uuid(), 'LOGO_CHEMIN', '/var/remocra/logo-sdis.png', 'STRING'::remocra."TYPE_PARAMETRE")
ON CONFLICT DO NOTHING;
