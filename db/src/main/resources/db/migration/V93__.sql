update remocra.rapport_personnalise rp set rapport_personnalise_source_sql=E'select
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
        and p.pei_disponibilite_terrestre = \'INDISPONIBLE\'
    group by
        c.commune_libelle
    order by
    c.commune_libelle;
'
where rp.rapport_personnalise_code='NB_PEI_INDISPO_COMMUNE';

update remocra.rapport_personnalise rp set rapport_personnalise_source_sql=E'select
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
        c.commune_libelle;
'
where rp.rapport_personnalise_code='PEI_ADRESSE_INCERTAINE';

update
	remocra.rapport_personnalise_parametre rpp
set
	rapport_personnalise_parametre_source_sql = E'select
        \'\' as id,
        \'Tous\' as libelle,
        cast(null as text) as tricol
    union
    select
        distinct     commune.commune_id::text as id, commune.commune_libelle::text as libelle  , commune.commune_libelle as tricol
    from
        remocra.commune commune
    join remocra.zone_integration zi on
        ST_CONTAINS(zi.zone_integration_geometrie,
        commune.commune_geometrie)
        and zi.zone_integration_id = #ZONE_COMPETENCE_ID#
    order by
        tricol nulls first;'
from
	remocra.rapport_personnalise rp
where
	rp.rapport_personnalise_id = rpp.rapport_personnalise_parametre_rapport_personnalise_id
	and (rp.rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION' 
    or rp.rapport_personnalise_code = 'PEI_GD')
	and rpp.rapport_personnalise_parametre_code = 'COMMUNE';

update
	remocra.rapport_personnalise_parametre rpp
set
	rapport_personnalise_parametre_source_sql = E'select
        \'\' as id,
        \'Tous\' as libelle,
        cast(null as text) as tricol
    union
    select
        distinct   c.commune_id::text as id, c.commune_libelle::text as libelle   ,
        c.commune_libelle as tricol
    from
        remocra.commune c
    join remocra.zone_integration zi on
        ST_CONTAINS(zi.zone_integration_geometrie,
        c.commune_geometrie)
        and zi.zone_integration_id = #ZONE_COMPETENCE_ID#
    order by
        tricol nulls first;'
from
	remocra.rapport_personnalise rp
where
	rp.rapport_personnalise_id = rpp.rapport_personnalise_parametre_rapport_personnalise_id
	and rp.rapport_personnalise_code = 'PEI_ANOMALIES'
	and rpp.rapport_personnalise_parametre_code = 'COMMUNE';

