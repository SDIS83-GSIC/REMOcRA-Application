UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql = '
WITH anomalie AS (
    SELECT lpa.pei_id,
      string_agg(a.anomalie_libelle, '', '') AS anomalies
    FROM remocra.l_pei_anomalie lpa
      JOIN remocra.anomalie a ON a.anomalie_id = lpa.anomalie_id
    GROUP BY lpa.pei_id
),
observations AS (
  SELECT DISTINCT ON (v.visite_pei_id)
       v.visite_pei_id,
       v.visite_observation AS observations
  FROM remocra.visite v
  ORDER BY v.visite_pei_id, v.visite_date DESC
)
SELECT
    p.pei_numero_complet AS "Numéro",
    p.pei_type_pei AS "Type",
    d.diametre_libelle AS "Diametre",
    vplm.debit AS "Débit",
    domaine.domaine_libelle AS "Domaine",
    deci.nature_deci_libelle AS "DECI",
    c.commune_libelle AS "Commune",
    COALESCE(voie.voie_libelle, p.pei_voie_texte) AS "Adresse",
    o.observations AS "Observations",
    pibi.pibi_debit_renforce AS "Débit renforcé",
    pibi.pibi_dispositif_inviolabilite AS "Dispositif d''inviolabilité",
    anomalie.anomalies as "Anomalies"
FROM remocra.pei p
INNER JOIN remocra.domaine domaine ON domaine.domaine_id = p.pei_domaine_id
LEFT JOIN remocra.pibi pibi ON pibi.pibi_id = p.pei_id
LEFT JOIN remocra.diametre d ON d.diametre_id = pibi.pibi_diametre_id
JOIN remocra.nature_deci deci ON deci.nature_deci_id = p.pei_nature_deci_id
JOIN remocra.commune c ON c.commune_id = p.pei_commune_id
LEFT JOIN remocra.voie voie ON voie.voie_id = p.pei_voie_id
LEFT JOIN observations o ON o.visite_pei_id = p.pei_id
JOIN anomalie ON anomalie.pei_id = p.pei_id
LEFT JOIN remocra.v_pei_last_mesures vplm ON vplm.pei_id = p.pei_id
WHERE
    (''COMMUNE'' = ''tous'' OR p.pei_commune_id::text = ''COMMUNE'')
    AND (''TYPE'' = ''tous'' OR p.pei_type_pei::text = ''TYPE'')
    AND (''DIAMETRE'' = ''tous'' OR (pibi.pibi_diametre_id::text = ''DIAMETRE''))
    AND (''RENFORCE'' = ''tous'' OR pibi.pibi_debit_renforce::text = ''RENFORCE'')
    AND (''INVIOLABILITE'' = ''tous'' OR pibi.pibi_dispositif_inviolabilite::text = ''INVIOLABILITE'')
    AND (''DEBIT'' = ''tous'' OR vplm.debit::text = ''DEBIT'')
    AND (''DOMAINE'' = ''tous'' OR domaine.domaine_id::text = ''DOMAINE'')
ORDER BY c.commune_libelle, p.pei_numero_complet;
'
WHERE rapport_personnalise_code = 'PEI_ANOMALIES';
