INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type)
values
    (gen_random_uuid(), 'PEI_HIGHLIGHT_COULEUR', '#d983e2', 'STRING'),
    (gen_random_uuid(), 'PEI_HIGHLIGHT_RAYON', '16', 'INTEGER'),
    (gen_random_uuid(), 'PEI_HIGHLIGHT_LARGEUR', '4', 'INTEGER')
;