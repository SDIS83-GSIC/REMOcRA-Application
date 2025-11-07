--ETAT_AVANCEMENT_CONTROLE
UPDATE remocra.rapport_personnalise
SET rapport_personnalise_source_sql =
'WITH RECURSIVE organismes(organisme_id) AS (
         SELECT o.organisme_id FROM remocra.organisme o JOIN remocra.zone_integration zc ON o.organisme_zone_integration_id = zc.zone_integration_id WHERE zc.zone_integration_id = #ZONE_COMPETENCE_ID#
       UNION ALL
         SELECT o.organisme_id
         FROM organismes os, remocra.organisme o
         WHERE o.organisme_parent_id = os.organisme_id
     ),
     param_prive AS (
       SELECT parametre_valeur || '' days'' AS valeur
       FROM remocra.parametre
       WHERE parametre_code  = ''PEI_RENOUVELLEMENT_CTRL_PRIVE''
     ),
     param_public AS (
       SELECT parametre_valeur || '' days'' AS valeur
       FROM remocra.parametre
       WHERE parametre_code = ''PEI_RENOUVELLEMENT_CTRL_PUBLIC''
     ),
     param_icpe AS (
            SELECT parametre_valeur || '' days'' AS valeur
            FROM remocra.parametre
            WHERE parametre_code  = ''PEI_RENOUVELLEMENT_CTRL_ICPE''
     ),
      param_icpe_conv AS (
                 SELECT parametre_valeur || '' days'' AS valeur
                 FROM remocra.parametre
                 WHERE parametre_code  = ''PEI_RENOUVELLEMENT_CTRL_ICPE_CONVENTIONNE''
          ),
     date_reception AS (
       SELECT hv.visite_pei_id AS pei_id, hv.visite_date
       FROM remocra.visite hv
        WHERE hv.visite_type_visite = ''RECEPTION''
     ),
     der_date_ctp AS (
      SELECT hv.visite_pei_id AS pei_id, max(hv.visite_date) AS visite_date
       FROM remocra.visite hv
        WHERE hv.visite_type_visite = ''CTP''
        GROUP BY hv.visite_pei_id
     ),
     etat_pei AS (
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
       FROM remocra.pei
         JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND (thnd.nature_deci_code = ''PUBLIC'' OR thnd.nature_deci_code = ''CONVENTIONNE'')
         JOIN param_public pp ON 1 = 1
         LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
         LEFT JOIN der_date_ctp ddc ON ddc.pei_id = pei.pei_id
       UNION
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
       FROM remocra.pei
         JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND thnd.nature_deci_code = ''PRIVE''
         JOIN param_prive pp ON 1 = 1
         LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
         LEFT JOIN der_date_ctp ddc ON ddc.pei_id = pei.pei_id
       UNION
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
          FROM remocra.pei
            JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND thnd.nature_deci_code = ''ICPE''
            JOIN param_icpe pp ON 1 = 1
            LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
            LEFT JOIN der_date_ctp ddc ON ddc.pei_id = pei.pei_id
       UNION
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
         FROM remocra.pei
           JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND thnd.nature_deci_code = ''ICPE_CONVENTIONNE''
           JOIN param_icpe_conv pp ON 1 = 1
           LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
           LEFT JOIN der_date_ctp ddc ON ddc.pei_id = pei.pei_id
     ),
     data AS (
       SELECT o.organisme_libelle AS "Nom de l''organisme",
         (CASE WHEN
         	CAST((CAST(sum(
         		CASE WHEN etat_pei.datectrl_ok
         		THEN 1
         		ELSE 0
         		END) AS numeric)
     		/ CAST(COUNT(l_tournee_pei.pei_id) AS numeric) * 100) AS numeric(5,2)) < 100
         THEN 2
         ELSE 1
         END) AS etat,
         o.organisme_type_organisme_id,
         tournee.tournee_libelle AS "Nom de la tournée",
         COUNT(l_tournee_pei.pei_id) AS "Nombre de PEI dans la tournée",
         sum(CASE WHEN etat_pei.datectrl_ok THEN 1 ELSE 0 END) AS "Nombre de PEI visités",
         sum(CASE WHEN etat_pei.datectrl_ok THEN 0 ELSE 1 END) AS "Nombre de PEI non visités",
         CAST((CAST(sum(CASE WHEN etat_pei.datectrl_ok THEN 1 ELSE 0 END) AS numeric) / CAST(COUNT(l_tournee_pei.pei_id) AS numeric) * 100) AS numeric(5,2)) AS "% Visités"
       FROM remocra.tournee
         JOIN remocra.l_tournee_pei ON l_tournee_pei.tournee_id = tournee.tournee_id
         LEFT JOIN etat_pei ON etat_pei.pei_id = l_tournee_pei.pei_id
         JOIN remocra.organisme o ON o.organisme_id = tournee.tournee_organisme_id
       WHERE o.organisme_id IN (SELECT organisme_id FROM organismes)
       GROUP BY o.organisme_libelle, tournee.tournee_libelle, o.organisme_type_organisme_id
       ORDER BY o.organisme_libelle, tournee.tournee_libelle
     )
     SELECT "Nom de l''organisme", "Nom de la tournée", "Nombre de PEI dans la tournée", "Nombre de PEI visités", "Nombre de PEI non visités",  "% Visités"
     FROM DATA
     WHERE (organisme_type_organisme_id::text = ''#TYPE_ORGANISME#'' OR  ''tous''= ''#TYPE_ORGANISME#'')
     AND  etat | ''#FILTRE_COMPLETION#'' = ''#FILTRE_COMPLETION#'';'
WHERE rapport_personnalise_code='ETAT_AVANCEMENT_CONTROLE';


--ETAT_AVANCEMENT_ROP
UPDATE remocra.rapport_personnalise
SET rapport_personnalise_source_sql =
'WITH RECURSIVE organismes(organisme_id) AS (
         SELECT o.organisme_id FROM remocra.organisme o JOIN remocra.zone_integration zc ON o.organisme_zone_integration_id = zc.zone_integration_id WHERE zc.zone_integration_id = #ZONE_COMPETENCE_ID#
       UNION ALL
         SELECT o.organisme_id
         FROM organismes os, remocra.organisme o
         WHERE o.organisme_parent_id = os.organisme_id
     ),
     param_prive AS (
       SELECT parametre_valeur || '' days'' AS valeur
       FROM remocra.parametre
       WHERE parametre_code  = ''PEI_RENOUVELLEMENT_RECO_PRIVE''
     ),
     param_public AS (
       SELECT parametre_valeur || '' days'' AS valeur
       FROM remocra.parametre
       WHERE parametre_code = ''PEI_RENOUVELLEMENT_RECO_PUBLIC''
     ),
     param_icpe AS (
             SELECT parametre_valeur || '' days'' AS valeur
             FROM remocra.parametre
             WHERE parametre_code  = ''PEI_RENOUVELLEMENT_RECO_ICPE''
      ),
       param_icpe_conv AS (
                  SELECT parametre_valeur || '' days'' AS valeur
                  FROM remocra.parametre
                  WHERE parametre_code  = ''PEI_RENOUVELLEMENT_RECO_ICPE_CONVENTIONNE''
           ),
     date_reception AS (
       SELECT hv.visite_pei_id AS pei_id, hv.visite_date
       FROM remocra.visite hv
        WHERE hv.visite_type_visite = ''RECEPTION''
     ),
     der_date_rop AS (
      SELECT hv.visite_pei_id AS pei_id, max(hv.visite_date) AS visite_date
       FROM remocra.visite hv
        WHERE hv.visite_type_visite = ''ROP''
        GROUP BY hv.visite_pei_id
     ),
     etat_pei AS (
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
       FROM remocra.pei
         JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND (thnd.nature_deci_code = ''PUBLIC'' OR thnd.nature_deci_code = ''CONVENTIONNE'')
         JOIN param_public pp ON 1 = 1
         LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
         LEFT JOIN der_date_rop ddc ON ddc.pei_id = pei.pei_id
       UNION
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
       FROM remocra.pei
         JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND thnd.nature_deci_code = ''PRIVE''
         JOIN param_prive pp ON 1 = 1
         LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
         LEFT JOIN der_date_rop ddc ON ddc.pei_id = pei.pei_id
       UNION
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
         FROM remocra.pei
           JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND thnd.nature_deci_code = ''ICPE''
           JOIN param_icpe pp ON 1 = 1
           LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
           LEFT JOIN der_date_rop ddc ON ddc.pei_id = pei.pei_id
       UNION
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
        FROM remocra.pei
          JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND thnd.nature_deci_code = ''ICPE_CONVENTIONNE''
          JOIN param_icpe_conv pp ON 1 = 1
          LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
          LEFT JOIN der_date_rop ddc ON ddc.pei_id = pei.pei_id
     ),
     data AS (
       SELECT o.organisme_libelle AS "Nom de l''organisme",
         (CASE WHEN
         	CAST((CAST(sum(
         		CASE WHEN etat_pei.datectrl_ok
         		THEN 1
         		ELSE 0
         		END) AS numeric)
     		/ CAST(COUNT(l_tournee_pei.pei_id) AS numeric) * 100) AS numeric(5,2)) < 100
         THEN 2
         ELSE 1
         END) AS etat,
         o.organisme_type_organisme_id,
         tournee.tournee_libelle AS "Nom de la tournée",
         COUNT(l_tournee_pei.pei_id) AS "Nombre de PEI dans la tournée",
         sum(CASE WHEN etat_pei.datectrl_ok THEN 1 ELSE 0 END) AS "Nombre de PEI visités",
         sum(CASE WHEN etat_pei.datectrl_ok THEN 0 ELSE 1 END) AS "Nombre de PEI non visités",
         CAST((CAST(sum(CASE WHEN etat_pei.datectrl_ok THEN 1 ELSE 0 END) AS numeric) / CAST(COUNT(l_tournee_pei.pei_id) AS numeric) * 100) AS numeric(5,2)) AS "% Visités"
       FROM remocra.tournee
         JOIN remocra.l_tournee_pei ON l_tournee_pei.tournee_id = tournee.tournee_id
         LEFT JOIN etat_pei ON etat_pei.pei_id = l_tournee_pei.pei_id
         JOIN remocra.organisme o ON o.organisme_id = tournee.tournee_organisme_id
       WHERE o.organisme_id IN (SELECT organisme_id FROM organismes)
       GROUP BY o.organisme_libelle, tournee.tournee_libelle, o.organisme_type_organisme_id
       ORDER BY o.organisme_libelle, tournee.tournee_libelle
     )
     SELECT "Nom de l''organisme", "Nom de la tournée", "Nombre de PEI dans la tournée", "Nombre de PEI visités", "Nombre de PEI non visités",  "% Visités"
     FROM DATA
     WHERE (organisme_type_organisme_id::text = ''#TYPE_ORGANISME#'' OR  ''tous''= ''#TYPE_ORGANISME#'')
     AND  etat | ''#FILTRE_COMPLETION#'' = ''#FILTRE_COMPLETION#'';'
WHERE rapport_personnalise_code='ETAT_AVANCEMENT_ROP';
