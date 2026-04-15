-- Ajout des paramètres PEI_RENOUVELLEMENT_CTRL_CONVENTIONNE et PEI_RENOUVELLEMENT_RECO_CONVENTIONNE
---- Par défaut, le paramètre prendra la valeur des paramètres PUBLIC
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES
    (
        gen_random_uuid(),
        'PEI_RENOUVELLEMENT_CTRL_CONVENTIONNE',
        coalesce((SELECT parametre_valeur FROM remocra.parametre WHERE parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PUBLIC'), 365::text),
        'INTEGER'::remocra."TYPE_PARAMETRE"
    ),
    (
        gen_random_uuid(),
        'PEI_RENOUVELLEMENT_RECO_CONVENTIONNE',
        coalesce((SELECT parametre_valeur FROM remocra.parametre WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PUBLIC'), 365::text),
        'INTEGER'::remocra."TYPE_PARAMETRE"
    )
;

--Modifie la vue remocra.v_pei_visite_date pour prendre en compte les natures DECI CONVENTIONNE
CREATE OR REPLACE VIEW remocra.v_pei_visite_date AS
WITH last_visites AS (
  SELECT
    visite_pei_id AS pei_id,
    max(
      CASE
        WHEN visite_type_visite = 'RECEPTION'::"TYPE_VISITE" THEN visite_date
          ELSE NULL::TIMESTAMPTZ
      END
    ) AS last_reception,
    max(
      CASE
        WHEN visite_type_visite = 'RECO_INIT'::"TYPE_VISITE" THEN visite_date
        ELSE NULL::TIMESTAMPTZ
      END
    ) AS last_reco_init,
    max(
      CASE
        WHEN visite_type_visite = 'CTP'::"TYPE_VISITE" THEN visite_date
        ELSE NULL::TIMESTAMPTZ
      END
    ) AS last_ctp,
    max(
      CASE
        WHEN visite_type_visite = 'ROP'::"TYPE_VISITE" THEN visite_date
        ELSE NULL::TIMESTAMPTZ
      END
    ) AS last_recop,
    max(
      CASE
        WHEN visite_type_visite = 'NP'::"TYPE_VISITE" THEN visite_date
        ELSE NULL::TIMESTAMPTZ
      END
    ) AS last_np
  FROM visite
  GROUP BY visite_pei_id
), param_mapping AS (
  SELECT
    nature_code,
    reco_param_code,
    ctrl_param_code
  FROM (VALUES
    ('PRIVE', 'PEI_RENOUVELLEMENT_RECO_PRIVE', 'PEI_RENOUVELLEMENT_CTRL_PRIVE'),
    ('PUBLIC', 'PEI_RENOUVELLEMENT_RECO_PUBLIC', 'PEI_RENOUVELLEMENT_CTRL_PUBLIC'),
    ('CONVENTIONNE', 'PEI_RENOUVELLEMENT_RECO_CONVENTIONNE', 'PEI_RENOUVELLEMENT_CTRL_CONVENTIONNE'),
    ('ICPE', 'PEI_RENOUVELLEMENT_RECO_ICPE', 'PEI_RENOUVELLEMENT_CTRL_ICPE'),
    ('ICPE_CONVENTIONNE', 'PEI_RENOUVELLEMENT_RECO_ICPE_CONVENTIONNE', 'PEI_RENOUVELLEMENT_CTRL_ICPE_CONVENTIONNE')
  ) AS t(nature_code, reco_param_code, ctrl_param_code)
), param_values AS (
  SELECT
    nature_code AS nature_deci_code,
    p_reco.parametre_valeur::integer AS delta_days_reco,
    p_ctrl.parametre_valeur::integer AS delta_days_ctp
  FROM param_mapping
    LEFT JOIN parametre p_reco ON reco_param_code = p_reco.parametre_code
    LEFT JOIN parametre p_ctrl ON ctrl_param_code = p_ctrl.parametre_code
)
SELECT
    pei.pei_id,
    last_reception,
    last_reco_init,
    last_ctp,
    last_recop AS last_rop,
    last_np,
    COALESCE(last_recop, last_reco_init) + ((delta_days_reco || ' day'::text)::interval) AS pei_next_rop,
  last_ctp + ((delta_days_ctp || ' day'::text)::interval) AS pei_next_ctp
FROM pei
    LEFT JOIN last_visites ON pei.pei_id = last_visites.pei_id
    JOIN nature_deci nd ON pei.pei_nature_deci_id = nd.nature_deci_id
    JOIN param_values p ON nd.nature_deci_code::text = p.nature_deci_code::text;


-- Adapte les rapports personnalisés en conséquence :
---- ETAT_AVANCEMENT_ROP
UPDATE remocra.rapport_personnalise
SET rapport_personnalise_source_sql='WITH RECURSIVE organismes(organisme_id) AS (
    SELECT o.organisme_id FROM remocra.organisme o JOIN remocra.zone_integration zc ON o.organisme_zone_integration_id = zc.zone_integration_id WHERE zc.zone_integration_id = #ZONE_COMPETENCE_ID#
    UNION ALL
    SELECT o.organisme_id
    FROM organismes os, remocra.organisme o
    WHERE o.organisme_parent_id = os.organisme_id
), pei_with_compliance AS (
    SELECT
        l_tournee_pei.pei_id,
        l_tournee_pei.tournee_id,
        v_pvd.pei_next_rop > now() AS is_compliant
    FROM remocra.l_tournee_pei
        JOIN remocra.v_pei_visite_date v_pvd ON l_tournee_pei.pei_id = v_pvd.pei_id
        JOIN remocra.pei ON pei.pei_id = l_tournee_pei.pei_id
), data AS (
    SELECT
        o.organisme_libelle AS "Nom de l''organisme",
        tournee.tournee_libelle AS "Nom de la tournée",
        COUNT(pwc.pei_id) AS "Nombre de PEI dans la tournée",
        SUM(CASE WHEN pwc.is_compliant THEN 1 ELSE 0 END) AS "Nombre de PEI visités",
        COUNT(pwc.pei_id) - SUM(CASE WHEN pwc.is_compliant THEN 1 ELSE 0 END) AS "Nombre de PEI non visités",
        CAST(CAST(SUM(CASE WHEN pwc.is_compliant THEN 1 ELSE 0 END) AS numeric) /
            CAST(COUNT(pwc.pei_id) AS numeric) * 100 AS numeric(5,2)) AS "% Visités",
	    organisme_type_organisme_id,
	    CASE WHEN (COUNT(pwc.pei_id) - SUM(CASE WHEN pwc.is_compliant THEN 1 ELSE 0 END)) = 0 THEN 1 ELSE 2 END AS etat
    FROM remocra.tournee
        JOIN pei_with_compliance pwc ON pwc.tournee_id = tournee.tournee_id
        JOIN remocra.organisme o ON o.organisme_id = tournee.tournee_organisme_id
    WHERE o.organisme_id IN (SELECT organisme_id FROM organismes)
    GROUP BY o.organisme_libelle, tournee.tournee_libelle, organisme_type_organisme_id
    ORDER BY o.organisme_libelle, tournee.tournee_libelle
)
SELECT
    "Nom de l''organisme",
    "Nom de la tournée",
    "Nombre de PEI dans la tournée",
    "Nombre de PEI visités",
    "Nombre de PEI non visités",
    "% Visités"
FROM DATA
WHERE (organisme_type_organisme_id::text = ''#TYPE_ORGANISME#'' OR  ''tous''= ''#TYPE_ORGANISME#'')
    AND etat | ''#FILTRE_COMPLETION#'' = ''#FILTRE_COMPLETION#'';'
WHERE rapport_personnalise_code='ETAT_AVANCEMENT_ROP';

---- ETAT_AVANCEMENT_CONTROLE
UPDATE remocra.rapport_personnalise
SET rapport_personnalise_source_sql='WITH RECURSIVE organismes(organisme_id) AS (
    SELECT o.organisme_id FROM remocra.organisme o JOIN remocra.zone_integration zc ON o.organisme_zone_integration_id = zc.zone_integration_id WHERE zc.zone_integration_id = #ZONE_COMPETENCE_ID#
    UNION ALL
    SELECT o.organisme_id
    FROM organismes os, remocra.organisme o
    WHERE o.organisme_parent_id = os.organisme_id
), pei_with_compliance AS (
    SELECT
        l_tournee_pei.pei_id,
        l_tournee_pei.tournee_id,
        v_pvd.pei_next_ctp > now() AS is_compliant
    FROM remocra.l_tournee_pei
             JOIN remocra.v_pei_visite_date v_pvd ON l_tournee_pei.pei_id = v_pvd.pei_id
             JOIN remocra.pei ON pei.pei_id = l_tournee_pei.pei_id
    WHERE pei_nature_deci_id::text = ''#NATURE_DECI#'' or ''#NATURE_DECI#'' = ''tous''
    ), data AS (
SELECT
    o.organisme_libelle AS "Nom de l''organisme",
    tournee.tournee_libelle AS "Nom de la tournée",
    COUNT(pwc.pei_id) AS "Nombre de PEI dans la tournée",
    SUM(CASE WHEN pwc.is_compliant THEN 1 ELSE 0 END) AS "Nombre de PEI visités",
    COUNT(pwc.pei_id) - SUM(CASE WHEN pwc.is_compliant THEN 1 ELSE 0 END) AS "Nombre de PEI non visités",
    CAST(CAST(SUM(CASE WHEN pwc.is_compliant THEN 1 ELSE 0 END) AS numeric) /
    CAST(COUNT(pwc.pei_id) AS numeric) * 100 AS numeric(5,2)) AS "% Visités",
	organisme_type_organisme_id,
	CASE WHEN (COUNT(pwc.pei_id) - SUM(CASE WHEN pwc.is_compliant THEN 1 ELSE 0 END)) = 0 THEN 1 ELSE 2 END AS etat
FROM remocra.tournee
    JOIN pei_with_compliance pwc ON pwc.tournee_id = tournee.tournee_id
    JOIN remocra.organisme o ON o.organisme_id = tournee.tournee_organisme_id
WHERE o.organisme_id IN (SELECT organisme_id FROM organismes)
GROUP BY o.organisme_libelle, tournee.tournee_libelle, organisme_type_organisme_id
ORDER BY o.organisme_libelle, tournee.tournee_libelle
    )
SELECT
    "Nom de l''organisme",
    "Nom de la tournée",
    "Nombre de PEI dans la tournée",
    "Nombre de PEI visités",
    "Nombre de PEI non visités",
    "% Visités"
FROM DATA
WHERE (organisme_type_organisme_id::text = ''#TYPE_ORGANISME#'' OR  ''tous''= ''#TYPE_ORGANISME#'')
  AND etat | ''#FILTRE_COMPLETION#'' = ''#FILTRE_COMPLETION#'';'
WHERE rapport_personnalise_code='ETAT_AVANCEMENT_CONTROLE';
