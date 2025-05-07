-- Suppression des éléments portant les codes des éléments "Protected"
---- Suppression des liens Rapport-Profil
DELETE FROM remocra.l_rapport_personnalise_profil_droit lrppd
WHERE lrppd.rapport_personnalise_id IN (
    SELECT rp.rapport_personnalise_id
    FROM remocra.rapport_personnalise rp
    WHERE rp.rapport_personnalise_code IN (
        'DSM_PAR_DEBIT', 'LISTE_PEI', 'LISTE_RI',
        'NB_PEI_INDISPO_COMMUNE', 'PEI_ADRESSE_INCERTAINE', 'PEI_ANOMALIES',
        'PEI_COMMUNE', 'PEI_DIAMETRE_CANALISATION', 'PEI_GD'
    )
);
---- Suppression des paramètres
DELETE FROM remocra.rapport_personnalise_parametre rpp
WHERE rpp.rapport_personnalise_parametre_rapport_personnalise_id IN (
	SELECT rp.rapport_personnalise_id
	FROM remocra.rapport_personnalise rp
	WHERE rp.rapport_personnalise_code IN (
		'DSM_PAR_DEBIT', 'LISTE_PEI', 'LISTE_RI',
		'NB_PEI_INDISPO_COMMUNE', 'PEI_ADRESSE_INCERTAINE', 'PEI_ANOMALIES',
		'PEI_COMMUNE', 'PEI_DIAMETRE_CANALISATION', 'PEI_GD'
	)
);
---- Suppression des rapports
DELETE FROM remocra.rapport_personnalise rp
WHERE rp.rapport_personnalise_code IN (
	'DSM_PAR_DEBIT', 'LISTE_PEI', 'LISTE_RI',
	'NB_PEI_INDISPO_COMMUNE', 'PEI_ADRESSE_INCERTAINE', 'PEI_ANOMALIES',
	'PEI_COMMUNE', 'PEI_DIAMETRE_CANALISATION', 'PEI_GD'
);

-- Réintégration des Modèles de rapports personnalisés et leurs paramètres
---- LISTE_PEI
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'LISTE_PEI',
    'Liste des PEI',
    true,
    NULL,
    'Liste les PEI sur l''ensemble du département',
    'select
                p.pei_numero_complet as "Numéro",
                n.nature_libelle as "Nature",
                St_X(p.pei_geometrie)::integer as "X",
                St_Y(p.pei_geometrie)::integer as "Y",
                c.commune_libelle as "Commune",
                ld.lieu_dit_libelle as "Lieu dit",
                v.voie_libelle as "Voie",
                croisement.voie_libelle as "Carrefour",
                p.pei_disponibilite_terrestre as "Disponible"
            from
                remocra.pei p
            join remocra.commune c on
                c.commune_id = p.pei_commune_id
            left join remocra.nature n on
                n.nature_id = p.pei_nature_id
            left join remocra.lieu_dit ld on
                ld.lieu_dit_id = p.pei_lieu_dit_id
            left join remocra.voie v on
                v.voie_id = p.pei_voie_id
            left join remocra.voie croisement on
                croisement.voie_id = p.pei_croisement_id
            order by
                c.commune_libelle,
                p.pei_numero_complet;',
    'DECI'::remocra.type_module)
;

---- LISTE_RI
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'LISTE_RI',
    'Noms et capacités des RI',
    true,
    NULL,
    'Liste les RI par communes avec le nombre de PEI rattachés',
    'select
                c.commune_libelle as "Nom de la commune",
                pibi.pibi_numero_scp as "Nom et capacité du réservoir",
                count(p.pei_id) as "Nombre de PEI alimentés par le RI (sur la commune)"
            from
                remocra.pei p
            join remocra.commune c on
                c.commune_id = p.pei_commune_id
            join remocra.pibi pibi on
                pibi.pibi_id = p.pei_id
            group by
                c.commune_libelle,
                pibi.pibi_numero_scp
            order by
                c.commune_libelle;',
    'DECI'::remocra.type_module)
;

