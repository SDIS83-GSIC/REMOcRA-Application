
UPDATE remocra.rapport_personnalise_parametre
SET rapport_personnalise_parametre_is_required = false
WHERE rapport_personnalise_parametre_code = 'ADRESSE' and rapport_personnalise_parametre_rapport_personnalise_id in (
    SELECT rapport_personnalise_id
    FROM remocra.rapport_personnalise
    WHERE rapport_personnalise_protected = true
);

UPDATE remocra.rapport_personnalise_parametre
SET rapport_personnalise_parametre_source_sql = 'with dernier_debit_pression as (
    select distinct on (v.pei_id)
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
WHERE rapport_personnalise_parametre_code = 'DEBIT' and rapport_personnalise_parametre_rapport_personnalise_id in (
    SELECT rapport_personnalise_id
    FROM remocra.rapport_personnalise
    WHERE rapport_personnalise_protected = true
);
