INSERT INTO rapport_personnalise_parametre (
    rapport_personnalise_parametre_id,
    rapport_personnalise_parametre_rapport_personnalise_id,
    rapport_personnalise_parametre_code,
    rapport_personnalise_parametre_libelle,
    rapport_personnalise_parametre_source_sql,
    rapport_personnalise_parametre_description,
    rapport_personnalise_parametre_source_sql_id,
    rapport_personnalise_parametre_source_sql_libelle,
    rapport_personnalise_parametre_valeur_defaut,
    rapport_personnalise_parametre_is_required,
    rapport_personnalise_parametre_type,
    rapport_personnalise_parametre_ordre
)
SELECT
    gen_random_uuid(),
    rp.rapport_personnalise_id,
    '#NATURE_DECI#',
    'Nature DECI',
    'SELECT * FROM (
       SELECT ''tous'' as id, ''Tous'' as libelle, 0 as ordre
       UNION
       SELECT     nature_deci_id::text as id, nature_deci_libelle as libelle    , 1 as ordre
       FROM remocra.nature_deci WHERE nature_deci_actif
     ) t
     ORDER BY ordre, libelle;',
    '',
    'nature_deci_id::text',
    'nature_deci_libelle',
    'tous',
    true,
    'SELECT_INPUT',
    2
FROM rapport_personnalise rp
WHERE rp.rapport_personnalise_code IN ('ETAT_AVANCEMENT_CONTROLE', 'PEI_COMMUNE');


-- requete avec DECI pour commune
UPDATE rapport_personnalise
SET rapport_personnalise_source_sql = $sql$
select
    p.pei_numero_complet as "Numéro",
    v.voie_libelle as "Voie",
    v2.voie_libelle as "Carrefour",
    ld.lieu_dit_libelle as "Lieu dit",
    commune.commune_libelle as "Commune",
    St_X(p.pei_geometrie)::integer as "X",
    St_Y(p.pei_geometrie)::integer as "Y",
    n.nature_libelle as "Nature",
    nd.nature_deci_libelle as "Nature DECI",
    d.domaine_libelle as "Domaine",
    case
        p.pei_disponibilite_terrestre
        when 'DISPONIBLE' then 'Disponible'
        when 'INDISPONIBLE' then 'Indisponible'
        else 'Non conforme'
        end as "Disponibilité",
    to_char(v_psd.last_ctp, 'dd/mm/yyyy') as "Contrôle",
    to_char(v_psd.last_reco_init, 'dd/mm/yyyy') as "Reconnaissance"
from
    remocra.pei p
        join remocra.commune commune
             on commune.commune_id = p.pei_commune_id
        left join remocra.voie v
                  on v.voie_id = p.pei_voie_id
        left join remocra.voie v2
                  on v2.voie_id = p.pei_croisement_id
        left join remocra.lieu_dit ld
                  on ld.lieu_dit_id = p.pei_lieu_dit_id
        left join remocra.nature n
                  on n.nature_id = p.pei_nature_id
        left join remocra.nature_deci nd
                  on nd.nature_deci_id = p.pei_nature_deci_id
        left join remocra.domaine d
                  on d.domaine_id = p.pei_domaine_id
        left join remocra.v_pei_visite_date v_psd
                  on v_psd.pei_id = p.pei_id
where
    ('COMMUNE' = 'null'
        or p.pei_commune_id::text = 'COMMUNE')
  and ('DISPONIBILITE_CODE' = 'null'
    or p.pei_disponibilite_terrestre::text = 'DISPONIBILITE_CODE')
  and ('#NATURE_DECI#' = 'tous'
    or p.pei_nature_deci_id::text = '#NATURE_DECI#');
$sql$
WHERE rapport_personnalise_code = 'PEI_COMMUNE';

