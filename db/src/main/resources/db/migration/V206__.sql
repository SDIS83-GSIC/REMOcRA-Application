UPDATE remocra.rapport_personnalise_parametre SET rapport_personnalise_parametre_valeur_defaut='-1'
WHERE rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code='DSM_PAR_DEBIT');

UPDATE remocra.rapport_personnalise_parametre SET
    rapport_personnalise_parametre_valeur_defaut='tous',
    rapport_personnalise_parametre_source_sql='SELECT * FROM (
        SELECT ''tous'' AS id, ''Tous'' AS libelle, CAST(NULL AS TEXT) AS tricol
        UNION
        SELECT commune.commune_id::text as id, commune.commune_libelle::text as libelle, commune.commune_libelle AS tricol
        FROM remocra.commune
        JOIN remocra.zone_integration zi ON ST_CONTAINS(zi.zone_integration_geometrie, commune.commune_geometrie)
            AND zi.zone_integration_id = #ZONE_COMPETENCE_ID#
    ) AS united_options
    ORDER BY CASE WHEN id = ''tous'' THEN 0 ELSE 1 END,
             (SUBSTRING(tricol FROM ''([0-9]+)'') IS NULL),
             COALESCE(CAST(SUBSTRING(tricol FROM ''([0-9]+)'') AS INTEGER), 0),
             libelle;'
WHERE rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code in ('PEI_ANOMALIES', 'PEI_DIAMETRE_CANALISATION', 'PEI_GD', 'PEI_COMMUNE'))
AND rapport_personnalise_parametre_code='COMMUNE';

UPDATE remocra.rapport_personnalise_parametre SET
    rapport_personnalise_parametre_valeur_defaut='tous',
    rapport_personnalise_parametre_source_sql='select * from (
        select
            ''tous'' as id,
            ''Tous'' as libelle
        union
        select distinct nature_type_pei::text as id, nature_type_pei::text as libelle
        from remocra.nature
    ) t
    order by CASE WHEN id = ''tous'' THEN 0 ELSE 1 END, libelle;'
WHERE rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code in ('PEI_ANOMALIES', 'PEI_DIAMETRE_CANALISATION'))
AND rapport_personnalise_parametre_code='TYPE';

UPDATE remocra.rapport_personnalise_parametre SET
    rapport_personnalise_parametre_valeur_defaut='tous',
    rapport_personnalise_parametre_source_sql='select * from (
        select
            ''tous'' as id,
            ''Tous'' as libelle
        union
        select distinct domaine_id::text as id, domaine_libelle::text as libelle
        from remocra.domaine
    ) t
    order by CASE WHEN id = ''tous'' THEN 0 ELSE 1 END, libelle;'
WHERE rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code in ('PEI_ANOMALIES', 'PEI_DIAMETRE_CANALISATION', 'PEI_GD'))
AND rapport_personnalise_parametre_code='DOMAINE';

UPDATE remocra.rapport_personnalise_parametre SET
    rapport_personnalise_parametre_valeur_defaut='tous',
    rapport_personnalise_parametre_source_sql='select * from (
        select
            ''tous'' as id,
            ''Tous'' as libelle
        union
        select distinct diametre_id::text as id, diametre_libelle::text as libelle
        from remocra.diametre
    ) t
    order by CASE WHEN id = ''tous'' THEN 0 ELSE 1 END, libelle;'
WHERE rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code in ('PEI_ANOMALIES', 'PEI_DIAMETRE_CANALISATION'))
AND rapport_personnalise_parametre_code='DIAMETRE';

UPDATE remocra.rapport_personnalise_parametre SET
    rapport_personnalise_parametre_valeur_defaut='tous',
    rapport_personnalise_parametre_source_sql='select * from (
        select
            ''tous'' as id,
            ''Tous'' as libelle
        union
        select cast(true as text) as id, cast(''vrai'' as text) as libelle
        union
        select ''false'' as id, ''faux'' as libelle
    ) t
    order by CASE
                 WHEN id = ''tous'' THEN 0
                 WHEN id = ''true'' THEN 1
                 WHEN id = ''false'' THEN 2
                 ELSE 3
             END;'
WHERE rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code in ('PEI_ANOMALIES', 'PEI_DIAMETRE_CANALISATION', 'PEI_GD'))
AND rapport_personnalise_parametre_code in('RENFORCE', 'INVIOLABILITE');

