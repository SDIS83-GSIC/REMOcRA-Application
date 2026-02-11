insert into parametre(
    parametre_id,
    parametre_code,
    parametre_valeur,
    parametre_type
) values (
             gen_random_uuid(),
             'RECHERCHE_BAN',
             'true',
             'BOOLEAN'
         );

insert into parametre(
    parametre_id,
    parametre_code,
    parametre_valeur,
    parametre_type
) values (
             gen_random_uuid(),
             'LISTE_TOPONYMIE_CODE',
             '[]',
             'STRING'
         );
