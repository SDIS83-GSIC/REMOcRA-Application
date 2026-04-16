UPDATE remocra.rapport_personnalise_parametre set rapport_personnalise_parametre_source_sql=
'select * from (
         select
             ''tous'' as id,
             ''Tous'' as libelle
         union
         select
             pg.enumlabel::text as id,
             case
               when pg.enumlabel = ''DISPONIBLE'' then ''Disponible''
               when pg.enumlabel = ''INDISPONIBLE'' then ''Indisponible''
               when pg.enumlabel = ''NON_CONFORME'' then (select parametre_valeur from remocra.parametre where parametre_code = ''PEI_LIBELLE_NON_CONFORME'')
               else pg.enumlabel::text
             end as libelle
         FROM pg_enum pg
         JOIN pg_type ON pg.enumtypid = pg_type.oid
         WHERE pg_type.typname = ''DISPONIBILITE''
     ) t
     order by CASE WHEN id = ''tous'' THEN 0 ELSE 1 END, libelle;'
where rapport_personnalise_parametre_code ='DISPONIBILITE_CODE';

UPDATE remocra.rapport_personnalise set rapport_personnalise_source_sql =
'select
                    p.pei_numero_complet as "Numéro",
                    n.nature_libelle as "Nature",
                    St_X(p.pei_geometrie)::integer as "X",
                    St_Y(p.pei_geometrie)::integer as "Y",
                    c.commune_libelle as "Commune",
                    ld.lieu_dit_libelle as "Lieu dit",
                    coalesce(v.voie_libelle, p.pei_voie_texte) as "Voie",
                    croisement.voie_libelle as "Carrefour",
                    case
                        p.pei_disponibilite_terrestre
                        when ''DISPONIBLE'' then ''Disponible''
                        when ''INDISPONIBLE'' then ''Indisponible''
                        else param.parametre_valeur
                    end as "Disponibilité"
                from
                    remocra.pei p
                join remocra.commune c on
                    c.commune_id = p.pei_commune_id
                join remocra.nature n on
                    n.nature_id = p.pei_nature_id
                left join remocra.lieu_dit ld on
                    ld.lieu_dit_id = p.pei_lieu_dit_id
                left join remocra.voie v on
                    v.voie_id = p.pei_voie_id
                left join remocra.voie croisement on
                    croisement.voie_id = p.pei_croisement_id
                LEFT JOIN (
                     SELECT parametre_valeur
                     FROM remocra.parametre
                     WHERE parametre_code = ''PEI_LIBELLE_NON_CONFORME''
                ) param ON true
                order by
                    CASE WHEN commune_libelle ~ ''^[0-9]+'' THEN 0 ELSE 1 END,
                    COALESCE(NULLIF(SUBSTRING(commune_libelle, ''^(\\d+)''), ''''), ''0'')::integer,
                    commune_libelle,
                    p.pei_numero_complet;'
where rapport_personnalise_code = 'LISTE_PEI';

UPDATE remocra.rapport_personnalise set rapport_personnalise_source_sql =
'select
    p.pei_numero_complet as "Numéro",
    coalesce(v.voie_libelle, p.pei_voie_texte) as "Voie",
    v2.voie_libelle as "Carrefour",
    ld.lieu_dit_libelle as "Lieu dit",
    commune.commune_libelle as "Commune",
    St_X(p.pei_geometrie)::integer as "X",
    St_Y(p.pei_geometrie)::integer as "Y",
    n.nature_libelle as "Nature",
    d.domaine_libelle as "Domaine",
    na.nature_deci_libelle as "Nature DECI",
    case
        p.pei_disponibilite_terrestre
            when ''DISPONIBLE'' then ''Disponible''
            when ''INDISPONIBLE'' then ''Indisponible''
            else param.parametre_valeur
    end as "Disponibilité",
    to_char(v_psd.last_ctp , ''dd/mm/yyyy'') as "Contrôle",
    to_char(v_psd.last_reco_init , ''dd/mm/yyyy'') as "Reconnaissance"
from
    remocra.pei p
join remocra.commune commune on
    commune.commune_id = p.pei_commune_id
left join remocra.voie v on
    v.voie_id = p.pei_voie_id
join remocra.nature_deci na on
    na.nature_deci_id = p.pei_nature_deci_id
left join remocra.voie v2 on
    v2.voie_id = p.pei_croisement_id
left join remocra.lieu_dit ld on
    ld.lieu_dit_id = p.pei_lieu_dit_id
join remocra.nature n on
    n.nature_id = p.pei_nature_id
join remocra.domaine d on
    d.domaine_id = p.pei_domaine_id
left join remocra.v_pei_visite_date v_psd on
    v_psd.pei_id = p.pei_id
LEFT JOIN (
    SELECT parametre_valeur
    FROM remocra.parametre
    WHERE parametre_code = ''PEI_LIBELLE_NON_CONFORME''
) param ON true
where
    (''COMMUNE'' = ''tous''
        or p.pei_commune_id::text = ''COMMUNE'')
    and (''#NATURE_DECI#'' = ''tous''
        or p.pei_nature_deci_id::text = ''#NATURE_DECI#'')
    and (''DISPONIBILITE_CODE'' = ''tous''
        or p.pei_disponibilite_terrestre::text = ''DISPONIBILITE_CODE'');'
where rapport_personnalise_code = 'PEI_COMMUNE';