UPDATE remocra.rapport_personnalise_parametre SET
    rapport_personnalise_parametre_valeur_defaut='tous',
    rapport_personnalise_parametre_source_sql='select * from (
        select
            ''tous'' as id,
            ''Tous'' as libelle
        union
        select nd.nature_deci_id::text as id, nd.nature_deci_libelle::text as libelle
        from remocra.nature_deci nd
    ) t
    order by CASE WHEN id = ''tous'' THEN 0 ELSE 1 END, libelle;'
WHERE rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code in ('PEI_DIAMETRE_CANALISATION', 'PEI_GD'))
AND rapport_personnalise_parametre_code='NATURE_DECI';

UPDATE remocra.rapport_personnalise_parametre SET
    rapport_personnalise_parametre_valeur_defaut='tous',
    rapport_personnalise_parametre_source_sql='select * from (
        select
            ''tous'' as id,
            ''Tous'' as libelle
        union
        select
            pg.enumlabel::text as id,
            pg.enumlabel::text as libelle
        FROM pg_enum pg
        JOIN pg_type ON pg.enumtypid = pg_type.oid
        WHERE pg_type.typname = ''DISPONIBILITE''
    ) t
    order by CASE WHEN id = ''tous'' THEN 0 ELSE 1 END, libelle;'
WHERE rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_COMMUNE' )
AND rapport_personnalise_parametre_code='DISPONIBILITE_CODE';

UPDATE remocra.rapport_personnalise_parametre SET
    rapport_personnalise_parametre_valeur_defaut='tous',
    rapport_personnalise_parametre_source_sql='with dernier_debit_pression as (
                                                   select distinct
                                                       v.debit as id
                                                   from remocra.v_pei_last_mesures v
                                                   left join remocra.l_tournee_pei ltp
                                                       on ltp.pei_id = v.pei_id
                                                   where debit is not null and ltp.tournee_id is null
                                               ),
                                               options as (
                                                   select id::text as id, id::text as libelle from dernier_debit_pression
                                                   union all
                                                   select ''tous'' as id, ''Tous'' as libelle
                                               )
                                               select *
                                               from options
                                               order by
                                                   case when id = ''tous'' then 0 else 1 end,
                                                   case when id = ''tous'' then null else id::numeric end;'
WHERE rapport_personnalise_parametre_rapport_personnalise_id in (
    SELECT rpp.rapport_personnalise_id
    FROM remocra.rapport_personnalise rpp
    WHERE rpp.rapport_personnalise_code in ('PEI_ANOMALIES', 'PEI_DIAMETRE_CANALISATION')
)
AND rapport_personnalise_parametre_code='DEBIT';

UPDATE remocra.rapport_personnalise SET
rapport_personnalise_source_sql='select
                                                p.pei_numero_complet as "Numéro",
                                                p.pei_type_pei as "Type",
                                                d.diametre_libelle as "Diametre",
                                                vcdp.visite_ctrl_debit_pression_debit as "Débit",
                                                domaine.domaine_libelle as "Domaine",
                                                deci.nature_deci_libelle as "DECI",
                                                c.commune_libelle as "Commune",
                                                voie.voie_libelle as "Adresse",
                                                p.pei_observation as "Observations",
                                                pibi.pibi_debit_renforce as "Débit renforcé",
                                                pibi.pibi_dispositif_inviolabilite as "Dispositif d''inviolabilité"
                                            from
                                                remocra.pei p
                                            inner join remocra.visite v on
                                                v.visite_pei_id = p.pei_id
                                            inner join remocra.visite_ctrl_debit_pression vcdp on
                                                vcdp.visite_ctrl_debit_pression_visite_id = v.visite_id
                                                and (''DEBIT'' = ''tous''
                                                    or vcdp.visite_ctrl_debit_pression_debit::text = ''DEBIT'')
                                            inner join remocra.domaine domaine on
                                                domaine.domaine_id = p.pei_domaine_id
                                                and (''DOMAINE'' = ''tous''
                                                    or domaine.domaine_id::text = ''DOMAINE'')
                                            left join remocra.pibi pibi on
                                                pibi.pibi_id = p.pei_id
                                            left join remocra.diametre d on
                                                d.diametre_id = pibi.pibi_diametre_id
                                            left join remocra.nature_deci deci on
                                                deci.nature_deci_id = p.pei_nature_deci_id
                                            left join remocra.commune c on
                                                c.commune_id = p.pei_commune_id
                                            left join remocra.voie voie on
                                                voie.voie_id = p.pei_voie_id
                                            where
                                                (''COMMUNE'' = ''tous''
                                                    or p.pei_commune_id::text = ''COMMUNE'')
                                                and (''TYPE'' = ''tous''
                                                    or p.pei_type_pei::text = ''TYPE'')
                                                and (
                                                    ''DIAMETRE'' = ''tous''
                                                    or (pibi.pibi_diametre_id::text = ''DIAMETRE'')
                                                )
                                                and (''RENFORCE'' = ''tous''
                                                    or pibi.pibi_debit_renforce::text = ''RENFORCE'')
                                                and (''INVIOLABILITE'' = ''tous''
                                                    or pibi.pibi_dispositif_inviolabilite::text = ''INVIOLABILITE'');'
