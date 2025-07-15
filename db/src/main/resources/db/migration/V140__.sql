
---- INDICATEUR_COMMUNE
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'INDICATEUR_COMMUNE',
    'Etat des points d''eau par commune',
    true,
    NULL,
    'Etat des points d''eau par commune',
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
     JOIN commune ON commune_id = stats.pei_commune_id;',
    'DECI'::remocra.type_module)
;