---- PEI_ANOMALIES
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'PEI_ANOMALIES',
    'PEI avec anomalies',
    true,
    NULL,
    'Liste les PEI avec des anomalies',
    'select
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
                and (''DEBIT'' = ''null''
                    or vcdp.visite_ctrl_debit_pression_debit::text = ''DEBIT'')
            inner join remocra.domaine domaine on
                domaine.domaine_id = p.pei_domaine_id
                and (''DOMAINE'' = ''null''
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
                (''COMMUNE'' = ''null''
                    or p.pei_commune_id::text = ''COMMUNE'')
                and (''TYPE'' = ''null''
                    or p.pei_type_pei::text = ''TYPE'')
                and (
                    ''DIAMETRE'' = ''null''
                    or (pibi.pibi_diametre_id::text = ''DIAMETRE'')
                )
                and (''RENFORCE'' = ''null''
                    or pibi.pibi_debit_renforce::text = ''RENFORCE'')
                and (''INVIOLABILITE'' = ''null''
                    or pibi.pibi_debit_renforce::text = ''INVIOLABILITE'');',
    'DECI'::remocra.type_module)
;
INSERT INTO remocra.rapport_personnalise_parametre (rapport_personnalise_parametre_id, rapport_personnalise_parametre_rapport_personnalise_id, rapport_personnalise_parametre_code, rapport_personnalise_parametre_libelle, rapport_personnalise_parametre_source_sql, rapport_personnalise_parametre_description, rapport_personnalise_parametre_source_sql_id, rapport_personnalise_parametre_source_sql_libelle, rapport_personnalise_parametre_valeur_defaut, rapport_personnalise_parametre_is_required, rapport_personnalise_parametre_type, rapport_personnalise_parametre_ordre)
VALUES
(-- PEI_ANOMALIES | COMMUNE
    gen_random_uuid(),
    (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_ANOMALIES'),
    'COMMUNE',
    'Commune',
    'SELECT * FROM (
		SELECT
			'''' AS id,
	        ''Tous'' AS libelle,
	        CAST(NULL AS TEXT) AS tricol
		UNION
	    SELECT
	        commune.commune_id::text AS id,
	        commune.commune_libelle::text AS libelle,
	        commune.commune_libelle AS tricol
	    FROM remocra.commune
	        JOIN remocra.zone_integration zi ON ST_CONTAINS(zi.zone_integration_geometrie, commune.commune_geometrie)
	            AND zi.zone_integration_id = #ZONE_COMPETENCE_ID#
	) AS united_options
	ORDER BY
		tricol IS NOT NULL,
	    CAST(SUBSTRING(tricol FROM ''([0-9]+)'') AS INTEGER),
	    libelle;',
    '',
    'c.commune_id::text',
    'c.commune_libelle::text',
    NULL,
    true,
    'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
    0
),
(-- PEI_ANOMALIES | DEBIT
    gen_random_uuid(),
    (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_ANOMALIES'),
    'DEBIT',
    'Débit',
    'with dernier_debit_pression as (
        select distinct on (v.visite_pei_id)
            vcdp.visite_ctrl_debit_pression_debit as id
        from remocra.visite_ctrl_debit_pression vcdp
        join remocra.visite v
            on vcdp.visite_ctrl_debit_pression_visite_id = v.visite_id
    ),
    options as (
        select  id::text as id, id::text as libelle  from dernier_debit_pression
        union all
        select '''' as id, ''tous'' as libelle
    )
    select distinct * from options;',
    '',
    'id::text',
    'id::text',
    NULL,
    true,
    'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
    1
),
(-- PEI_ANOMALIES | TYPE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_ANOMALIES'),
	'TYPE',
	'Type',
	'select
        '' '' as id,
        '' Tous'' as libelle
    union
    select
        distinct nature_type_pei::text as id,
        nature_type_pei::text as libelle
    from
        remocra.nature
    order by
        id nulls first;',
	'',
	'nature_type_pei::text',
	'nature_type_pei::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	2
),
(-- PEI_ANOMALIES | DOMAINE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_ANOMALIES'),
	'DOMAINE',
	'Domaine',
	'select
        '' '' as id,
        '' Tous'' as libelle
    union
    select
        distinct domaine_id::text as id,
        domaine_libelle::text as libelle
    from
        remocra.domaine
    order by
        id nulls first;',
	'',
	'domaine_id::text',
	'domaine_libelle::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	3
),
(-- PEI_ANOMALIES | DIAMETRE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_ANOMALIES'),
	'DIAMETRE',
	'Diametre',
	'select
        '''' as id,
        ''Tous'' as libelle
    union
    select
        distinct diametre_id::text as id,
        diametre_libelle::text as libelle
    from
        remocra.diametre
    order by
        id nulls first;',
	'',
	'diametre_id::text',
	'diametre_libelle::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	4
),
(-- PEI_ANOMALIES | RENFORCE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_ANOMALIES'),
	'RENFORCE',
	'Dispositif renforcé',
	'select
		'''' as id,
		''tous'' as libelle
		union
		select
			cast(true as text) as id,
			cast(''vrai'' as text) as libelle
		union
		select
			''false'' as id,
			''faux'' as libelle
		order by
			id nulls first;',
	'',
	'cast(true as text)',
	'cast(''vrai'' as text)',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	5
),
(-- PEI_ANOMALIES | INVIOLABILITE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_ANOMALIES'),
	'INVIOLABILITE',
	'Inviolabilité',
	'select
		'''' as id,
		''tous'' as libelle
		union
		select
			cast(true as text) as id,
			cast(''vrai'' as text) as libelle
		union
		select
			''false'' as id,
			''faux'' as libelle
		order by
			id nulls first;',
	'',
	'cast(true as text)',
	'cast(''vrai'' as text)',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	6
);

---- PEI_COMMUNE
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'PEI_COMMUNE',
    'PEI sur une commune',
    true,
    NULL,
    'Liste les PEI associés à une commune',
    'select
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
                (''COMMUNE'' = ''null''
                    or p.pei_commune_id::text = ''COMMUNE'')
                and (''DISPONIBILITE_CODE'' = ''null''
                    or p.pei_disponibilite_terrestre::text = ''DISPONIBILITE_CODE'');',
    'DECI'::remocra.type_module)
;
INSERT INTO remocra.rapport_personnalise_parametre (rapport_personnalise_parametre_id, rapport_personnalise_parametre_rapport_personnalise_id, rapport_personnalise_parametre_code, rapport_personnalise_parametre_libelle, rapport_personnalise_parametre_source_sql, rapport_personnalise_parametre_description, rapport_personnalise_parametre_source_sql_id, rapport_personnalise_parametre_source_sql_libelle, rapport_personnalise_parametre_valeur_defaut, rapport_personnalise_parametre_is_required, rapport_personnalise_parametre_type, rapport_personnalise_parametre_ordre)
VALUES
(-- PEI_COMMUNE | DISPONIBILITE_CODE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_COMMUNE'),
	'DISPONIBILITE_CODE',
	'Disponibilité',
	'select
        '''' as id,
        ''Tous'' as libelle
    union
    select
    pg.enumlabel::text as id, pg.enumlabel::text as libelle FROM pg_enum pg
    JOIN pg_type ON pg.enumtypid = pg_type.oid
    WHERE pg_type.typname = ''DISPONIBILITE'';',
	'',
	'pg.enumlabel::text',
	'pg.enumlabel::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	1
),
(-- PEI_COMMUNE | COMMUNE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_COMMUNE'),
	'COMMUNE',
	'Commune',
	'SELECT * FROM (
		SELECT
			'''' AS id,
	        ''Tous'' AS libelle,
	        CAST(NULL AS TEXT) AS tricol
		UNION
	    SELECT
	        commune.commune_id::text AS id,
	        commune.commune_libelle::text AS libelle,
	        commune.commune_libelle AS tricol
	    FROM remocra.commune
	        JOIN remocra.zone_integration zi ON ST_CONTAINS(zi.zone_integration_geometrie, commune.commune_geometrie)
	            AND zi.zone_integration_id = #ZONE_COMPETENCE_ID#
	) AS united_options
	ORDER BY
		tricol IS NOT NULL,
	    CAST(SUBSTRING(tricol FROM ''([0-9]+)'') AS INTEGER),
	    libelle;',
	'',
	'commune.commune_id::text',
	'commune.commune_libelle::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	1
);

---- PEI_DIAMETRE_CANALISATION
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'PEI_DIAMETRE_CANALISATION',
    'PEI avec diametre canalisation',
    true,
    NULL,
    'Liste les PEI avec leur diamètre de canalisation',
    'select
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
                and (''DEBIT'' = ''null''
                    or vcdp.visite_ctrl_debit_pression_debit::text = ''DEBIT'')
            inner join remocra.domaine domaine on
                domaine.domaine_id = p.pei_domaine_id
                and (''DOMAINE'' = ''null''
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
                (''COMMUNE'' = ''null''
                    or p.pei_commune_id::text = ''COMMUNE'')
                and (''TYPE'' = ''null''
                    or p.pei_type_pei::text = ''TYPE'')
                and (
                    ''DIAMETRE'' = ''null''
                    or (pibi.pibi_diametre_id::text = ''DIAMETRE'')
                )
                and (''RENFORCE'' = ''null''
                    or pibi.pibi_debit_renforce::text = ''RENFORCE'')
                and (''INVIOLABILITE'' = ''null''
                    or pibi.pibi_debit_renforce::text = ''INVIOLABILITE'')
                and (''NATURE_DECI'' = ''null''
                    or deci.nature_deci_id::text = ''NATURE_DECI'')
                and (''ADRESSE'' = ''null''
                    or voie.voie_libelle like ''% ADRESSE %'');',
    'DECI'::remocra.type_module)
;
INSERT INTO remocra.rapport_personnalise_parametre (rapport_personnalise_parametre_id, rapport_personnalise_parametre_rapport_personnalise_id, rapport_personnalise_parametre_code, rapport_personnalise_parametre_libelle, rapport_personnalise_parametre_source_sql, rapport_personnalise_parametre_description, rapport_personnalise_parametre_source_sql_id, rapport_personnalise_parametre_source_sql_libelle, rapport_personnalise_parametre_valeur_defaut, rapport_personnalise_parametre_is_required, rapport_personnalise_parametre_type, rapport_personnalise_parametre_ordre)
VALUES
(-- PEI_DIAMETRE_CANALISATION | COMMUNE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'),
	'COMMUNE',
	'Commune',
	'SELECT * FROM (
	SELECT
		'''' AS id,
        ''Tous'' AS libelle,
        CAST(NULL AS TEXT) AS tricol
	UNION
    SELECT
        commune.commune_id::text AS id,
        commune.commune_libelle::text AS libelle,
        commune.commune_libelle AS tricol
    FROM remocra.commune
        JOIN remocra.zone_integration zi ON ST_CONTAINS(zi.zone_integration_geometrie, commune.commune_geometrie)
            AND zi.zone_integration_id = #ZONE_COMPETENCE_ID#
	) AS united_options
	ORDER BY
		tricol IS NOT NULL,
	    CAST(SUBSTRING(tricol FROM ''([0-9]+)'') AS INTEGER),
	    libelle;',
	'',
	' commune.commune_id::text',
	'commune.commune_libelle::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	0
),
(-- PEI_DIAMETRE_CANALISATION | DEBIT
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'),
	'DEBIT',
	'Débit',
	'with dernier_debit_pression as (
	        select distinct on (v.visite_pei_id)
	            vcdp.visite_ctrl_debit_pression_debit as id
	        from remocra.visite_ctrl_debit_pression vcdp
	        join remocra.visite v
	            on vcdp.visite_ctrl_debit_pression_visite_id = v.visite_id
	    ),
	    options as (
	        select  id::text as id, id::text as libelle  from dernier_debit_pression
	        union all
	        select '''' as id, ''tous'' as libelle
	    )
	    select distinct * from options;',
	'',
	'id::text',
	'id::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	1
),
(-- PEI_DIAMETRE_CANALISATION | TYPE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'),
	'TYPE',
	'Type',
	'select
	        '' '' as id,
	        '' Tous'' as libelle
	    union
	    select
	        distinct nature_type_pei::text as id,
	        nature_type_pei::text as libelle
	    from
	        remocra.nature
	    order by
	        id nulls first;',
	'',
	'nature_type_pei::text',
	'nature_type_pei::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	2
),
(-- PEI_DIAMETRE_CANALISATION | DOMAINE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'),
	'DOMAINE',
	'Domaine',
	'select
	        '' '' as id,
	        '' Tous'' as libelle
	    union
	    select
	        distinct domaine_id::text as id,
	        domaine_libelle::text as libelle
	    from
	        remocra.domaine
	    order by
	        id nulls first;',
	'',
	'domaine_id::text',
	'domaine_libelle::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	3
),
(-- PEI_DIAMETRE_CANALISATION | DIAMETRE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'),
	'DIAMETRE',
	'Diametre',
	'select
	        '''' as id,
	        ''Tous'' as libelle
	    union
	    select
	        distinct diametre_id::text as id,
	        diametre_libelle::text as libelle
	    from
	        remocra.diametre
	    order by
	        id nulls first;',
	'',
	'diametre_id::text',
	'diametre_libelle::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	4
),
(-- PEI_DIAMETRE_CANALISATION | INVIOLABILITE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'),
	'INVIOLABILITE',
	'Inviolabilité',
	'select
	'''' as id,
	''tous'' as libelle
	union
	select
		cast(true as text) as id,
		cast(''vrai'' as text) as libelle
	union
	select
		''false'' as id,
		''faux'' as libelle
	order by
		id nulls first;',
	'',
	'cast(true as text)',
	'cast(''vrai'' as text)',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	5
),
(-- PEI_DIAMETRE_CANALISATION | RENFORCE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'),
	'RENFORCE',
	'Dispositif renforcé',
	'select
		'''' as id,
		''tous'' as libelle
	union
	select
		cast(true as text) as id,
		cast(''vrai'' as text) as libelle
	union
	select
		''false'' as id,
		''faux'' as libelle
	order by
		id nulls first;',
	'',
	'cast(true as text)',
	'cast(''vrai'' as text)',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	6
),
(-- PEI_DIAMETRE_CANALISATION | NATURE_DECI
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'),
	'NATURE_DECI',
	'Nature DECI',
	'select
        '''' as id,
        ''Tous'' as libelle
    union
    select
        nd.nature_deci_id::text as id,
        nd.nature_deci_libelle::text as libelle
    from
        remocra.nature_deci nd
    order by
        id nulls first;',
	'',
	'nd.nature_deci_id::text',
	'nd.nature_deci_libelle::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	7
),
(-- PEI_DIAMETRE_CANALISATION | ADRESSE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'),
	'ADRESSE',
	'Adresse',
	NULL,
	'',
	NULL,
	NULL,
	NULL,
	true,
	'TEXT_INPUT'::remocra.type_parametre_rapport_courrier,
	8
);

---- PEI_GD
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'PEI_GD',
    'PEI gros débit',
     true,
     NULL,
     'Liste les PEI gros débit',
     'with observations as (
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
                (''COMMUNE'' = ''null''
                    or p.pei_commune_id::text = ''COMMUNE'')
                and (''INVIOLABILITE'' = ''null''
                    or pibi.pibi_debit_renforce::text = ''INVIOLABILITE'')
                and (''NATURE_DECI'' = ''null''
                    or deci.nature_deci_id::text = ''NATURE_DECI'')
                and (''ADRESSE'' = ''null''
                    or voie.voie_libelle like ''% ADRESSE %'')
                and (n.nature_code = ''BI''
                    and d.diametre_code = ''DIAM100''
                    and pibi.pibi_jumele_id is not null)
                or (n.nature_code = ''PI''
                    and d.diametre_code = ''DIAM150'')
            order by
                c.commune_libelle,
                p.pei_numero_complet;',
    'DECI'::remocra.type_module)
;
INSERT INTO remocra.rapport_personnalise_parametre (rapport_personnalise_parametre_id, rapport_personnalise_parametre_rapport_personnalise_id, rapport_personnalise_parametre_code, rapport_personnalise_parametre_libelle, rapport_personnalise_parametre_source_sql, rapport_personnalise_parametre_description, rapport_personnalise_parametre_source_sql_id, rapport_personnalise_parametre_source_sql_libelle, rapport_personnalise_parametre_valeur_defaut, rapport_personnalise_parametre_is_required, rapport_personnalise_parametre_type, rapport_personnalise_parametre_ordre)
VALUES
(-- PEI_GD | COMMUNE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_GD'),
	'COMMUNE',
	'Commune',
	'SELECT * FROM (
	SELECT
		'''' AS id,
        ''Tous'' AS libelle,
        CAST(NULL AS TEXT) AS tricol
	UNION
    SELECT
        commune.commune_id::text AS id,
        commune.commune_libelle::text AS libelle,
        commune.commune_libelle AS tricol
    FROM remocra.commune
        JOIN remocra.zone_integration zi ON ST_CONTAINS(zi.zone_integration_geometrie, commune.commune_geometrie)
            AND zi.zone_integration_id = #ZONE_COMPETENCE_ID#
	) AS united_options
	ORDER BY
		tricol IS NOT NULL,
	    CAST(SUBSTRING(tricol FROM ''([0-9]+)'') AS INTEGER),
	    libelle;',
	'',
	' commune.commune_id::text',
	'commune.commune_libelle::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	0
),
(-- PEI_GD | DOMAINE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_GD'),
	'DOMAINE',
	'Domaine',
	'select
	        '' '' as id,
	        '' Tous'' as libelle
	    union
	    select
	        distinct domaine_id::text as id,
	        domaine_libelle::text as libelle
	    from
	        remocra.domaine
	    order by
	        id nulls first;',
	'',
	'domaine_id::text',
	'domaine_libelle::text',
	NULL,
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	1
),
(-- PEI_GD | INVIOLABILITE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_GD'),
	'INVIOLABILITE',
	'Inviolabilité',
	'select
		'''' as id,
		''tous'' as libelle
	union
	select
		cast(true as text) as id,
		cast(''vrai'' as text) as libelle
	union
	select
		''false'' as id,
		''faux'' as libelle
	order by
		id nulls first;', '', 'cast(true as text)', 'cast(''vrai'' as text)', NULL, true, 'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	2
),
(-- PEI_GD | NATURE_DECI
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_GD'),
	'NATURE_DECI',
	'Nature DECI',
	'select
	        '''' as id,
	        ''Tous'' as libelle
	    union
	    select
	        nd.nature_deci_id::text as id,
	        nd.nature_deci_libelle::text as libelle
	    from
	        remocra.nature_deci nd
	    order by
	        id nulls first;', '', 'nd.nature_deci_id::text', 'nd.nature_deci_libelle::text', NULL, true, 'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	3
),
(-- PEI_GD | ADRESSE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_GD'),
	'ADRESSE',
	'Adresse',
	NULL,
	'',
	NULL,
	NULL,
	NULL,
	true,
	'TEXT_INPUT'::remocra.type_parametre_rapport_courrier,
	4
);

