UPDATE remocra.rapport_personnalise_parametre SET rapport_personnalise_parametre_source_sql_libelle =
    'case
               when pg.enumlabel = ''DISPONIBLE'' then ''Disponible''
               when pg.enumlabel = ''INDISPONIBLE'' then ''Indisponible''
               when pg.enumlabel = ''NON_CONFORME'' then (select parametre_valeur from remocra.parametre where parametre_code = ''PEI_LIBELLE_NON_CONFORME'')
               else pg.enumlabel::text
             end'
WHERE rapport_personnalise_parametre_id IN (
    SELECT rpp.rapport_personnalise_parametre_id
    FROM remocra.rapport_personnalise_parametre rpp
             JOIN remocra.rapport_personnalise rp ON rpp.rapport_personnalise_parametre_rapport_personnalise_id = rp.rapport_personnalise_id
    WHERE rpp.rapport_personnalise_parametre_code = 'DISPONIBILITE_CODE'
      AND rp.rapport_personnalise_protected = true
);
