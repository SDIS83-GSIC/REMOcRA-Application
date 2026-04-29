DROP VIEW v_pei_visite_date ;
DROP VIEW v_pei_last_mesures;

CREATE MATERIALIZED VIEW remocra.v_pei_visite_date
AS WITH last_visites AS (
     SELECT visite.visite_pei_id AS pei_id,
        max(
            CASE
                WHEN visite.visite_type_visite = 'RECEPTION'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time zone
            END) AS last_reception,
        max(
            CASE
                WHEN visite.visite_type_visite = 'RECO_INIT'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time zone
            END) AS last_reco_init,
        max(
            CASE
                WHEN visite.visite_type_visite = 'CTP'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time zone
            END) AS last_ctp,
        max(
            CASE
                WHEN visite.visite_type_visite = 'ROP'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time zone
            END) AS last_recop,
        max(
            CASE
                WHEN visite.visite_type_visite = 'NP'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time zone
            END) AS last_np
       FROM visite
      GROUP BY visite.visite_pei_id
    ), param_mapping AS (
     SELECT t.nature_code,
        t.reco_param_code,
        t.ctrl_param_code
       FROM ( VALUES ('PRIVE'::text,'PEI_RENOUVELLEMENT_RECO_PRIVE'::text,'PEI_RENOUVELLEMENT_CTRL_PRIVE'::text), ('PUBLIC'::text,'PEI_RENOUVELLEMENT_RECO_PUBLIC'::text,'PEI_RENOUVELLEMENT_CTRL_PUBLIC'::text), ('CONVENTIONNE'::text,'PEI_RENOUVELLEMENT_RECO_CONVENTIONNE'::text,'PEI_RENOUVELLEMENT_CTRL_CONVENTIONNE'::text), ('ICPE'::text,'PEI_RENOUVELLEMENT_RECO_ICPE'::text,'PEI_RENOUVELLEMENT_CTRL_ICPE'::text), ('ICPE_CONVENTIONNE'::text,'PEI_RENOUVELLEMENT_RECO_ICPE_CONVENTIONNE'::text,'PEI_RENOUVELLEMENT_CTRL_ICPE_CONVENTIONNE'::text)) t(nature_code, reco_param_code, ctrl_param_code)
    ), param_values AS (
     SELECT param_mapping.nature_code AS nature_deci_code,
        p_reco.parametre_valeur::integer AS delta_days_reco,
        p_ctrl.parametre_valeur::integer AS delta_days_ctp
       FROM param_mapping
         LEFT JOIN parametre p_reco ON param_mapping.reco_param_code = p_reco.parametre_code
         LEFT JOIN parametre p_ctrl ON param_mapping.ctrl_param_code = p_ctrl.parametre_code
    )
    SELECT pei.pei_id,
           last_visites.last_reception,
           last_visites.last_reco_init,
           last_visites.last_ctp,
           last_visites.last_recop AS last_rop,
           last_visites.last_np,
           COALESCE(last_visites.last_recop, last_visites.last_reco_init) + ((p.delta_days_reco || ' day'::text)::interval) AS pei_next_rop,
        last_visites.last_ctp + ((p.delta_days_ctp || ' day'::text)::interval) AS pei_next_ctp
    FROM pei
        LEFT JOIN last_visites ON pei.pei_id = last_visites.pei_id
        JOIN nature_deci nd ON pei.pei_nature_deci_id = nd.nature_deci_id
        JOIN param_values p ON nd.nature_deci_code::text = p.nature_deci_code
    WITH DATA;


CREATE MATERIALIZED VIEW remocra.v_pei_last_mesures
AS WITH last_ctrl_debit_pression AS (
     SELECT visite_1.visite_pei_id AS pei_id,
        max(visite_1.visite_date) AS date
       FROM visite visite_1
         JOIN visite_ctrl_debit_pression vcdp ON vcdp.visite_ctrl_debit_pression_visite_id = visite_1.visite_id
      GROUP BY visite_1.visite_pei_id
    )
    SELECT DISTINCT ON (last_ctrl_debit_pression.pei_id)
        last_ctrl_debit_pression.pei_id,
        visite_ctrl_debit_pression.visite_ctrl_debit_pression_debit AS debit,
        visite_ctrl_debit_pression.visite_ctrl_debit_pression_pression AS pression,
        visite_ctrl_debit_pression.visite_ctrl_debit_pression_pression_dyn AS pression_dyn
    FROM last_ctrl_debit_pression
        JOIN visite ON visite.visite_pei_id = last_ctrl_debit_pression.pei_id AND visite.visite_date = last_ctrl_debit_pression.date
        JOIN visite_ctrl_debit_pression ON visite_ctrl_debit_pression.visite_ctrl_debit_pression_visite_id = visite.visite_id;