---- NB_PEI_INDISPO_COMMUNE
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'NB_PEI_INDISPO_COMMUNE',
    'Nombre de PEI indispo par commune',
    true,
    NULL,
    'Nombre de PEI indisponibles à l’instant T par commune',
    'select
            c.commune_libelle,
            count(p.pei_id) as "Total PEI indispo"
        from
            remocra.pei p
        join remocra.commune c on
            c.commune_id = p.pei_commune_id
        join remocra.zone_integration zi on
            zi.zone_integration_id = p.pei_zone_speciale_id
        where
            p.pei_zone_speciale_id = #ZONE_COMPETENCE_ID#
            and p.pei_disponibilite_terrestre = ''INDISPONIBLE''
        group by
            c.commune_libelle
        order by
        c.commune_libelle;',
    'DECI'::remocra.type_module)
;

---- DSM_PAR_DEBIT
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'DSM_PAR_DEBIT',
    'Débits simultanés',
    true,
    NULL,
    'Liste les débits simultanés',
    'SELECT
                        DISTINCT ON (ds.debit_simultane_id)
                        ds.debit_simultane_numero_dossier AS "Numéro de dossier",
                        c.commune_libelle AS "Commune",
                        s.site_libelle AS "Site",
                        to_char(dsm.debit_simultane_mesure_date_mesure, ''dd/mm/yyyy'') AS "Dernière mesure",
                        dsm.debit_simultane_mesure_debit_mesure AS "Débit mesuré",
                        ldsmp.nb_pei AS "Nombre PEI",
                        tr.type_reseau_libelle AS "Type de réseau",
                        pibi.pibi_diametre_canalisation AS "Diamètre de canalisation"
                        FROM
                        (SELECT
                        debit_simultane_mesure_id AS dsm_id,
                        COUNT(pei_id) AS nb_pei,
                        (SELECT pei_id FROM remocra.l_debit_simultane_mesure_pei t2 where t1.debit_simultane_mesure_id=t2.debit_simultane_mesure_id LIMIT 1)
                        from remocra.l_debit_simultane_mesure_pei t1
                        GROUP BY debit_simultane_mesure_id
                        ) AS ldsmp
                        JOIN remocra.debit_simultane_mesure dsm ON dsm.debit_simultane_mesure_id=ldsmp.dsm_id AND (''DSM_DEBIT_RETENU''=''null'' OR dsm.debit_simultane_mesure_debit_retenu::text=''DSM_DEBIT_RETENU'')
                        LEFT JOIN remocra.debit_simultane ds ON ds.debit_simultane_id = dsm.debit_simultane_id
                        LEFT JOIN remocra.site s ON ds.debit_simultane_site_id=s.site_id
                        LEFT JOIN remocra.pei p ON p.pei_id=ldsmp.pei_id
                        LEFT JOIN remocra.commune c ON c.commune_id=p.pei_commune_id AND s.site_id=p.pei_site_id
                        LEFT JOIN remocra.type_reseau tr ON tr.type_reseau_id=p.pei_id
                        LEFT JOIN remocra.pibi AS pibi ON pibi.pibi_id=p.pei_id
                        ORDER BY
                        ds.debit_simultane_id,
                        dsm.debit_simultane_mesure_date_mesure;
                        select
                debit_simultane_mesure_id as dsm_id,
                COUNT(pei_id) as nb_pei,
                (
                select
                    pei_id
                from
                    remocra.l_debit_simultane_mesure_pei t2
                where
                    t1.debit_simultane_mesure_id = t2.debit_simultane_mesure_id
                limit 1)
            from
                remocra.l_debit_simultane_mesure_pei t1
            group by
                debit_simultane_mesure_id;',
    'DECI'::remocra.type_module)