WHERE rapport_personnalise_code = 'PEI_ANOMALIES';

UPDATE remocra.rapport_personnalise SET
rapport_personnalise_source_sql='select
                                                p.pei_type_pei as "Type",
                                                d.diametre_libelle as "Diametre",
                                                vcdp.visite_ctrl_debit_pression_debit as "Débit",
                                                domaine.domaine_libelle as "Domaine",
                                                deci.nature_deci_libelle as "Nature DECI",
                                                c.commune_libelle as "Commune",
                                                voie.voie_libelle as "Voie",
                                                p.pei_observation as "Observations",
                                                pibi.pibi_diametre_canalisation as "Diamètre canalisation",
                                                pibi.pibi_debit_renforce as "Débit renforcé",
                                                pibi.pibi_dispositif_inviolabilite as "Dispositif d''inviolabilité"
                                            from
                                                remocra.pei p
                                            inner join remocra.visite v on
                                                v.visite_pei_id = p.pei_id
                                            inner join remocra.visite_ctrl_debit_pression vcdp on
                                                vcdp.visite_ctrl_debit_pression_visite_id = v.visite_id
                                                and (''DEBIT'' = ''tous''
                                                    or vcdp.visite_ctrl_debit_pression_debit::text = ''DEBIT'')
                                            inner join remocra.domaine domaine on
                                                domaine.domaine_id = p.pei_domaine_id
                                                and (''DOMAINE'' = ''tous''
                                                    or domaine.domaine_id::text = ''DOMAINE'')
                                            left join remocra.pibi pibi on
                                                pibi.pibi_id = p.pei_id
                                            left join remocra.diametre d on
                                                d.diametre_id = pibi.pibi_diametre_id
                                            left join remocra.nature_deci deci on
                                                deci.nature_deci_id = p.pei_nature_deci_id
                                            left join remocra.commune c on
                                                c.commune_id = p.pei_commune_id
                                            left join remocra.voie voie on
                                                voie.voie_id = p.pei_voie_id
                                            where
                                                (''COMMUNE'' = ''tous''
                                                    or p.pei_commune_id::text = ''COMMUNE'')
                                                and (''TYPE'' = ''tous''
                                                    or p.pei_type_pei::text = ''TYPE'')
                                                and (
                                                    ''DIAMETRE'' = ''tous''
                                                    or (pibi.pibi_diametre_id::text = ''DIAMETRE'')
                                                )
                                                and (''RENFORCE'' = ''tous''
                                                    or pibi.pibi_debit_renforce::text = ''RENFORCE'')
                                                and (''INVIOLABILITE'' = ''tous''
                                                    or pibi.pibi_dispositif_inviolabilite::text = ''INVIOLABILITE'')
                                                and (''NATURE_DECI'' = ''tous''
                                                    or deci.nature_deci_id::text = ''NATURE_DECI'')
                                                and (''ADRESSE'' = ''null''
                                                    or voie.voie_libelle ilike ''%'' || ''ADRESSE'' || ''%'');'
WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION';

