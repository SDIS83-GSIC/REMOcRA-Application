INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type)
values
    (gen_random_uuid(), 'PEI_SELECTION_COULEUR', '#ff00f2b3', 'STRING'),
    (gen_random_uuid(), 'PEI_SELECTION_RAYON', '16', 'INTEGER'),
    (gen_random_uuid(), 'PEI_SELECTION_LARGEUR', '4', 'INTEGER')
;