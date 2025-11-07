--Modifie la vue v_pei_visite_date pour prendre en compte les natures DECI ICPE

CREATE OR REPLACE VIEW remocra.v_pei_visite_date
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
        ), param_values AS (
         SELECT nd_1.nature_deci_code,
                CASE
                    WHEN nd_1.nature_deci_code::text = 'PRIVE'::text THEN ( SELECT parametre.parametre_valeur::integer AS parametre_valeur
                       FROM parametre
                      WHERE parametre.parametre_code = 'PEI_RENOUVELLEMENT_RECO_PRIVE'::text)
                    WHEN nd_1.nature_deci_code::text = 'PUBLIC'::text OR nd_1.nature_deci_code::text = 'CONVENTIONNE'::text THEN ( SELECT parametre.parametre_valeur::integer AS parametre_valeur
                       FROM parametre
                      WHERE parametre.parametre_code = 'PEI_RENOUVELLEMENT_RECO_PUBLIC'::text)
                    WHEN nd_1.nature_deci_code::text = 'ICPE'::text THEN ( SELECT parametre.parametre_valeur::integer AS parametre_valeur
                        FROM parametre
                        WHERE parametre.parametre_code = 'PEI_RENOUVELLEMENT_RECO_ICPE'::text)
                    WHEN nd_1.nature_deci_code::text = 'ICPE_CONVENTIONNE'::text THEN ( SELECT parametre.parametre_valeur::integer AS parametre_valeur
                        FROM parametre
                        WHERE parametre.parametre_code = 'PEI_RENOUVELLEMENT_RECO_ICPE_CONVENTIONNE'::text)
                    ELSE NULL::integer
                END AS delta_days_reco,
                CASE
                    WHEN nd_1.nature_deci_code::text = 'PRIVE'::text THEN ( SELECT parametre.parametre_valeur::integer AS parametre_valeur
                       FROM parametre
                      WHERE parametre.parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PRIVE'::text)
                    WHEN nd_1.nature_deci_code::text = 'PUBLIC'::text OR nd_1.nature_deci_code::text = 'CONVENTIONNE'::text THEN ( SELECT parametre.parametre_valeur::integer AS parametre_valeur
                       FROM parametre
                      WHERE parametre.parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PUBLIC'::text)
                    WHEN nd_1.nature_deci_code::text = 'ICPE'::text THEN ( SELECT parametre.parametre_valeur::integer AS parametre_valeur
                       FROM parametre
                      WHERE parametre.parametre_code = 'PEI_RENOUVELLEMENT_CTRL_ICPE'::text)
                    WHEN nd_1.nature_deci_code::text = 'ICPE_CONVENTIONNE'::text THEN ( SELECT parametre.parametre_valeur::integer AS parametre_valeur
                       FROM parametre
                      WHERE parametre.parametre_code = 'PEI_RENOUVELLEMENT_CTRL_ICPE_CONVENTIONNE'::text)
                    ELSE NULL::integer
                END AS delta_days_ctp
           FROM nature_deci nd_1
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
     JOIN param_values p ON nd.nature_deci_code::text = p.nature_deci_code::text;
