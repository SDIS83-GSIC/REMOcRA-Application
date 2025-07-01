-- Rajoute un paramètre permettant de débrayer l'affichage en standalone de la fiche Résumé
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'PEI_FICHE_RESUME_STANDALONE', true, 'BOOLEAN');