UPDATE remocra.rapport_personnalise SET
rapport_personnalise_source_sql='with observations as (
                                            select
                                                distinct on
                                                (visite_pei_id)
                                                visite_pei_id,
                                                visite_observation
                                            from
                                                remocra.visite
                                            order by
                                                visite_pei_id,
                                                visite_date desc
                                            )
                                            select
                                                p.pei_numero_complet as "Numéro",
                                                n.nature_libelle as "Type",
                                                d.diametre_libelle as "Diametre",
                                                dsm.debit_simultane_mesure_debit_mesure as "Débit",
                                                domaine.domaine_libelle as "Domaine",
                                                deci.nature_deci_libelle as "Deci",
                                                c.commune_libelle as "Commune",
                                                voie.voie_libelle as "Adresse",
                                                o.visite_observation as "Observation",
                                                pibi.pibi_dispositif_inviolabilite as "Dispositif d''inviolabilité"
                                            from
                                                remocra.pei p
                                            left join remocra.commune c on
                                                c.commune_id = p.pei_commune_id
                                            left join remocra.pibi pibi on
                                                pibi.pibi_id = p.pei_id
                                            join remocra.l_debit_simultane_mesure_pei l_dsmp on
                                                l_dsmp.pei_id = p.pei_id
                                            left join remocra.debit_simultane_mesure dsm on
                                                dsm.debit_simultane_id = l_dsmp.debit_simultane_mesure_id
                                            left join remocra.diametre d on
                                                d.diametre_id = pibi.pibi_diametre_id
                                            left join remocra.voie voie on
                                                voie.voie_id = p.pei_voie_id
                                            left join remocra.nature_deci deci on
                                                deci.nature_deci_id = p.pei_nature_deci_id
                                            left join remocra.domaine domaine on
                                                domaine.domaine_id = p.pei_domaine_id
                                            left join remocra.v_pei_visite_date v_psd on
                                                v_psd.pei_id = p.pei_id
                                            left join remocra.nature n on
                                                n.nature_id = p.pei_nature_id
                                            left join observations o on
                                                o.visite_pei_id = p.pei_id
                                            where
                                                (''COMMUNE'' = ''tous''
                                                    or p.pei_commune_id::text = ''COMMUNE'')
                                                and (''INVIOLABILITE'' = ''tous''
                                                    or pibi.pibi_dispositif_inviolabilite::text = ''INVIOLABILITE'')
                                                and (''NATURE_DECI'' = ''tous''
                                                    or deci.nature_deci_id::text = ''NATURE_DECI'')
                                                and (''ADRESSE'' is null
                                                    or voie.voie_libelle like ''%'' || ''ADRESSE'' || ''%'')
                                                and (n.nature_code = ''BI''
                                                    and d.diametre_code = ''DIAM100''
                                                    and pibi.pibi_jumele_id is not null)
                                                or (n.nature_code = ''PI''
                                                    and d.diametre_code = ''DIAM150'')
                                            order by
                                                c.commune_libelle,
                                                p.pei_numero_complet;'
WHERE rapport_personnalise_code = 'PEI_GD';

UPDATE remocra.rapport_personnalise SET
rapport_personnalise_source_sql='select
                                                p.pei_numero_complet as "Numéro",
                                                            v.voie_libelle as "Voie",
                                                            v2.voie_libelle as "Carrefour",
                                                            ld.lieu_dit_libelle as "Lieu dit",
                                                            commune.commune_libelle as "Commune",
                                                            St_X(p.pei_geometrie)::integer as "X",
                                                            St_Y(p.pei_geometrie)::integer as "Y",
                                                            n.nature_libelle as "Nature",
                                                            d.domaine_libelle as "Domaine",
                                                            case
                                                    p.pei_disponibilite_terrestre
                                                            when ''DISPONIBLE'' then ''Disponible''
                                                    when ''INDISPONIBLE'' then ''Indisponible''
                                                    else ''Non conforme''
                                                end as "Disponibilité",
                                                            to_char(v_psd.last_ctp ,
                                                ''dd/mm/yyyy'') as "Contrôle",
                                                            to_char(v_psd.last_reco_init ,
                                                ''dd/mm/yyyy'') as "Reconnaissance"
                                            from
                                                remocra.pei p
                                            join remocra.commune commune on
                                                commune.commune_id = p.pei_commune_id
                                            left join remocra.voie v on
                                                v.voie_id = p.pei_voie_id
                                            left join remocra.voie v2 on
                                                v2.voie_id = p.pei_croisement_id
                                            left join remocra.lieu_dit ld on
                                                ld.lieu_dit_id = p.pei_lieu_dit_id
                                            left join remocra.nature n on
                                                n.nature_id = p.pei_nature_id
                                            left join remocra.domaine d on
                                                d.domaine_id = p.pei_domaine_id
                                            left join remocra.v_pei_visite_date v_psd on
                                                v_psd.pei_id = p.pei_id
                                            where
                                                (''COMMUNE'' = ''tous''
                                                    or p.pei_commune_id::text = ''COMMUNE'')
                                                and (''DISPONIBILITE_CODE'' = ''tous''
                                                    or p.pei_disponibilite_terrestre::text = ''DISPONIBILITE_CODE'');'
WHERE rapport_personnalise_code = 'PEI_COMMUNE';
