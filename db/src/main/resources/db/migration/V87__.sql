update remocra.rapport_personnalise rp set rapport_personnalise_source_sql=E'SELECT
                        DISTINCT ON (ds.debit_simultane_id)
                        ds.debit_simultane_numero_dossier AS "Numéro de dossier",
                        c.commune_libelle AS "Commune",
                        s.site_libelle AS "Site",
                        to_char(dsm.debit_simultane_mesure_date_mesure, \'dd/mm/yyyy\') AS "Dernière mesure",
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
                        JOIN remocra.debit_simultane_mesure dsm ON dsm.debit_simultane_mesure_id=ldsmp.dsm_id AND (\'DSM_DEBIT_RETENU\'=\'null\' OR dsm.debit_simultane_mesure_debit_retenu::text=\'DSM_DEBIT_RETENU\')
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
                debit_simultane_mesure_id;
'
where rp.rapport_personnalise_code='DSM_PAR_DEBIT';

update
	remocra.rapport_personnalise_parametre rpp
set
	rapport_personnalise_parametre_source_sql = E'with dernier_debit_pression as (
        select distinct on (v.visite_pei_id)
            vcdp.visite_ctrl_debit_pression_debit as id
        from remocra.visite_ctrl_debit_pression vcdp
        join remocra.visite v
            on vcdp.visite_ctrl_debit_pression_visite_id = v.visite_id
    ),
    options as (
        select  id::text as id, id::text as libelle  from dernier_debit_pression
        union all
        select \'\' as id, \'tous\' as libelle
    )
    select distinct * from options;',
	rapport_personnalise_parametre_source_sql_id = 'id::text',
	rapport_personnalise_parametre_source_sql_libelle = 'id::text'
from
	remocra.rapport_personnalise rp
where
	rp.rapport_personnalise_id = rpp.rapport_personnalise_parametre_rapport_personnalise_id
	and (rp.rapport_personnalise_code = 'PEI_ANOMALIES'
		or rp.rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION')
	and rpp.rapport_personnalise_parametre_code = 'DEBIT';

update
	remocra.rapport_personnalise_parametre rpp
set
	rapport_personnalise_parametre_source_sql = E'select
        \' \' as id,
        \' Tous\' as libelle
    union
    select
        distinct nature_type_pei::text as id,
        nature_type_pei::text as libelle
    from
        remocra.nature
    order by
        id nulls first;'
from
	remocra.rapport_personnalise rp
where
	rp.rapport_personnalise_id = rpp.rapport_personnalise_parametre_rapport_personnalise_id
	and (rp.rapport_personnalise_code = 'PEI_ANOMALIES'
		or rp.rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION')
	and rpp.rapport_personnalise_parametre_code = 'TYPE';

update
	remocra.rapport_personnalise_parametre rpp
set
	rapport_personnalise_parametre_source_sql = E'select
        \' \' as id,
        \' Tous\' as libelle
    union
    select
        distinct domaine_id::text as id,
        domaine_libelle::text as libelle
    from
        remocra.domaine
    order by
        id nulls first;'
from
	remocra.rapport_personnalise rp
where
	rp.rapport_personnalise_id = rpp.rapport_personnalise_parametre_rapport_personnalise_id
	and (rp.rapport_personnalise_code = 'PEI_ANOMALIES'
		or rp.rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'
		or rp.rapport_personnalise_code = 'PEI_GD')
	and rpp.rapport_personnalise_parametre_code = 'DOMAINE';

    update
	remocra.rapport_personnalise_parametre rpp
set
	rapport_personnalise_parametre_source_sql = E'select
        \'\' as id,
        \'Tous\' as libelle
    union
    select
        distinct diametre_id::text as id,
        diametre_libelle::text as libelle
    from
        remocra.diametre
    order by
        id nulls first;'
from
	remocra.rapport_personnalise rp
where
	rp.rapport_personnalise_id = rpp.rapport_personnalise_parametre_rapport_personnalise_id
	and (rp.rapport_personnalise_code = 'PEI_ANOMALIES'
		or rp.rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION')
	and rpp.rapport_personnalise_parametre_code = 'DIAMETRE';

update
	remocra.rapport_personnalise_parametre rpp
set
	rapport_personnalise_parametre_source_sql = E'select
	\'\' as id,
	\'tous\' as libelle
union
select
	cast(true as text) as id,
	cast(\'vrai\' as text) as libelle
union
select
	\'false\' as id,
	\'faux\' as libelle
order by
	id nulls first;',
	rapport_personnalise_parametre_source_sql_id = 'cast(true as text)',
	rapport_personnalise_parametre_source_sql_libelle = 'cast(''vrai'' as text)'
from
	remocra.rapport_personnalise rp
where
	rp.rapport_personnalise_id = rpp.rapport_personnalise_parametre_rapport_personnalise_id
	and (rp.rapport_personnalise_code = 'PEI_ANOMALIES'
		or rp.rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION'
		or rp.rapport_personnalise_code = 'PEI_GD')
	and (rpp.rapport_personnalise_parametre_code = 'RENFORCE' or rpp.rapport_personnalise_parametre_code = 'INVIOLABILITE');

