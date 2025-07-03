-- Rajoute un paramètre permettant de débrayer l'affichage des types d'engins dans la fiche PEI
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'PEI_DISPLAY_TYPE_ENGIN', true, 'BOOLEAN');