-- requete avec DECI pour ETAT_AVANCEMENT_CONTROLE
UPDATE rapport_personnalise
SET rapport_personnalise_source_sql = $sql$WITH RECURSIVE organismes(organisme_id) AS (
         SELECT o.organisme_id FROM remocra.organisme o JOIN remocra.zone_integration zc ON o.organisme_zone_integration_id = zc.zone_integration_id WHERE zc.zone_integration_id = #ZONE_COMPETENCE_ID#
       UNION ALL
         SELECT o.organisme_id
         FROM organismes os, remocra.organisme o
         WHERE o.organisme_parent_id = os.organisme_id
     ),
     param_prive AS (
       SELECT parametre_valeur || ' days' AS valeur
       FROM remocra.parametre
       WHERE parametre_code  = 'PEI_RENOUVELLEMENT_CTRL_PRIVE'
     ),
     param_public AS (
       SELECT parametre_valeur || ' days' AS valeur
       FROM remocra.parametre
       WHERE parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PUBLIC'
     ),
     param_icpe AS (
            SELECT parametre_valeur || ' days' AS valeur
            FROM remocra.parametre
            WHERE parametre_code  = 'PEI_RENOUVELLEMENT_CTRL_ICPE'
     ),
      param_icpe_conv AS (
                 SELECT parametre_valeur || ' days' AS valeur
                 FROM remocra.parametre
                 WHERE parametre_code  = 'PEI_RENOUVELLEMENT_CTRL_ICPE_CONVENTIONNE'
          ),
     date_reception AS (
       SELECT hv.visite_pei_id AS pei_id, hv.visite_date
       FROM remocra.visite hv
        WHERE hv.visite_type_visite = 'RECEPTION'
     ),
     der_date_ctp AS (
      SELECT hv.visite_pei_id AS pei_id, max(hv.visite_date) AS visite_date
       FROM remocra.visite hv
        WHERE hv.visite_type_visite = 'CTP'
        GROUP BY hv.visite_pei_id
     ),
     pei_select as (
      select * from remocra.pei
      where pei_nature_deci_id::text = '#NATURE_DECI#' or '#NATURE_DECI#' = 'tous'
     ),
     etat_pei AS (
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
       FROM pei_select pei
         JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND (thnd.nature_deci_code = 'PUBLIC' OR thnd.nature_deci_code = 'CONVENTIONNE')
         JOIN param_public pp ON 1 = 1
         LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
         LEFT JOIN der_date_ctp ddc ON ddc.pei_id = pei.pei_id
       UNION
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
       FROM pei_select pei
         JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND thnd.nature_deci_code = 'PRIVE'
         JOIN param_prive pp ON 1 = 1
         LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
         LEFT JOIN der_date_ctp ddc ON ddc.pei_id = pei.pei_id
       UNION
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
          FROM pei_select pei
            JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND thnd.nature_deci_code = 'ICPE'
            JOIN param_icpe pp ON 1 = 1
            LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
            LEFT JOIN der_date_ctp ddc ON ddc.pei_id = pei.pei_id
       UNION
       SELECT pei.pei_id,
       COALESCE(dc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) OR COALESCE(ddc.visite_date > current_timestamp - CAST(PP.valeur AS interval), false) AS datectrl_ok
         FROM pei_select pei
           JOIN remocra.nature_deci thnd ON thnd.nature_deci_id = pei.pei_nature_deci_id AND thnd.nature_deci_code = 'ICPE_CONVENTIONNE'
           JOIN param_icpe_conv pp ON 1 = 1
           LEFT JOIN date_reception dc ON dc.pei_id = pei.pei_id
           LEFT JOIN der_date_ctp ddc ON ddc.pei_id = pei.pei_id
     ),
     data AS (
       SELECT o.organisme_libelle AS "Nom de l'organisme",
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
SELECT "Nom de l'organisme", "Nom de la tournée", "Nombre de PEI dans la tournée", "Nombre de PEI visités", "Nombre de PEI non visités",  "% Visités"
FROM DATA
WHERE (organisme_type_organisme_id::text = '#TYPE_ORGANISME#' OR  'tous'= '#TYPE_ORGANISME#')
  AND  etat | '#FILTRE_COMPLETION#' = '#FILTRE_COMPLETION#';
$sql$
WHERE rapport_personnalise_code = 'ETAT_AVANCEMENT_CONTROLE';
