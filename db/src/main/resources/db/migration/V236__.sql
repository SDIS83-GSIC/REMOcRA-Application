UPDATE remocra.rapport_personnalise_parametre SET rapport_personnalise_parametre_source_sql =
 'select *
  from (
      select
          -1::int as id,
          ''Tous'' as libelle,
          0 as ordre
      union
      select distinct
          debit_simultane_mesure_debit_retenu::int as id,
          debit_simultane_mesure_debit_retenu::text as libelle,
          1 as ordre
      from remocra.debit_simultane_mesure
      where debit_simultane_mesure_debit_retenu is not null
  ) t
  order by ordre,
      case when libelle ~ ''^\d+$'' then libelle::int else null end,
      libelle
;
'
WHERE rapport_personnalise_parametre_code = '#DSM_DEBIT_RETENU#' AND rapport_personnalise_parametre_rapport_personnalise_id = (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'DSM_PAR_DEBIT');

UPDATE remocra.rapport_personnalise_parametre SET rapport_personnalise_parametre_source_sql =
 'SELECT ''3'' as id, ''Tous'' as libelle, 0 as ordre UNION
          SELECT ''2'' as id, ''Incomplet'' as libelle, 1 as ordre
           UNION SELECT ''1''  as id,  ''Complet''  as libelle, 1 as ordre order by ordre, id;'
WHERE rapport_personnalise_parametre_code = '#FILTRE_COMPLETION#' AND rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code in ('ETAT_AVANCEMENT_CONTROLE', 'ETAT_AVANCEMENT_ROP'));

UPDATE remocra.rapport_personnalise_parametre SET rapport_personnalise_parametre_source_sql =
 'select * from (
          select
              ''tous'' as id,
              ''Tous'' as libelle
          union
          select distinct diametre_id::text as id, diametre_libelle::text as libelle
          from remocra.diametre
      ) t
      order by CASE WHEN id = ''tous'' THEN 0 ELSE 1 END,
      case when libelle ~ ''^\d+$'' then libelle::int else null end,
        libelle;'
WHERE rapport_personnalise_parametre_code = 'DIAMETRE' AND rapport_personnalise_parametre_rapport_personnalise_id in (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code in ('PEI_ANOMALIES', 'PEI_DIAMETRE_CANALISATION','PEI_SANS_TOURNEE'));

UPDATE remocra.rapport_personnalise_parametre SET rapport_personnalise_parametre_source_sql =
 'with dernier_debit_pression as (
      select distinct on (v.pei_id)
          v.debit as id
      from remocra.v_pei_last_mesures v
      left join remocra.l_tournee_pei ltp
          on ltp.pei_id = v.pei_id
      where debit is not null and ltp.tournee_id is null
  ),
  options as (
      select distinct id::text as id, id::text as libelle from dernier_debit_pression
      union all
      select ''tous'' as id, ''Tous'' as libelle
  )
  select *
  from options
  order by
      case when id = ''tous'' then 0 else 1 end,
      case when id = ''tous'' then null else id::numeric end;'
WHERE rapport_personnalise_parametre_code = 'DEBIT' AND rapport_personnalise_parametre_rapport_personnalise_id = (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code ='PEI_SANS_TOURNEE');

UPDATE remocra.rapport_personnalise_parametre SET rapport_personnalise_parametre_source_sql =
 'select * from (
          select
              ''tous'' as id,
              ''Tous'' as libelle
          union
          select distinct nature_type_pei::text as id, nature_type_pei::text as libelle
          from remocra.nature
      ) t
      order by CASE WHEN id = ''tous'' THEN 0 ELSE 1 END, libelle;'
WHERE rapport_personnalise_parametre_code = 'TYPE' AND rapport_personnalise_parametre_rapport_personnalise_id = (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code ='PEI_SANS_TOURNEE');

UPDATE remocra.rapport_personnalise_parametre SET rapport_personnalise_parametre_source_sql =
 'select * from (
          select
              ''tous'' as id,
              ''Tous'' as libelle
          union
          select distinct domaine_id::text as id, domaine_libelle::text as libelle
          from remocra.domaine
      ) t
      order by CASE WHEN id = ''tous'' THEN 0 ELSE 1 END, libelle;'
WHERE rapport_personnalise_parametre_code = 'DOMAINE' AND rapport_personnalise_parametre_rapport_personnalise_id = (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code ='PEI_SANS_TOURNEE');

UPDATE remocra.rapport_personnalise_parametre SET rapport_personnalise_parametre_source_sql =
 'select * from (
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
WHERE rapport_personnalise_parametre_code in ('RENFORCE', 'INVIOLABILITE') AND rapport_personnalise_parametre_rapport_personnalise_id = (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code ='PEI_SANS_TOURNEE');
