UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql=
'SELECT
    pei_numero_complet as "Numéro",
    pei_type_pei as "Type",
    diametre_libelle as "Diametre",
    v_plm.debit as "Débit",
    domaine_libelle as "Domaine",
    nature_deci_libelle as "DECI",
    commune_libelle as "Commune",
    coalesce(voie_libelle, pei_voie_texte) as "Adresse",
    pei_observation as "Observations",
    pibi_debit_renforce as "Débit renforcé",
    pibi_dispositif_inviolabilite as "Dispositif d''inviolabilité"
FROM remocra.pei
    LEFT JOIN remocra.v_pei_last_mesures v_plm on pei.pei_id = v_plm.pei_id
    JOIN remocra.domaine ON pei_domaine_id = domaine_id
    LEFT JOIN remocra.pibi ON pei.pei_id = pibi_id
    LEFT JOIN remocra.diametre on pibi_diametre_id = diametre_id
    JOIN remocra.nature_deci ON pei_nature_deci_id = nature_deci_id
    JOIN remocra.commune on pei_commune_id = commune_id
    LEFT JOIN remocra.voie ON pei_voie_id = voie_id
    LEFT JOIN remocra.l_tournee_pei ltp ON pei.pei_id = ltp.pei_id
WHERE ltp.tournee_id IS NULL
    AND st_within(pei_geometrie, (SELECT zone_integration_geometrie FROM remocra.zone_integration WHERE zone_integration_id = #ZONE_COMPETENCE#))
    AND (''DOMAINE'' = ''tous'' or domaine_id::text = ''DOMAINE'')
    AND (''COMMUNE'' = ''tous'' OR pei_commune_id::text = ''COMMUNE'')
    AND (''TYPE'' = ''tous'' OR pei_type_pei::text = ''TYPE'')
    AND (''DIAMETRE'' = ''tous'' OR pibi_diametre_id::text = ''DIAMETRE'')
    AND (''RENFORCE'' = ''tous'' OR pibi.pibi_debit_renforce::text = ''RENFORCE'')
    AND (''INVIOLABILITE'' = ''tous'' OR pibi.pibi_debit_renforce::text = ''INVIOLABILITE'')
    AND (''DEBIT'' = ''tous'' OR v_plm.debit::text = ''DEBIT'')
ORDER BY pei_numero_complet;'
WHERE rapport_personnalise_code = 'PEI_SANS_TOURNEE';