;
INSERT INTO remocra.rapport_personnalise_parametre (rapport_personnalise_parametre_id, rapport_personnalise_parametre_rapport_personnalise_id, rapport_personnalise_parametre_code, rapport_personnalise_parametre_libelle, rapport_personnalise_parametre_source_sql, rapport_personnalise_parametre_description, rapport_personnalise_parametre_source_sql_id, rapport_personnalise_parametre_source_sql_libelle, rapport_personnalise_parametre_valeur_defaut, rapport_personnalise_parametre_is_required, rapport_personnalise_parametre_type, rapport_personnalise_parametre_ordre)
VALUES
(-- DSM_PAR_DEBIT | DSM_DEBIT_RETENU
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_GD'),
	'DSM_DEBIT_RETENU',
	'Débit retenu',
	'select
        '''' as id,
        ''Tous'' as libelle
    union select distinct
        debit_simultane_mesure_debit_retenu::text as id,
        debit_simultane_mesure_debit_retenu::text as libelle
    FROM remocra.debit_simultane_mesure
    where debit_simultane_mesure_debit_retenu is not null',
	'', 'debit_simultane_mesure_debit_retenu::text', 'debit_simultane_mesure_debit_retenu::text', NULL, true, 'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	0
);

---- PEI_ADRESSE_INCERTAINE
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'PEI_ADRESSE_INCERTAINE',
    'PEI à adresse incertaine',
    true,
    NULL,
    'Liste les PEI dont l''adresse est incertaine (voie non reconnue)',
    'select
        p.pei_numero_complet as "Numéro",
        c.commune_libelle as "Commune",
        p.pei_voie_texte as "Voie",
        v.voie_libelle as "Carrefour"
    from
        remocra.pei p
    join remocra.zone_integration zi on
        ST_CONTAINS(zi.zone_integration_geometrie,
        p.pei_geometrie)
        and zi.zone_integration_id = #ZONE_COMPETENCE_ID#
    join remocra.commune c on
        c.commune_id = p.pei_commune_id
    left join remocra.voie v on
        v.voie_id = p.pei_croisement_id
    where
        p.pei_voie_texte is not null
    order by
        c.commune_libelle;',
    'DECI'::remocra.type_module)
;
