UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
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
     order by CASE WHEN commune_libelle ~ ''^[0-9]+'' THEN 0 ELSE 1 END,
     COALESCE(NULLIF(SUBSTRING(commune_libelle, ''^(\\d+)''), ''''), ''0'')::integer,
     commune_libelle;'
WHERE rapport_personnalise_code = 'INDICATEUR_COMMUNE';

UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
    'select
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
             order by CASE WHEN commune_libelle ~ ''^[0-9]+'' THEN 0 ELSE 1 END,
                  COALESCE(NULLIF(SUBSTRING(commune_libelle, ''^(\\d+)''), ''''), ''0'')::integer,
                  commune_libelle;'
WHERE rapport_personnalise_code = 'NB_PEI_INDISPO_COMMUNE';

UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
    'select
                     c.commune_libelle as "Nom de la commune",
                     pibi.pibi_identifiant_gestionnaire as "Nom et capacité du réservoir",
                     count(p.pei_id) as "Nombre de PEI alimentés par le RI (sur la commune)"
                 from
                     remocra.pei p
                 join remocra.commune c on
                     c.commune_id = p.pei_commune_id
                 join remocra.pibi pibi on
                     pibi.pibi_id = p.pei_id
                 group by
                     c.commune_libelle,
                     pibi.pibi_identifiant_gestionnaire
                 order by CASE WHEN commune_libelle ~ ''^[0-9]+'' THEN 0 ELSE 1 END,
                                  COALESCE(NULLIF(SUBSTRING(commune_libelle, ''^(\\d+)''), ''''), ''0'')::integer,
                                  commune_libelle;'
WHERE rapport_personnalise_code = 'LISTE_RI';

UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
    'select
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
         order by CASE WHEN commune_libelle ~ ''^[0-9]+'' THEN 0 ELSE 1 END,
         COALESCE(NULLIF(SUBSTRING(commune_libelle, ''^(\\d+)''), ''''), ''0'')::integer,
         commune_libelle;'
WHERE rapport_personnalise_code = 'PEI_ADRESSE_INCERTAINE';

UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
    'select
                     p.pei_numero_complet as "Numéro",
                     n.nature_libelle as "Nature",
                     St_X(p.pei_geometrie)::integer as "X",
                     St_Y(p.pei_geometrie)::integer as "Y",
                     c.commune_libelle as "Commune",
                     ld.lieu_dit_libelle as "Lieu dit",
                     v.voie_libelle as "Voie",
                     croisement.voie_libelle as "Carrefour",
                     p.pei_disponibilite_terrestre as "Disponible"
                 from
                     remocra.pei p
                 join remocra.commune c on
                     c.commune_id = p.pei_commune_id
                 left join remocra.nature n on
                     n.nature_id = p.pei_nature_id
                 left join remocra.lieu_dit ld on
                     ld.lieu_dit_id = p.pei_lieu_dit_id
                 left join remocra.voie v on
                     v.voie_id = p.pei_voie_id
                 left join remocra.voie croisement on
                     croisement.voie_id = p.pei_croisement_id
                 order by
                     CASE WHEN commune_libelle ~ ''^[0-9]+'' THEN 0 ELSE 1 END,
                     COALESCE(NULLIF(SUBSTRING(commune_libelle, ''^(\\d+)''), ''''), ''0'')::integer,
                     commune_libelle,
                     p.pei_numero_complet;'
WHERE rapport_personnalise_code = 'LISTE_PEI';

UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
    'with observations as (
                                                 select
                                                     distinct on
                                                     (visite_pei_id)
                                                     visite_pei_id,
                                                     visite_observation
                                                 from
                                                     remocra.visite
                                                 order by
                                                     visite_pei_id,
                                                     visite_date desc
                                                 )
                                                 select
                                                     p.pei_numero_complet as "Numéro",
                                                     n.nature_libelle as "Type",
                                                     d.diametre_libelle as "Diametre",
                                                     dsm.debit_simultane_mesure_debit_mesure as "Débit",
                                                     domaine.domaine_libelle as "Domaine",
                                                     deci.nature_deci_libelle as "Deci",
                                                     c.commune_libelle as "Commune",
                                                     voie.voie_libelle as "Adresse",
                                                     o.visite_observation as "Observation",
                                                     pibi.pibi_dispositif_inviolabilite as "Dispositif d''inviolabilité"
                                                 from
                                                     remocra.pei p
                                                 left join remocra.commune c on
                                                     c.commune_id = p.pei_commune_id
                                                 left join remocra.pibi pibi on
                                                     pibi.pibi_id = p.pei_id
                                                 join remocra.l_debit_simultane_mesure_pei l_dsmp on
                                                     l_dsmp.pei_id = p.pei_id
                                                 left join remocra.debit_simultane_mesure dsm on
                                                     dsm.debit_simultane_id = l_dsmp.debit_simultane_mesure_id
                                                 left join remocra.diametre d on
                                                     d.diametre_id = pibi.pibi_diametre_id
                                                 left join remocra.voie voie on
                                                     voie.voie_id = p.pei_voie_id
                                                 left join remocra.nature_deci deci on
                                                     deci.nature_deci_id = p.pei_nature_deci_id
                                                 left join remocra.domaine domaine on
                                                     domaine.domaine_id = p.pei_domaine_id
                                                 left join remocra.v_pei_visite_date v_psd on
                                                     v_psd.pei_id = p.pei_id
                                                 left join remocra.nature n on
                                                     n.nature_id = p.pei_nature_id
                                                 left join observations o on
                                                     o.visite_pei_id = p.pei_id
                                                 where
                                                     (''COMMUNE'' = ''tous''
                                                         or p.pei_commune_id::text = ''COMMUNE'')
                                                     and (''INVIOLABILITE'' = ''tous''
                                                         or pibi.pibi_dispositif_inviolabilite::text = ''INVIOLABILITE'')
                                                     and (''NATURE_DECI'' = ''tous''
                                                         or deci.nature_deci_id::text = ''NATURE_DECI'')
                                                     and (''ADRESSE'' is null
                                                         or voie.voie_libelle like ''%'' || ''ADRESSE'' || ''%'')
                                                     and (n.nature_code = ''BI''
                                                         and d.diametre_code = ''DIAM100''
                                                         and pibi.pibi_jumele_id is not null)
                                                     or (n.nature_code = ''PI''
                                                         and d.diametre_code = ''DIAM150'')
                                                 order by
                                                     CASE WHEN commune_libelle ~ ''^[0-9]+'' THEN 0 ELSE 1 END,
                                                                          COALESCE(NULLIF(SUBSTRING(commune_libelle, ''^(\\d+)''), ''''), ''0'')::integer,
                                                                          commune_libelle,
                                                     p.pei_numero_complet;'
WHERE rapport_personnalise_code = 'PEI_GD';
