
---- PEI_SANS_TOURNEE
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'PEI_SANS_TOURNEE',
    'PEI sans tournée',
    true,
    NULL,
    'Liste les PEI non associés à une tournée',
    'select distinct on (p.pei_numero_complet)
         p.pei_numero_complet as "Numéro",
         p.pei_type_pei as "Type",
         d.diametre_libelle as "Diametre",
         v.debit as "Débit",
         domaine.domaine_libelle as "Domaine",
         deci.nature_deci_libelle as "DECI",
         c.commune_libelle as "Commune",
         voie.voie_libelle as "Adresse",
         p.pei_observation as "Observations",
         pibi.pibi_debit_renforce as "Débit renforcé",
         pibi.pibi_dispositif_inviolabilite as "Dispositif d''inviolabilité"
     from
         remocra.pei p
     left join remocra.v_pei_last_mesures v on
         v.pei_id = p.pei_id
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
     LEFT JOIN remocra.l_tournee_pei ltp
     ON ltp.pei_id = p.pei_id
     where
         ltp.tournee_id IS NULL AND
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
             or pibi.pibi_debit_renforce::text = ''INVIOLABILITE'')
		 and (''DEBIT'' = ''tous''
		             or v.debit::text = ''DEBIT'')
     order by p.pei_numero_complet;',
    'DECI'::remocra.type_module)
;
INSERT INTO remocra.rapport_personnalise_parametre (rapport_personnalise_parametre_id, rapport_personnalise_parametre_rapport_personnalise_id, rapport_personnalise_parametre_code, rapport_personnalise_parametre_libelle, rapport_personnalise_parametre_source_sql, rapport_personnalise_parametre_description, rapport_personnalise_parametre_source_sql_id, rapport_personnalise_parametre_source_sql_libelle, rapport_personnalise_parametre_valeur_defaut, rapport_personnalise_parametre_is_required, rapport_personnalise_parametre_type, rapport_personnalise_parametre_ordre)
VALUES
(-- PEI_SANS_TOURNEE | COMMUNE
    gen_random_uuid(),
    (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_SANS_TOURNEE'),
    'COMMUNE',
    'Commune',
    'SELECT * FROM (
		SELECT
			''tous'' as id,
	        ''Tous'' as libelle,
	        CAST(NULL AS TEXT) AS tricol
		UNION
	     SELECT
	        commune.commune_id::text as id,
	        commune.commune_libelle::text as libelle,
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
    'tous',
    true,
    'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
    0
),
(-- PEI_SANS_TOURNEE | DEBIT
    gen_random_uuid(),
    (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_SANS_TOURNEE'),
    'DEBIT',
    'Débit',
    'with dernier_debit_pression as (
        select distinct on (v.pei_id)
            v.debit as id
        from remocra.v_pei_last_mesures v
	     LEFT JOIN remocra.l_tournee_pei ltp
	     ON ltp.pei_id = v.pei_id
            where debit is not null and ltp.tournee_id IS NULL
    ),
    options as (
        select  id::text as id, id::text as libelle  from dernier_debit_pression
        union all
        select ''tous'' as id, ''Tous'' as libelle
    )
    select distinct * from options;',
    '',
    'id::text',
    'id::text',
    'tous',
    true,
    'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
    1
),
(-- PEI_SANS_TOURNEE | TYPE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_SANS_TOURNEE'),
	'TYPE',
	'Type',
	'select
        ''tous'' as id,
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
    'tous',
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	2
),
(-- PEI_SANS_TOURNEE | DOMAINE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_SANS_TOURNEE'),
	'DOMAINE',
	'Domaine',
	'select
        ''tous'' as id,
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
    'tous',
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	3
),
(-- PEI_SANS_TOURNEE | DIAMETRE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_SANS_TOURNEE'),
	'DIAMETRE',
	'Diamètre',
	'select
        ''tous'' as id,
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
    'tous',
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	4
),
(-- PEI_SANS_TOURNEE | RENFORCE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_SANS_TOURNEE'),
	'RENFORCE',
	'Dispositif renforcé',
	'select
		''tous'' as id,
		''Tous'' as libelle
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
    'tous',
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	5
),
(-- PEI_SANS_TOURNEE | INVIOLABILITE
	gen_random_uuid(),
	(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'PEI_SANS_TOURNEE'),
	'INVIOLABILITE',
	'Inviolabilité',
	'select
		''tous'' as id,
		''Tous'' as libelle
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
    'tous',
	true,
	'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
	6
);
