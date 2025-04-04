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
    'COMMUNE',
    'Commune',
    E'select
        \'\' as id,
        \'Tous\' as libelle,
        cast(null as text) as tricol
    union
    select
        distinct commune.commune_id::text as id, commune.commune_libelle::text as libelle, commune.commune_libelle as tricol
    from
        remocra.commune commune
    join remocra.zone_integration zi on
        ST_CONTAINS(zi.zone_integration_geometrie,
        commune.commune_geometrie)
        and zi.zone_integration_id = #ZONE_COMPETENCE_ID#
    order by
        tricol nulls first;',
    '',
    'commune.commune_id::text',
    'commune.commune_libelle::text',
    NULL,
    true,
    cast('SELECT_INPUT' as remocra.type_parametre_rapport_courrier),
    1
FROM remocra.rapport_personnalise rp WHERE rp.rapport_personnalise_code='PEI_COMMUNE';
