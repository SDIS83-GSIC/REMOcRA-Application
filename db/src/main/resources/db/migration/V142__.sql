
---- ETAT_AVANCEMENT_ROP
INSERT INTO remocra.rapport_personnalise (rapport_personnalise_id, rapport_personnalise_actif, rapport_personnalise_code, rapport_personnalise_libelle, rapport_personnalise_protected, rapport_personnalise_champ_geometrie, rapport_personnalise_description, rapport_personnalise_source_sql, rapport_personnalise_module)
VALUES(
    gen_random_uuid(),
    true,
    'ETAT_AVANCEMENT_ROP',
    'Etat d''avancement des ROP',
    true,
    NULL,
    'Tournées et nombre de PEI visités/non visités durant le délai légal',
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
     AND  etat | ''#FILTRE_COMPLETION#'' = ''#FILTRE_COMPLETION#'';
',
    'DECI'::remocra.type_module)
;
INSERT INTO remocra.rapport_personnalise_parametre (rapport_personnalise_parametre_id, rapport_personnalise_parametre_rapport_personnalise_id, rapport_personnalise_parametre_code, rapport_personnalise_parametre_libelle, rapport_personnalise_parametre_source_sql, rapport_personnalise_parametre_description, rapport_personnalise_parametre_source_sql_id, rapport_personnalise_parametre_source_sql_libelle, rapport_personnalise_parametre_valeur_defaut, rapport_personnalise_parametre_is_required, rapport_personnalise_parametre_type, rapport_personnalise_parametre_ordre)
VALUES
(
    gen_random_uuid(),
    (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'ETAT_AVANCEMENT_ROP'),
    '#TYPE_ORGANISME#',
    'Type d''organisme',
    'SELECT * FROM (
       SELECT ''tous'' as id, ''Tous'' as libelle, 0 as ordre
       UNION
       SELECT type_organisme_id::text as id, type_organisme_libelle as libelle, 1 as ordre
       FROM remocra.type_organisme WHERE type_organisme_actif
     ) t
     ORDER BY ordre, libelle;',
    '',
    'type_organisme_id::text',
    'type_organisme_libelle',
    'tous',
    true,
    'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
    0
),
(
    gen_random_uuid(),
    (SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code = 'ETAT_AVANCEMENT_ROP'),
    '#FILTRE_COMPLETION#',
    'Etat d''avancement',
    'SELECT ''3'' as id, ''Tous'' as libelle UNION
        SELECT ''2'' as id, ''Incomplet''  as libelle
         UNION SELECT ''1''  as id,  ''Complet''  as libelle;',
    '',
    '''3''',
    '''Tous''',
    '3',
    true,
    'SELECT_INPUT'::remocra.type_parametre_rapport_courrier,
    1
)
;
