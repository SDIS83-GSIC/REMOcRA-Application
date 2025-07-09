---- NB_PEI_INDISPO_COMMUNE
UPDATE remocra.rapport_personnalise
set rapport_personnalise_source_sql =  'select
            c.commune_libelle,
            count(p.pei_id) as "Total PEI indispo"
        from
            remocra.pei p
        join remocra.commune c on
            c.commune_id = p.pei_commune_id
        join remocra.zone_integration zi on
            zi.zone_integration_id = #ZONE_COMPETENCE_ID#
        WHERE st_contains(zi.zone_integration_geometrie, p.pei_geometrie) AND p.pei_disponibilite_terrestre = ''INDISPONIBLE''::remocra."DISPONIBILITE"
        group by
            c.commune_libelle
        order by
        c.commune_libelle;'
where rapport_personnalise_code = 'NB_PEI_INDISPO_COMMUNE';
