-- Ajoute un paramètre permettant de définir les valeurs par défaut d'organisme et de profil utilisateur à affecter à un utilisateur lors de sa création
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'ORGANISME_DEFAUT', null, 'STRING'::remocra."TYPE_PARAMETRE")
;

INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'PROFIL_UTILISATEUR_DEFAUT', null, 'STRING'::remocra."TYPE_PARAMETRE")
;
