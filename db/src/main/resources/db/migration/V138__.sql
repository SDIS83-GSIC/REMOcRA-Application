-- DSM_PAR_DEBIT
UPDATE remocra.rapport_personnalise
set rapport_personnalise_source_sql =  'SELECT
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
        LEFT JOIN remocra.debit_simultane_mesure dsm ON dsm.debit_simultane_mesure_id=ldsmp.dsm_id
        LEFT JOIN remocra.debit_simultane ds ON ds.debit_simultane_id = dsm.debit_simultane_id
        LEFT JOIN remocra.site s ON ds.debit_simultane_site_id=s.site_id
        LEFT JOIN remocra.pei p ON p.pei_id=ldsmp.pei_id
        LEFT JOIN remocra.commune c ON c.commune_id=p.pei_commune_id
        LEFT JOIN remocra.type_reseau tr ON tr.type_reseau_id=p.pei_id
        LEFT JOIN remocra.pibi AS pibi ON pibi.pibi_id=p.pei_id
        WHERE #DSM_DEBIT_RETENU#=-1 OR dsm.debit_simultane_mesure_debit_retenu=#DSM_DEBIT_RETENU#
        ORDER BY
        ds.debit_simultane_id,
        dsm.debit_simultane_mesure_date_mesure;'
where rapport_personnalise_code = 'DSM_PAR_DEBIT';

DELETE FROM remocra.rapport_personnalise_parametre WHERE rapport_personnalise_parametre_code = 'DSM_DEBIT_RETENU';

INSERT INTO remocra.rapport_personnalise_parametre (
    rapport_personnalise_parametre_id,
    rapport_personnalise_parametre_rapport_personnalise_id,
    rapport_personnalise_parametre_code,
    rapport_personnalise_parametre_libelle,
    rapport_personnalise_parametre_source_sql,
    rapport_personnalise_parametre_description,
    rapport_personnalise_parametre_source_sql_id,
    rapport_personnalise_parametre_source_sql_libelle,
    rapport_personnalise_parametre_valeur_defaut,
    rapport_personnalise_parametre_is_required,
    rapport_personnalise_parametre_type,
    rapport_personnalise_parametre_ordre
)
SELECT
    gen_random_uuid(),
    rapport_personnalise_id,
    '#DSM_DEBIT_RETENU#',
    'Débit retenu',
    'select
        -1::int as id,
        ''Tous'' as libelle
    union select distinct
        debit_simultane_mesure_debit_retenu::int as id,
        debit_simultane_mesure_debit_retenu::text as libelle
    FROM remocra.debit_simultane_mesure
    where debit_simultane_mesure_debit_retenu is not null
    ',
    '',
    'debit_simultane_mesure_debit_retenu::int',
    'debit_simultane_mesure_debit_retenu::text',
    NULL,
    true,
   'SELECT_INPUT',
    0
FROM rapport_personnalise WHERE rapport_personnalise_code='DSM_PAR_DEBIT';
