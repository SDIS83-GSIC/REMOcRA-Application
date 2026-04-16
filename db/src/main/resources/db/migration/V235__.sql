INSERT INTO remocra.rapport_personnalise_parametre (rapport_personnalise_parametre_id, rapport_personnalise_parametre_rapport_personnalise_id, rapport_personnalise_parametre_code, rapport_personnalise_parametre_libelle, rapport_personnalise_parametre_source_sql, rapport_personnalise_parametre_valeur_defaut, rapport_personnalise_parametre_is_required, rapport_personnalise_parametre_type,rapport_personnalise_parametre_ordre)
VALUES (gen_random_uuid(), (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'INDICATEUR_COMMUNE'),
 'COMMUNE',
 'Commune',
 'SELECT * FROM (
          SELECT ''tous'' AS id, ''Tous'' AS libelle, CAST(NULL AS TEXT) AS tricol
          UNION
          SELECT commune.commune_id::text as id, commune.commune_libelle::text as libelle, commune.commune_libelle AS tricol
          FROM remocra.commune
          JOIN remocra.zone_integration zi ON ST_CONTAINS(zi.zone_integration_geometrie, commune.commune_geometrie)
              AND zi.zone_integration_id = #ZONE_COMPETENCE_ID#
      ) AS united_options
      ORDER BY CASE WHEN id = ''tous'' THEN 0 ELSE 1 END,
               (SUBSTRING(tricol FROM ''([0-9]+)'') IS NULL),
               COALESCE(CAST(SUBSTRING(tricol FROM ''([0-9]+)'') AS INTEGER), 0),
               libelle;',
 'tous',
 true,
 'SELECT_INPUT'::remocra."type_parametre_rapport_courrier",
 0);

UPDATE remocra.rapport_personnalise set rapport_personnalise_source_sql =
'WITH stats AS (SELECT
       count(pei_id) FILTER(WHERE pei_disponibilite_terrestre = ''INDISPONIBLE''::remocra."DISPONIBILITE") AS nb_indispo,
       count(pei_id) FILTER(WHERE pei_disponibilite_terrestre = ''DISPONIBLE''::remocra."DISPONIBILITE") AS nb_dispo,
       count(pei_id) FILTER(WHERE pei_disponibilite_terrestre = ''NON_CONFORME''::remocra."DISPONIBILITE") AS nb_non_conforme,
       count(pei_id) AS total,
       pei_commune_id
       FROM pei
       GROUP BY pei_commune_id
       )
      SELECT
       commune.commune_code_insee AS "INSEE",
       commune.commune_libelle AS "Commune",
       stats.total AS "Total",
       stats.nb_dispo AS "Disponibles",
       stats.nb_indispo AS "Non disponibles",
       stats.nb_non_conforme AS "Non conformes",
       TRUNC(stats.nb_dispo::decimal / stats.total * 100, 2) AS "Pourcentage disponibles"
      FROM stats
      JOIN commune ON commune_id = stats.pei_commune_id
      where
            (''COMMUNE'' = ''tous''
              or stats.pei_commune_id::text = ''COMMUNE'')
      order by CASE WHEN commune_libelle ~ ''^[0-9]+'' THEN 0 ELSE 1 END,
      COALESCE(NULLIF(SUBSTRING(commune_libelle, ''^(\\d+)''), ''''), ''0'')::integer,
      commune_libelle;'
where rapport_personnalise_code = 'INDICATEUR_COMMUNE';
